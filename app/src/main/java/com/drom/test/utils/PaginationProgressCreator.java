package com.drom.test.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.drom.test.R;


public interface PaginationProgressCreator {
    /**
     * Create new loading list item {@link android.support.v7.widget.RecyclerView.ViewHolder}.
     *
     * @param parent   parent ViewGroup.
     * @param viewType type of the loading list item.
     * @return ViewHolder that will be used as loading list item.
     */
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * Bind the loading list item.
     *
     * @param holder   loading list item ViewHolder.
     * @param position loading list item position.
     */
    void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

    PaginationProgressCreator DEFAULT = new PaginationProgressCreator() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PaginationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pagination_progress, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // No binding for default loading row
        }
    };

    final class PaginationHolder extends RecyclerView.ViewHolder {

        private PaginationHolder(View itemView) {
            super(itemView);
        }
    }
}
