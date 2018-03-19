package com.drom.test.model.entity;

import com.google.gson.annotations.SerializedName;

public class UserEntity {
    @SerializedName("id") private String id;
    @SerializedName("login") private String login;
    @SerializedName("avatar_url") private String avatarUrl;

    public String getLogin() {
        return login;
    }

    public String getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
