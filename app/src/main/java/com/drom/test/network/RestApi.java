package com.drom.test.network;

import com.drom.test.model.entity.SearchResponseEntity;
import com.drom.test.model.entity.UserEntity;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

import java.util.Map;

public interface RestApi {

    @GET("user")
    Flowable<UserEntity> login(@HeaderMap Map<String, String> headers);

    @GET("search/repositories")
    Flowable<SearchResponseEntity> search(@Query("q") String query, @Query("page") int page, @Query("per_page") int pageSize);
}
