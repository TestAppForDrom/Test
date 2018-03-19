package com.drom.test.model.entity;

import com.google.gson.annotations.SerializedName;

public class RepositoryEntity {
    @SerializedName("id") private String id;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("url") private String url;
    @SerializedName("owner") private UserEntity owner;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public UserEntity getOwner() {
        return owner;
    }
}
