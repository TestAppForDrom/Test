package com.drom.test.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import io.realm.Realm;

import javax.inject.Inject;

public class DromTestApplication extends Application implements HasActivityInjector {
    private static DromTestApplication instance;
    @Inject DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;
    private DromTestComponent component;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerDromTestComponent
                .builder()
                .context(this)
                .build();
        component.inject(this);
        Realm.init(this);
    }

    public static DromTestComponent getComponent() {
        return instance.component;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }
}
