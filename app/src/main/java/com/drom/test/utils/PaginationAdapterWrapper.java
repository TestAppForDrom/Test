package com.drom.test.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class PaginationAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = Integer.MAX_VALUE - 50;
    private boolean displayPaginationProgressBar;
    private final RecyclerView.Adapter inner;
    private final PaginationProgressCreator paginationProgressCreator;

    private PaginationAdapterWrapper(@NonNull RecyclerView.Adapter inner, @NonNull PaginationProgressCreator paginationProgressCreator) {
        this.inner = inner;
        this.paginationProgressCreator = paginationProgressCreator;
        inner.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(positionStart, itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyDataSetChanged();
            }
        });
    }

    public static PaginationAdapterWrapper wrap(RecyclerView.Adapter adapter, PaginationProgressCreator paginationProgressCreator) {
        return new PaginationAdapterWrapper(adapter, paginationProgressCreator);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PROGRESS) {
            return paginationProgressCreator.onCreateViewHolder(parent, viewType);
        } else {
            return inner.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isLoadingRow(position)) {
            paginationProgressCreator.onBindViewHolder(holder, position);
        } else {
            inner.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        if (!displayPaginationProgressBar && inner.getItemCount() == 0) {
            return 0;
        }
        return displayPaginationProgressBar ? inner.getItemCount() + 1 : inner.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadingRow(position)) {
            return TYPE_PROGRESS;
        } else {
            return inner.getItemViewType(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return isLoadingRow(position) ? RecyclerView.NO_ID : inner.getItemId(position);
    }

    private boolean isLoadingRow(int position) {
        return displayPaginationProgressBar && position == getPaginationProgressBarPosition();
    }

    private int getPaginationProgressBarPosition() {
        return displayPaginationProgressBar ? getItemCount() - 1 : -1;
    }

    public void displayPaginationProgressBar(boolean display) {
        if (displayPaginationProgressBar != display) {
            displayPaginationProgressBar = display;
            notifyDataSetChanged();
        }
    }

    public boolean isPaginationProgressBarVisible() {
        return displayPaginationProgressBar;
    }

    public RecyclerView.Adapter getWrappedAdapter() {
        return inner;
    }

    public void setAllDataLoaded(boolean loaded) {
        displayPaginationProgressBar(!loaded);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        inner.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        inner.onDetachedFromRecyclerView(recyclerView);
    }
}
