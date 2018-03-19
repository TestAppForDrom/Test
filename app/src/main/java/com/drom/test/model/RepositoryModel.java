package com.drom.test.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class RepositoryModel implements RealmModel {
    @PrimaryKey private String id;
    private String name;
    private String description;
    private String url;
    private String ownerName;
    private String ownerAvatar;

    public String getId() {
        return id;
    }

    public RepositoryModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public RepositoryModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RepositoryModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RepositoryModel setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public RepositoryModel setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public RepositoryModel setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
        return this;
    }
}
