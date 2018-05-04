package com.zero.capsule.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.zero.capsule.utils.Constants;


/**
 * Created by azharuddin on 4/8/17.
 */

public class FirebaseDB {

    private final FirebaseDatabase dbRT = FirebaseDatabase.getInstance();
    private final FirebaseFirestore dbFS = FirebaseFirestore.getInstance();

    /* RealTime */
    private final DatabaseReference ENVIRONMENT = dbRT.getReference(Constants.ENV);

    /* Fire Store */

    public FirebaseDB() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        dbFS.setFirestoreSettings(settings);
    }

}
