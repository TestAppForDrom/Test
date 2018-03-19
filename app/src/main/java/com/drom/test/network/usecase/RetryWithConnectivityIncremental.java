package com.drom.test.network.usecase;

import android.content.Context;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RetryWithConnectivityIncremental {
    private final int maxTimeout;
    private final TimeUnit timeUnit;
    private final Flowable<Boolean> isConnectedObservable;
    private final int startTimeOut;
    private int timeout;

    public RetryWithConnectivityIncremental(Context context, int startTimeOut, int maxTimeout, TimeUnit timeUnit) {
        this.startTimeOut = startTimeOut;
        this.maxTimeout = maxTimeout;
        this.timeUnit = timeUnit;
        timeout = startTimeOut;
        isConnectedObservable = getConnectedFlowable(context);
    }

    public Flowable<?> getFlowable() {
        return Flowable.zip(
                isConnectedObservable,
                isConnectedObservable.compose(attachIncrementalTimeout()),
                (boolean1, boolean2) -> boolean1 && boolean2);
    }

    private FlowableTransformer<Boolean, Boolean> attachIncrementalTimeout() {
        return observable -> observable.timeout(timeout, timeUnit)
                .doOnError(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        timeout = timeout > maxTimeout ? maxTimeout : timeout + startTimeOut;
                    }
                });
    }

    private static Flowable<Boolean> getConnectedFlowable(Context context) {
        return BroadcastObserver.fromConnectivityManager(context)
                .distinctUntilChanged()
                .filter(isConnected -> isConnected);
    }
}
