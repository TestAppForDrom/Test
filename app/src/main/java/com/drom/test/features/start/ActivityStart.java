package com.drom.test.features.start;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.drom.test.beans.SessionManager;
import com.drom.test.features.auth.AuthActivity;
import com.drom.test.features.search.SearchActivity;
import dagger.android.AndroidInjection;

import javax.inject.Inject;

public class ActivityStart extends AppCompatActivity {
    @Inject SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        if (sessionManager.hasAccount()) {
            startActivity(SearchActivity.createIntent(this));
        } else {
            startActivity(AuthActivity.createIntent(this));
        }
        finish();
    }
}
