package com.zero.capsule.meds.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by azharuddin on 17/08/17.
 */

public class SharePref {

    public static void setToken(Context activity, String _token) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.TOKEN, _token);
        editor.apply();
    }

    public static String getToken(Context activity) {
        SharedPreferences preferences = activity.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE);
        return preferences.getString(Constants.TOKEN, "");
    }

    public static void setUserName(Context activity, String _name) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.name, _name);
        editor.apply();
    }

    public static String getUSerName(Context activity) {
        SharedPreferences preferences = activity.getSharedPreferences(Constants.LOGIN, Context.MODE_PRIVATE);
        return preferences.getString(Constants.name, "");
    }

    public static void setPrescriptionPhotoList(Activity activity, String _photoList) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Constants.PRESCRIPTION_PHOTO, Context.MODE_PRIVATE).edit();
        editor.putString("photo", _photoList);
        editor.apply();
    }

    public static String getPrescriptionPhotoList(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(Constants.PRESCRIPTION_PHOTO, Context.MODE_PRIVATE);
        return preferences.getString("photo", "");
    }

}