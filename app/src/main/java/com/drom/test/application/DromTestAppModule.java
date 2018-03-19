package com.drom.test.application;

import android.content.Context;
import com.drom.test.beans.DromAccountManager;
import com.drom.test.beans.RealmDataStorage;
import com.drom.test.beans.SessionManager;
import com.drom.test.network.RestApiBean;
import com.drom.test.network.RetrofitRestApi;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DromTestAppModule {

    @Provides
    @Singleton
    public static SessionManager provideSessionManager(Context context) {
        return new DromAccountManager(context);
    }

    @Provides
    @Singleton
    public static RestApiBean provideRestApi(Context context, SessionManager sessionManager) {
        return new RetrofitRestApi(context, sessionManager);
    }

    @Provides
    @Singleton
    public static RealmDataStorage provideRealmStorage() {
        return new RealmDataStorage();
    }
}
