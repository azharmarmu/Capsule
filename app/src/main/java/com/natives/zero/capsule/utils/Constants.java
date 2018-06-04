package com.natives.zero.capsule.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

public class Constants {

    //public static final String ENV = "development";
    public static final String ENV = "production";

    // TAG
    public static final String TAG = "capsule";

    /* Firebase AUTH */
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();


    /* Camera Constants*/
    public static int REQUEST_CAMERA = 1;
    public static int REQUEST_GALLERY = 2;
    public static int RESULT_INTENT = 100;

    public static final String image = "image";
    public static final String camera = "camera";
    public static final String gallery = "gallery";

    public static final String result = "result";

    /**/
    static final String LOGIN = "Login";
    static final String TOKEN = "token";
    public static String PRESCRIPTION_PHOTO = "photo";
    public static PhoneAuthProvider.ForceResendingToken OTP_RESEND_TOKEN = null;


    public static final String deviceToken = "deviceToken";
    public static final String phoneNumber = "phoneNumber";
    public static final String verificationID = "verificationID";


    // broadcast receiver intent filters
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // carousel ---> RT
    public static final String carouselList = "carousel";
    public static final String url = "url";
    public static final String link = "link";

    // usersList ---> FS
    public static final String usersList = "usersList";

    // popularProductList --> FS
    public static String popularProductsList = "productsList";
    public static String popularity = "popularity";
    public static String productImageLink = "imageLink";
    public static String productName = "name";
    public static String productOffer = "offerPercentage";
    public static String productMRP = "MRP";

    // deliveryBoyList
    public static final String deliveryBoyList = "deliveryBoyList";
}
