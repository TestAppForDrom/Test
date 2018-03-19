package com.drom.test.features.auth;

import android.accounts.Account;
import android.os.Parcel;

public class DromTestAccount extends Account {
    public static final String TYPE = "com.drom.test.auth";
    public static final String TOKEN_FULL_ACCESS = TYPE + ".TOKEN_FULL_ACCESS";
    public static final String KEY_PASSWORD = TYPE + ".KEY_PASSWORD";
    public static final String UNAUTHORIZED = TYPE + ".UNAUTHORIZED";

    public DromTestAccount(String name) {
        super(name, TYPE);
    }

    public DromTestAccount(Parcel in) {
        super(in);
    }
}
