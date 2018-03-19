package com.drom.test.network.usecase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

public class BroadcastObserver implements FlowableOnSubscribe<Boolean> {
    private final Context context;

    private BroadcastObserver(Context context) {
        this.context = context;
    }

    public static Flowable<Boolean> fromConnectivityManager(Context context) {
        return Flowable.create(new BroadcastObserver(context), BackpressureStrategy.LATEST);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Boolean> emitter) throws Exception {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                emitter.onNext(hasInternetConnection(context));
            }
        };
        context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        emitter.setDisposable(unsubscribeInUiThread(() -> context.unregisterReceiver(receiver)));
    }


    private static Disposable unsubscribeInUiThread(Action unsubscribe) {
        return Disposables.fromAction(() -> {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                unsubscribe.run();
            } else {
                Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                inner.schedule(() -> {
                    try {
                        unsubscribe.run();
                    } catch (Exception ignored) {

                    }
                    inner.dispose();
                });
            }
        });
    }

    private static boolean hasInternetConnection(Context appContext) {
        ConnectivityManager manager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (manager != null) {
                NetworkInfo info = manager.getActiveNetworkInfo();
                if (info != null && (info.isConnectedOrConnecting() || info.isAvailable())) {
                    return true;
                }

                info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (info != null && info.isConnectedOrConnecting()) {
                    return true;
                } else {
                    info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (info != null && info.isConnectedOrConnecting()) {
                        return true;
                    }
                }
            }

        } catch (Exception ignored) {

        }
        return false;
    }
}
