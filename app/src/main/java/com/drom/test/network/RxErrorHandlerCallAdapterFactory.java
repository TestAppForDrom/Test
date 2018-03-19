package com.drom.test.network;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import retrofit2.*;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class RxErrorHandlerCallAdapterFactory extends CallAdapter.Factory {
    private final RxJava2CallAdapterFactory mOriginalCallAdapterFactory;

    private RxErrorHandlerCallAdapterFactory() {
        mOriginalCallAdapterFactory = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create() {
        return new RxErrorHandlerCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        return new RxCallAdapterWrapper<>(retrofit, mOriginalCallAdapterFactory.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Flowable<R>> {
        private final Retrofit mRetrofit;
        private final CallAdapter<R, ?> mWrappedCallAdapter;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<R, ?> wrapped) {
            mRetrofit = retrofit;
            mWrappedCallAdapter = wrapped;
        }

        @Override
        public Type responseType() {
            return mWrappedCallAdapter.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Flowable<R> adapt(@NonNull Call<R> call) {
            //noinspection Convert2Lambda
            return ((Flowable) mWrappedCallAdapter.adapt(call)).onErrorResumeNext(new Function<Throwable, Flowable>() {
                @Override
                public Flowable apply(Throwable throwable) {
                    return Flowable.error(asRetrofitException(throwable));
                }
            });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            // We had non-200 http error
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();

                return RetrofitException.httpError(response.raw().request().url().toString(), response, mRetrofit);
            }
            // A network error happened
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }

            // We don't know what happened. We need to simply convert to an unknown error
            return RetrofitException.unexpectedError(throwable);
        }
    }
}
