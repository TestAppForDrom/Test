package com.drom.test.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public class CommonUtils {

    private CommonUtils() {
    }

    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static void visible(View... views) {
        if (views != null) {
            for (View v : views) {
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public static void gone(View... views) {
        if (views != null) {
            for (View v : views) {
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
            }
        }
    }
}
