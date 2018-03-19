package com.drom.test.model.mapper;

import android.support.annotation.NonNull;
import com.drom.test.model.RepositoryModel;
import com.drom.test.model.entity.RepositoryEntity;
import com.drom.test.model.entity.UserEntity;

public class RepositoryMapper extends BaseMapper<RepositoryModel, RepositoryEntity> {

    @NonNull
    @Override
    public RepositoryModel mapTo(RepositoryEntity entity) {
        RepositoryModel result = new RepositoryModel()
                .setId(entity.getId())
                .setName(entity.getName())
                .setDescription(entity.getDescription())
                .setUrl(entity.getUrl());
        UserEntity userEntity = entity.getOwner();
        if (userEntity != null) {
            result.setOwnerName(userEntity.getLogin())
                    .setOwnerAvatar(userEntity.getAvatarUrl());
        }
        return result;
    }
}
