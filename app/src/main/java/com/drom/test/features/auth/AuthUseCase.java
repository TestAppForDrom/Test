package com.drom.test.features.auth;

import android.support.annotation.NonNull;
import com.drom.test.model.entity.UserEntity;
import com.drom.test.network.usecase.UseCase;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class AuthUseCase extends UseCase<UserEntity> {
    private final String username;
    private final String password;

    public AuthUseCase(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }

    @NonNull
    @Override
    protected Flowable<UserEntity> buildUseCaseObservable() {
        return restApiBean.login(username, password)
                .subscribeOn(Schedulers.io());
    }
}
