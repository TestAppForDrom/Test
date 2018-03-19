package com.drom.test.network.usecase;

import android.content.Context;
import com.drom.test.application.DromTestApplication;
import com.drom.test.network.RestApiBean;
import dagger.Lazy;

import javax.inject.Inject;

public abstract class BaseUseCase {
    @Inject protected Lazy<Context> appContext;
    @Inject protected RestApiBean restApiBean;

    protected BaseUseCase() {
        DromTestApplication.getComponent().inject(this);
    }
}
