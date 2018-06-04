package com.natives.zero.capsule.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mindorks.paracamera.Camera;
import com.natives.zero.capsule.R;
import com.natives.zero.capsule.adapter.PrescriptionPhotoAdapter;
import com.natives.zero.capsule.listeners.PhotoListListener;
import com.natives.zero.capsule.utils.CommonUtil;
import com.natives.zero.capsule.utils.Constants;
import com.natives.zero.capsule.utils.DialogUtils;
import com.natives.zero.capsule.utils.SharePref;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadPrescriptionActivity extends AppCompatActivity
        implements PhotoListListener {

    CardView attachedPrescriptionCard;

    List<String> photosList = new ArrayList<>();
    RecyclerView.Adapter adapter;

    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        // get uploaded prescription list
        getAttachedPrescriptions();

        setViews();
    }


    private void setViews() {
        //setListeners
        PrescriptionPhotoAdapter adapterClass = new PrescriptionPhotoAdapter(this, photosList);
        adapterClass.setPhotoListListener(this);

        adapter = adapterClass;
        final RecyclerView recyclerView = findViewById(R.id.rvPrescriptionPhoto);
        recyclerView.removeAllViews();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }

    private void getAttachedPrescriptions() {
        attachedPrescriptionCard = findViewById(R.id.attachedPrescriptionCard);

        String photoListString = SharePref.getPrescriptionPhotoList(UploadPrescriptionActivity.this);
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();

        if (gson.fromJson(photoListString, type) != null) {
            photosList = gson.fromJson(photoListString, type);
        }

        showHidePrescriptionCard();
    }

    public void cameraClick(View view) {
        Log.i("Camera click", "yes");
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        camera = new Camera.Builder()
                                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                                .setTakePhotoRequestCode(Constants.REQUEST_CAMERA)
                                .setDirectory("capsule")
                                .setName("prescription_" + System.currentTimeMillis())
                                .setImageFormat(Camera.IMAGE_JPEG)
                                .setCompression(75)
                                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                                .build(UploadPrescriptionActivity.this);
                        try {
                            camera.takePicture();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Dexter", "There was an error: " + error.toString());
                    }
                })
                .check();

    }

    public void galleryClick(View view) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.REQUEST_GALLERY);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        DialogUtils.appToastShort(UploadPrescriptionActivity.this, "Need permission to choose prescription from gallery");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Dexter", "There was an error: " + error.toString());
                    }
                })
                .check();
    }

    public void myPrescriptionsClick(View view) {
        startActivity(new Intent(this, ExistingPrescriptionActivity.class));
    }

    public void continuePrescription(View view) {
        startActivity(new Intent(this, OrderInformationActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CAMERA) {
                Uri uri = CommonUtil.getImageUri(UploadPrescriptionActivity.this, camera.getCameraBitmap());
                addPhoto(Objects.requireNonNull(uri).toString());
            } else if (requestCode == Constants.REQUEST_GALLERY) {
                Uri uri = data.getData();
                addPhoto(Objects.requireNonNull(uri).toString());
            }
        }
    }

    public void addPhoto(String uri) {
        photosList.add(uri);
        adapter.notifyDataSetChanged();
        showHidePrescriptionCard();
        updatePrescriptionPhotoListCache();
    }

    @Override
    public void removePhoto(int index) {
        photosList.remove(index);
        adapter.notifyDataSetChanged();
        showHidePrescriptionCard();
        updatePrescriptionPhotoListCache();
    }

    private void updatePrescriptionPhotoListCache() {
        Gson gson = new Gson();
        String photoListString = gson.toJson(photosList);
        SharePref.setPrescriptionPhotoList(UploadPrescriptionActivity.this, photoListString);
    }

    private void showHidePrescriptionCard() {
        if (photosList.size() > 0) {
            attachedPrescriptionCard.setVisibility(View.VISIBLE);
        } else {
            attachedPrescriptionCard.setVisibility(View.GONE);
        }
    }
}
