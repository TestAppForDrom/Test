package com.drom.test.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.lang.ref.WeakReference;

public class RvWrapperPaginator extends RecyclerView.OnScrollListener {
    private final PublishSubject<Integer> nextPageSubject;
    private final WeakReference<RecyclerView> recyclerViewRef;
    private final int paginationOffsetCount;

    public RvWrapperPaginator(@NonNull RecyclerView recyclerView, int paginationOffsetCount) {
        recyclerViewRef = new WeakReference<>(recyclerView);
        nextPageSubject = PublishSubject.create();
        this.paginationOffsetCount = paginationOffsetCount;
    }

    @NonNull
    public Observable<Integer> getObservable() {
        return nextPageSubject
                .doOnSubscribe(ignored -> initListener())
                .distinctUntilChanged()
                .doOnDispose(this::removeListener);
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        RecyclerView.LayoutManager lm = rv.getLayoutManager();
        if (lm != null) {
            int visibleItemCount = lm.getChildCount();
            int totalItemCount = lm.getItemCount();
            int firstVisibleItemPosition = findFirstVisibleItemPosition(lm);

            if (totalItemCount < paginationOffsetCount || firstVisibleItemPosition + visibleItemCount < totalItemCount - paginationOffsetCount) {
                return;
            }
            RecyclerView w = recyclerViewRef.get();
            if (w != null && w.getAdapter() != null) {
                if (w.getAdapter() instanceof PaginationAdapterWrapper) {
                    nextPageSubject.onNext(((PaginationAdapterWrapper) w.getAdapter()).getWrappedAdapter().getItemCount());
                } else {
                    nextPageSubject.onNext(w.getAdapter().getItemCount());
                }
            }
        }
    }

    private static int findFirstVisibleItemPosition(RecyclerView.LayoutManager lm) {
        if (lm instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) lm).findFirstVisibleItemPositions(null)[0];
        }
        throw new IllegalStateException("Type of layoutManager unknown. In this case this method needs to be overridden");
    }

    private void initListener() {
        RecyclerView rv = recyclerViewRef.get();
        if (rv != null) {
            rv.addOnScrollListener(this);
        }
    }

    private void removeListener() {
        RecyclerView rv = recyclerViewRef.get();
        if (rv != null) {
            rv.removeOnScrollListener(this);
        }
    }
}
