package com.drom.test.features.search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.drom.test.R;
import com.drom.test.application.GlideApp;
import com.drom.test.model.RepositoryModel;

public class HolderRepositoryItem extends RecyclerView.ViewHolder {
    @BindView(R.id.image_avatar) ImageView avatar;
    @BindView(R.id.text_username) TextView userName;
    @BindView(R.id.text_repo_name) TextView repoName;
    @BindView(R.id.text_repo_description) TextView repoDescription;
    @BindView(R.id.hint_repo_description) View hintDescription;

    private HolderRepositoryItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(RepositoryModel model) {
        userName.setText(model.getOwnerName());
        repoName.setText(model.getName());
        if (TextUtils.isEmpty(model.getDescription())) {
            hintDescription.setVisibility(View.GONE);
            repoDescription.setVisibility(View.GONE);
        } else {
            hintDescription.setVisibility(View.VISIBLE);
            repoDescription.setVisibility(View.VISIBLE);
            repoDescription.setText(model.getDescription());
        }
        GlideApp.with(avatar).clear(avatar);
        if (model.getOwnerAvatar() != null) {
            GlideApp.with(avatar)
                    .load(model.getOwnerAvatar())
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(avatar);
        } else {
            avatar.setBackgroundColor(avatar.getResources().getColor(R.color.colorAccent));
        }
    }

    public void onDetached() {
        GlideApp.with(avatar).clear(avatar);
    }

    public static HolderRepositoryItem createHolder(@NonNull ViewGroup parent) {
        return new HolderRepositoryItem(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holder_repository_item, parent, false));
    }
}
