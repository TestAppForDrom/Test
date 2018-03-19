package com.drom.test.beans;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import com.drom.test.model.SearchResponseModel;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDataStorage {
    private final RealmConfiguration realmConfiguration;
    private Realm realm;

    public RealmDataStorage() {
        realmConfiguration = new RealmConfiguration.Builder()
                .name("Default")
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @UiThread
    @NonNull
    public SearchResponseModel getRepositories() {
        Realm r = ensureRealmCreated();
        SearchResponseModel cached = realm.where(SearchResponseModel.class).findFirst();
        if (cached == null) {
            r.beginTransaction();
            cached = r.createObject(SearchResponseModel.class);
            r.commitTransaction();
        }
        return cached;
    }

    @AnyThread
    public void insertRepositories(@NonNull SearchResponseModel data) {
        try (Realm r = rawRealm(realmConfiguration)) {
            r.executeTransaction(realm -> {
                SearchResponseModel cached = realm.where(SearchResponseModel.class).findFirst();
                if (cached != null) {
                    cached.getRepos().clear();
                    cached.getRepos().addAll(data.getRepos());
                    cached.setTotalCount(data.getTotalCount());
                } else {
                    realm.insert(data);
                }
            });
        }
    }

    @AnyThread
    public void insertRepositoriesNextPage(@NonNull SearchResponseModel data) {
        try (Realm r = rawRealm(realmConfiguration)) {
            r.executeTransaction(realm -> {
                SearchResponseModel cached = realm.where(SearchResponseModel.class).findFirst();
                if (cached != null) {
                    cached.getRepos().addAll(data.getRepos());
                } else {
                    realm.insert(data);
                }
            });
        }
    }

    public void clear() {
        try (Realm r = rawRealm(realmConfiguration)) {
            r.executeTransaction(realm -> {
                SearchResponseModel cached = realm.where(SearchResponseModel.class).findFirst();
                if (cached != null) {
                    cached.getRepos().clear();
                }
            });
        }
    }

    @NonNull
    private static Realm rawRealm(RealmConfiguration configuration) {
        return Realm.getInstance(configuration);
    }

    @NonNull
    private Realm ensureRealmCreated() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getInstance(realmConfiguration);
        }
        return realm;
    }
}
