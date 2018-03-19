package com.drom.test.network.usecase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.drom.test.network.RetrofitException;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class UseCase<T> extends BaseUseCase {

    public Flowable<T> execute() {
        return buildUseCaseObservable()
                .retryWhen(flowable -> flowable.flatMap(throwable -> {
                    if (isNetworkError(throwable)) {
                        return new RetryWithConnectivityIncremental(appContext.get(), 10, 40, TimeUnit.SECONDS).getFlowable();
                    }
                    //noinspection RedundantTypeArguments
                    return Flowable.<Object>error(throwable);
                }));
    }

    @NonNull
    protected abstract Flowable<T> buildUseCaseObservable();

    public static void maybeUnsubscribe(@Nullable Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    protected static boolean isNetworkError(Throwable throwable) {
        if (throwable instanceof RetrofitException) {
            return ((RetrofitException) throwable).getKind() == RetrofitException.Kind.NETWORK;
        }
        return throwable instanceof TimeoutException;
    }
}
