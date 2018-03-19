package com.drom.test.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Pair;
import com.drom.test.BuildConfig;
import com.drom.test.beans.SessionManager;
import com.drom.test.features.auth.DromTestAccount;
import com.drom.test.model.entity.SearchResponseEntity;
import com.drom.test.model.entity.UserEntity;
import com.google.gson.Gson;
import io.reactivex.Flowable;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetrofitRestApi implements RestApiBean {
    private static final String ENDPOINT = "https://api.github.com/";
    private static final long CONNECTION_TIMEOUT_SECONDS = 120;
    private static final long READ_TIMEOUT_SECONDS = 120;
    private static final long WRITE_TIMEOUT_SECONDS = 120;
    private final SessionManager sessionManager;
    private final RestApi restApi;

    public RetrofitRestApi(@NonNull Context context, @NonNull SessionManager sessionManager) {
        this.sessionManager = sessionManager;

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.interceptors().add(new HeaderInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.interceptors().add(logging);
        }
        OkHttpClient httpClient = httpClientBuilder
                .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxErrorHandlerCallAdapterFactory.create())
                .client(httpClient)
                .build();

        restApi = retrofit.create(RestApi.class);
    }

    @Override
    public Flowable<UserEntity> login(@NonNull String name, @NonNull String password) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", createHeader(name, password));
        return restApi.login(headers);
    }

    @Override
    public Flowable<SearchResponseEntity> search(@Nullable String query, int page, int pageSize) {
        return restApi.search(query, page, pageSize);
    }

    private class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Pair<String, String> loginPassword = sessionManager.getLoginPassword();
            if (loginPassword != null) {
                String username = loginPassword.first;
                String password = loginPassword.second;
                if (username != null && password != null && !DromTestAccount.UNAUTHORIZED.equals(username)) {
                    String basicAuth = createHeader(username, password);
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basicAuth.trim());
                    return chain.proceed(requestBuilder.build());
                }
            }

            return chain.proceed(chain.request());
        }
    }

    @Nullable
    private static String createHeader(String username, String password) {
        if (username != null && password != null) {
            String userCredentials = username + ":" + password;
            return "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.NO_WRAP));
        }
        return null;
    }
}
