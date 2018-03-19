package com.drom.test.beans;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.drom.test.features.auth.DromTestAccount;
import io.reactivex.Completable;

public class DromAccountManager implements SessionManager {
    private final Context context;

    public DromAccountManager(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public boolean hasAccount() {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(DromTestAccount.TYPE);
        return accounts.length > 0;
    }

    @Override
    public boolean isUnauthorizedAccount() {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(DromTestAccount.TYPE);
        if (accounts.length > 0) {
            Account account = accounts[0];
            return DromTestAccount.UNAUTHORIZED.equals(account.name);
        }
        return true;
    }

    @Override
    @NonNull
    public Bundle addAccount(@NonNull String login, @NonNull String password, @Nullable String authToken) {
        AccountManager am = AccountManager.get(context);
        Bundle result = new Bundle();
        DromTestAccount account;
        if (am.addAccountExplicitly(account = new DromTestAccount(login), password, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            am.setAuthToken(account, account.type, authToken);
        }
        return result;
    }

    @Nullable
    @Override
    public Pair<String, String> getLoginPassword() {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(DromTestAccount.TYPE);
        if (accounts.length > 0) {
            Account account = accounts[0];
            return new Pair<>(account.name, am.getPassword(account));
        }
        return null;
    }

    @Override
    public Completable logOut() {
        return Completable.create(source -> {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(DromTestAccount.TYPE);
            if (accounts.length > 0) {
                Account account = accounts[0];
                am.removeAccount(account, future -> source.onComplete(), new Handler(Looper.myLooper()));
            } else {
                source.onError(new IllegalStateException("There's no accounts to remove"));
            }
        });
    }
}
