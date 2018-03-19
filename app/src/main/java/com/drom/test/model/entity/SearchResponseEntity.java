package com.drom.test.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponseEntity {
    @SerializedName("total_count") private long totalCount;
    @SerializedName("items") private List<RepositoryEntity> repos;

    public long getTotalCount() {
        return totalCount;
    }

    public List<RepositoryEntity> getRepos() {
        return repos;
    }
}
