package com.drom.test.features.search;

import android.support.annotation.NonNull;
import com.drom.test.application.DromTestApplication;
import com.drom.test.beans.RealmDataStorage;
import com.drom.test.model.SearchResponseModel;
import com.drom.test.model.mapper.SearchResponseMapper;
import com.drom.test.network.usecase.UseCase;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;

public class SearchUseCase extends UseCase<SearchResponseModel> {
    private final String query;
    private final int page;
    private final int pageSize;
    @Inject RealmDataStorage dataStorage;

    public SearchUseCase(String query, int page, int pageSize) {
        DromTestApplication.getComponent().inject(this);
        this.query = query;
        this.page = page;
        this.pageSize = pageSize;
    }

    @NonNull
    @Override
    protected Flowable<SearchResponseModel> buildUseCaseObservable() {
        return restApiBean.search(query, page, pageSize)
                .subscribeOn(Schedulers.io())
                .map(response -> new SearchResponseMapper().mapTo(response))
                .doOnNext(data -> {
                    if (page == 1) {
                        dataStorage.insertRepositories(data);
                    } else {
                        dataStorage.insertRepositoriesNextPage(data);
                    }
                });
    }
}
