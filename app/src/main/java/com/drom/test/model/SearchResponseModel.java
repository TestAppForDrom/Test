package com.drom.test.model;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

@RealmClass
public class SearchResponseModel implements RealmModel {
    private long totalCount;
    private RealmList<RepositoryModel> repos;

    public long getTotalCount() {
        return totalCount;
    }

    public SearchResponseModel setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public RealmList<RepositoryModel> getRepos() {
        return repos;
    }

    public SearchResponseModel setRepos(RealmList<RepositoryModel> repos) {
        this.repos = repos;
        return this;
    }
}
