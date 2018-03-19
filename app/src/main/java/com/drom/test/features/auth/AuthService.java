package com.drom.test.features.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AuthService extends Service {
    private AppAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new AppAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
