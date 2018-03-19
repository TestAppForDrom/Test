package com.drom.test.beans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import io.reactivex.Completable;

public interface SessionManager {

    boolean hasAccount();

    boolean isUnauthorizedAccount();

    @NonNull
    Bundle addAccount(@NonNull String login, @NonNull String password, @Nullable String authToken);

    @Nullable
    Pair<String, String> getLoginPassword();

    Completable logOut();
}
