package com.drom.test.features.auth;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AppAuthenticator extends AbstractAccountAuthenticator {
    private final Context context;

    public AppAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = AuthActivity.createIntent(context)
                .putExtra(AuthActivity.EXTRA_TOKEN_TYPE, accountType)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        Bundle result = new Bundle();
        if (options != null) {
            result.putAll(options);
        }
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
