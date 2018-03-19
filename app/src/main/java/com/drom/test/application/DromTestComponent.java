package com.drom.test.application;

import android.content.Context;
import com.drom.test.features.auth.AppAuthenticator;
import com.drom.test.features.auth.AuthViewModel;
import com.drom.test.features.search.SearchUseCase;
import com.drom.test.features.search.SearchViewModel;
import com.drom.test.network.usecase.BaseUseCase;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ActivityBuilder.class, DromTestAppModule.class})
public interface DromTestComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder context(Context appContext);

        DromTestComponent build();
    }

    void inject(DromTestApplication app);

    void inject(AppAuthenticator authenticator);

    void inject(AuthViewModel viewModel);

    void inject(SearchViewModel viewModel);

    void inject(BaseUseCase useCase);

    void inject(SearchUseCase useCase);
}
