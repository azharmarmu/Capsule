package com.zero.capsule.meds.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.SharePref;


/**
 * Created by azharuddin on 21/07/17.
 */
@SuppressWarnings("ConstantConditions")
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String TAG = "FirebaseID";
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        saveTokenInLocal(refreshedToken);
        if (Constants.AUTH.getCurrentUser() != null) {
            saveTokenToDB(refreshedToken);
        }
    }

    private void saveTokenInLocal(String refreshedToken) {
        SharePref.setToken(getApplicationContext(), refreshedToken);
    }

    private void saveTokenToDB(String refreshedToken) {
        new FirebaseDB().userFS.document(Constants.AUTH.getUid()).update(Constants.deviceToken, refreshedToken);
    }

}

