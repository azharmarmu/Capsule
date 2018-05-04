package com.zero.capsule.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by azharuddin on 17/08/17.
 */

public class SharePref {

    public static String getToken(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE);
        return preferences.getString(Constants.TOKEN, "");
    }

    public static void setToken(Context mContext, String token) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

}
