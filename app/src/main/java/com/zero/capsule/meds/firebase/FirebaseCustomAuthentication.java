package com.zero.capsule.meds.firebase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zero.capsule.meds.activity.MainActivity;
import com.zero.capsule.meds.activity.RegistrationActivity;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;


/**
 * Created by azharuddin on 4/8/17.
 */

public class FirebaseCustomAuthentication {
    public static void signInWithCustomToken(final Activity activity, final String phoneNumber, String customToken) {
        Constants.AUTH.signInWithCustomToken(customToken)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(activity.getLocalClassName(), "signInWithCustomToken:success");


                            checkUsers(activity, phoneNumber);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(activity.getLocalClassName(), "signInWithCustomToken:failure", task.getException());
                            DialogUtils.appToastShort(activity, "Authentication failed.");
                        }
                    }
                });
    }

    private static void checkUsers(final Activity activity, String phoneNumber) {
        new FirebaseDB().userFS
                .document(phoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            Intent mainActivity = new Intent(activity, MainActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(mainActivity);
                            activity.finish();
                        } else {
                            Intent registrationActivity = new Intent(activity, RegistrationActivity.class);
                            registrationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(registrationActivity);
                            activity.finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DialogUtils.appToastShort(activity, e.getMessage());
                    }
                });
    }


}
