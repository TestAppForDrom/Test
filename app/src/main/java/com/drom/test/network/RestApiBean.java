package com.drom.test.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.drom.test.model.entity.SearchResponseEntity;
import com.drom.test.model.entity.UserEntity;
import io.reactivex.Flowable;

public interface RestApiBean {

    Flowable<UserEntity> login(@NonNull String name, @NonNull String password);

    Flowable<SearchResponseEntity> search(@Nullable String query, int page, int pageSize);
}
