package com.drom.test.features.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import com.drom.test.model.RepositoryModel;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class RepositoryAdapter extends RealmRecyclerViewAdapter<RepositoryModel, HolderRepositoryItem> {

    public RepositoryAdapter(@Nullable OrderedRealmCollection<RepositoryModel> data) {
        super(data, true);
    }

    @NonNull
    @Override
    public HolderRepositoryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return HolderRepositoryItem.createHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRepositoryItem holder, int position) {
        holder.bindView(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull HolderRepositoryItem holder) {
        holder.onDetached();
    }
}
