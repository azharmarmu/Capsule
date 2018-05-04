package com.zero.capsule.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

public class Constants {

    public static final String ENV = "development";
    //public static final String ENV = "production";

    /* Firebase AUTH */
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();

    /* Camera Constants*/
    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;

    /**/
    static final String LOGIN = "Login";
    static final String TOKEN = "token";
    public static PhoneAuthProvider.ForceResendingToken OTP_RESEND_TOKEN = null;


    public static final String deviceToken = "deviceToken";
    public static final String phoneNumber = "phoneNumber";
    public static final String verificationID = "verificationID";


    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";
}
