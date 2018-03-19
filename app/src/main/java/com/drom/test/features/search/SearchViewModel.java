package com.drom.test.features.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.drom.test.R;
import com.drom.test.application.DromTestApplication;
import com.drom.test.beans.RealmDataStorage;
import com.drom.test.model.RepositoryModel;
import com.drom.test.network.RetrofitException;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.realm.OrderedRealmCollection;

import javax.inject.Inject;
import java.net.HttpURLConnection;

public class SearchViewModel extends AndroidViewModel {
    public static final int PAGE_SIZE = 50;
    private final PublishSubject<String> searchSubject = PublishSubject.create();
    private final PublishSubject<Pair<String, Integer>> nextPageSubject = PublishSubject.create();
    private final CompositeDisposable searchDisposable = new CompositeDisposable();
    @Inject RealmDataStorage dataStorage;
    private MutableLiveData<SearchViewObject> searchLiveData;
    private MutableLiveData<SearchViewObject> nextPageLiveData;
    private MutableLiveData<OrderedRealmCollection<RepositoryModel>> reposLiveData;
    private MediatorLiveData<SearchViewObject> compoundData;

    public SearchViewModel(Application application) {
        super(application);
        DromTestApplication.getComponent().inject(this);
        searchInternal();
        loadNextPageInternal();
    }

    @NonNull
    public LiveData<OrderedRealmCollection<RepositoryModel>> getReposData() {
        if (reposLiveData == null) {
            reposLiveData = new MutableLiveData<>();
            reposLiveData.setValue(dataStorage.getRepositories().getRepos());
        }
        return reposLiveData;
    }

    @NonNull
    public LiveData<SearchViewObject> getObservable() {
        if (compoundData == null) {
            compoundData = new MediatorLiveData<>();
            searchLiveData = new MutableLiveData<>();
            nextPageLiveData = new MutableLiveData<>();
            compoundData.addSource(searchLiveData, compoundData::setValue);
            compoundData.addSource(nextPageLiveData, compoundData::setValue);
        }
        return compoundData;
    }

    public void search(@NonNull String query) {
        searchSubject.onNext(query);
    }

    private void searchInternal() {
        searchDisposable.add(
                searchSubject.toFlowable(BackpressureStrategy.DROP)
                        .distinctUntilChanged()
                        .switchMap(this::toSearchObservable)
                        .subscribe(result -> searchLiveData.setValue(result))
        );
    }

    private Flowable<SearchViewObject> toSearchObservable(@NonNull String query) {
        if (query.isEmpty()) {
            dataStorage.clear();
            return Flowable.just(SearchViewObject.success());
        } else {
            return new SearchUseCase(query, 1, PAGE_SIZE)
                    .execute()
                    .map(ignored -> SearchViewObject.success())
                    .startWith(SearchViewObject.progress())
                    .onErrorResumeNext(this::toSearchError)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    public void loadNextPage(@NonNull String query, int currentCount) {
        nextPageSubject.onNext(new Pair<>(query, currentCount));
    }

    private void loadNextPageInternal() {
        searchDisposable.add(
                nextPageSubject.toFlowable(BackpressureStrategy.DROP)
                        .switchMap(pair -> toNextPageObservable(pair.first, pair.second))
                        .subscribe(result -> nextPageLiveData.setValue(result))
        );
    }

    private Flowable<SearchViewObject> toNextPageObservable(@NonNull String query, int currentCount) {
        return new SearchUseCase(query, calculatePageNumber(currentCount), PAGE_SIZE)
                .execute()
                .map(ignored -> SearchViewObject.success())
                .startWith(SearchViewObject.paginationProgress())
                .onErrorResumeNext(this::toSearchError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Flowable<SearchViewObject> toSearchError(Throwable error) {
        if (error instanceof RetrofitException && ((RetrofitException) error).getResponse() != null) {
            int code = ((RetrofitException) error).getResponse().code();
            if (code == HttpURLConnection.HTTP_FORBIDDEN) {
                return Flowable.just(SearchViewObject.error(getApplication().getString(R.string.error_api_rate_limit)));
            }
        }
        return Flowable.just(SearchViewObject.error(getApplication().getString(R.string.error_unknown)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        searchDisposable.clear();
    }

    private static int calculatePageNumber(int currentCount) {
        return currentCount / PAGE_SIZE + 1;
    }
}
