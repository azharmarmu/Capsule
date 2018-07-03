package com.zero.capsule.meds.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zero.capsule.meds.utils.Constants;


/**
 * Created by azharuddin on 4/8/17.
 */

public class FirebaseDB {

    private final FirebaseDatabase dbRT = FirebaseDatabase.getInstance();
    private final FirebaseFirestore dbFS = FirebaseFirestore.getInstance();
    private static FirebaseStorage dbST = FirebaseStorage.getInstance();

    /* RealTime */
    private final DatabaseReference RT_ENVIRONMENT = dbRT.getReference(Constants.ENV);
    public final DatabaseReference carouselListRT = RT_ENVIRONMENT.child(Constants.carouselList);
    public final DatabaseReference lastOrderIDRT = RT_ENVIRONMENT.child(Constants.lastOrderIDRT);

    /* Fire Store */
    public final CollectionReference userFS = dbFS.collection(Constants.usersList);
    public final CollectionReference popularProductsListFS = dbFS.collection(Constants.popularProductsList);
    public final CollectionReference orderListFS = dbFS.collection(Constants.orderList);
    public final CollectionReference servingPincodeFS = dbFS.collection(Constants.servingPincodeList);
    public final CollectionReference deliveryBoyFS = dbFS.collection(Constants.deliveryBoyList);
    public final CollectionReference allMedicineListFS = dbFS.collection(Constants.allMedicineList);

    /* Storage */
    private final StorageReference ST_ENVIRONMENT = dbST.getReference(Constants.ENV);
    public final StorageReference prescriptionST = ST_ENVIRONMENT.child(Constants.prescriptions);

}
