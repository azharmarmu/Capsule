package com.zero.capsule.meds.utils;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.listeners.UploadImageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StorageUtil {

    private static UploadImageListener uploadImageListener;

    public void uploadImage(List<String> photoUri, final String orderID) {

        final List<String> uri = new ArrayList<>();
        for (int i = 0; i < photoUri.size(); i++) {
            new FirebaseDB().prescriptionST.child(Constants.orderID + "-" + orderID)
                    .child(String.valueOf(i))
                    .putFile(Uri.parse(photoUri.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uri.add(Objects.requireNonNull(taskSnapshot.getUploadSessionUri()).toString());
                            uploadImageListener.addPhoto(uri, orderID);
                        }
                    });
        }
    }

    public void setUploadListener(UploadImageListener uploadImageListener) {
        StorageUtil.uploadImageListener = uploadImageListener;
    }

}
