package com.drom.test.features.auth;

public class ViewObject<T> {
    public final T data;
    public final boolean inProgress;
    public final String error;

    private ViewObject(T data, boolean inProgress, String error) {
        this.data = data;
        this.inProgress = inProgress;
        this.error = error;
    }

    public static <T> ViewObject<T> success(T data) {
        return new ViewObject<>(data, false, null);
    }

    public static <T> ViewObject<T> progress(boolean progress) {
        return new ViewObject<>(null, progress, null);
    }

    public static <T> ViewObject<T> error(String error) {
        return new ViewObject<>(null, false, error);
    }
}
