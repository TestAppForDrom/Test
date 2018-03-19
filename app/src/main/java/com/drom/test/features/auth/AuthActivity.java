package com.drom.test.features.auth;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.drom.test.R;
import com.drom.test.beans.SessionManager;
import com.drom.test.features.search.SearchActivity;
import com.drom.test.network.RestApiBean;
import dagger.Lazy;
import dagger.android.AndroidInjection;

import javax.inject.Inject;

public class AuthActivity extends AuthenticatorActivity {
    public static final String EXTRA_TOKEN_TYPE = "com.drom.test.features.auth.AUTH_TOKEN";
    @Inject Lazy<SessionManager> sessionManager;
    @Inject RestApiBean restApiBean;
    @BindView(R.id.text_login) EditText etLogin;
    @BindView(R.id.text_password) EditText etPassword;
    @BindView(R.id.button_login) View btnLogin;
    @BindView(R.id.button_unauthorized) View btnLoginUnauthorized;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    public static Intent createIntent(Context callingContext) {
        return new Intent(callingContext, AuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle icicle) {
        AndroidInjection.inject(this);
        super.onCreate(icicle);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        AuthViewModel viewModel = ViewModelProviders.of(this).get(AuthViewModel.class);

        viewModel.loginLiveData().observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.inProgress) {
                showProgress();
            } else if (event.error != null) {
                hideProgress();
                showError(event.error);
            } else if (event.data != null) {
                hideProgress();
                onLoginSuccess(etLogin.getText().toString(), etPassword.getText().toString());
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etPassword.getText()) && !TextUtils.isEmpty(etLogin.getText())) {
                viewModel.login(etLogin.getText().toString(), etPassword.getText().toString());
            }
        });

        btnLoginUnauthorized.setOnClickListener(v -> onEnterUnauthorized());
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        etLogin.setEnabled(false);
        etPassword.setEnabled(false);
        btnLogin.setEnabled(false);
        btnLoginUnauthorized.setEnabled(false);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        etLogin.setEnabled(true);
        etPassword.setEnabled(true);
        btnLogin.setEnabled(true);
        btnLoginUnauthorized.setEnabled(true);
    }

    private void showError(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void onLoginSuccess(String login, String password) {
        Bundle result = sessionManager.get().addAccount(login, password, "");
        setAccountAuthenticatorResult(result);
        setResult(Activity.RESULT_OK);
        startActivity(SearchActivity.createIntent(this));
        finish();
    }

    private void onEnterUnauthorized() {
        onLoginSuccess(DromTestAccount.UNAUTHORIZED, DromTestAccount.UNAUTHORIZED);
    }
}
