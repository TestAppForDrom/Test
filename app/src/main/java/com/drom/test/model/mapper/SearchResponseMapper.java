package com.drom.test.model.mapper;

import android.support.annotation.NonNull;
import com.drom.test.model.RepositoryModel;
import com.drom.test.model.SearchResponseModel;
import com.drom.test.model.entity.SearchResponseEntity;
import io.realm.RealmList;

public class SearchResponseMapper extends BaseMapper<SearchResponseModel, SearchResponseEntity> {
    private final RepositoryMapper mapper = new RepositoryMapper();

    @NonNull
    @Override
    public SearchResponseModel mapTo(SearchResponseEntity entity) {
        RealmList<RepositoryModel> repos = new RealmList<>();
        repos.addAll(mapper.mapTo(entity.getRepos()));
        return new SearchResponseModel()
                .setTotalCount(entity.getTotalCount())
                .setRepos(repos);
    }
}
