package com.drom.test.application;

import com.drom.test.features.auth.AuthActivity;
import com.drom.test.features.search.SearchActivity;
import com.drom.test.features.start.ActivityStart;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract ActivityStart bindStartActivity();

    @ContributesAndroidInjector
    abstract AuthActivity bindAuthActivity();

    @ContributesAndroidInjector
    abstract SearchActivity bindMainActivity();
}
