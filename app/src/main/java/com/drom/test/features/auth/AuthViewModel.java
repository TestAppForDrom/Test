package com.drom.test.features.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.drom.test.network.RetrofitException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import java.net.HttpURLConnection;

public class AuthViewModel extends ViewModel {
    private MutableLiveData<ViewObject> authTokenData;
    private Disposable loginDisposable;

    public LiveData<ViewObject> loginLiveData() {
        if (authTokenData == null) {
            authTokenData = new MutableLiveData<>();
        }
        return authTokenData;
    }

    public void login(String username, String password) {
        authTokenData.setValue(ViewObject.progress(true));
        loginDisposable = new AuthUseCase(username, password)
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> authTokenData.setValue(ViewObject.success(result)),
                        error -> authTokenData.setValue(ViewObject.error(createErrorMessage(error))));
    }

    private String createErrorMessage(Throwable error) {
        if (error instanceof RetrofitException) {
            if (((RetrofitException) error).getResponse().code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                return "Bad credentials";
            }
        }
        Log.i("M_DATA", "Error " + error);
        error.printStackTrace();
        return "Something went wrong. Try again";
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (loginDisposable != null) {
            loginDisposable.dispose();
            loginDisposable = null;
        }
    }
}
