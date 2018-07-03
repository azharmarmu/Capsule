package com.zero.capsule.meds.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.adapter.PrescriptionPhotoAdapter;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.listeners.PhotoListListener;
import com.zero.capsule.meds.listeners.UploadImageListener;
import com.zero.capsule.meds.utils.CommonUtil;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;
import com.zero.capsule.meds.utils.StorageUtil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class BuyMedicinesActivity extends AppCompatActivity implements PhotoListListener, UploadImageListener {

    Uri imageUri;
    List<String> photosList = new ArrayList<>();

    RadioGroup container, attachedPrescriptionTypeLayout;
    RecyclerView.Adapter adapter;
    TextView tvAddress;

    RelativeLayout attachedPrescriptionLayout, attachedMedicineLayout;
    LinearLayout medSpecifyLayout;

    String prescriptionTypeForOrder = Constants.orderAll;

    boolean isSOD = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addressCheck();
    }

    private void addressCheck() {
        new FirebaseDB().userFS
                .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            HashMap<String, Object> docData = (HashMap<String, Object>) documentSnapshot.getData();
                            if (docData != null) {
                                if (!docData.containsKey(Constants.address)) {
                                    startActivity(new Intent(BuyMedicinesActivity.this, AddAddressActivity.class));
                                    finish();
                                } else {
                                    setContentView(R.layout.activity_buy_medicines);

                                    Toolbar toolbar = findViewById(R.id.toolbar);
                                    setSupportActionBar(toolbar);
                                    Objects.requireNonNull(getSupportActionBar()).setTitle("Order Medicines");
                                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                                    attachedPrescriptionLayout = findViewById(R.id.attachedPrescriptionLayout);
                                    attachedMedicineLayout = findViewById(R.id.attachedMedicineLayout);
                                    attachedPrescriptionTypeLayout = findViewById(R.id.attachedPrescriptionTypeLayout);

                                    container = findViewById(R.id.rgPrescriptionType);
                                    container.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            attachedPrescriptionLayout.setVisibility(View.GONE);
                                            attachedMedicineLayout.setVisibility(View.GONE);
                                            attachedPrescriptionTypeLayout.setVisibility(View.GONE);

                                            radioGroup.check(i);
                                            if (R.id.rbSOD == i) {
                                                attachedMedicineLayout.setVisibility(View.VISIBLE);
                                                isSOD = true;
                                            } else {
                                                attachedPrescriptionLayout.setVisibility(View.VISIBLE);
                                                attachedPrescriptionTypeLayout.setVisibility(View.VISIBLE);
                                                isSOD = false;
                                            }
                                        }
                                    });
                                    container.check(R.id.rbSOD);

                                    medSpecifyLayout = findViewById(R.id.medSpecifyLayout);
                                    attachedPrescriptionTypeLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                            medSpecifyLayout.setVisibility(View.GONE);

                                            radioGroup.check(i);
                                            if (R.id.rbOrderAll == i) {
                                                prescriptionTypeForOrder = Constants.orderAll;
                                            } else if (R.id.rbOrderSpecify == i) {
                                                medSpecifyLayout.setVisibility(View.VISIBLE);
                                                prescriptionTypeForOrder = Constants.orderSpecify;
                                            } else if (R.id.rbOrderCall == i) {
                                                prescriptionTypeForOrder = Constants.orderCall;
                                            }
                                        }
                                    });
                                    attachedPrescriptionTypeLayout.check(R.id.rbOrderAll);

                                    setViews();

                                    HashMap<String, Object> addressList = (HashMap<String, Object>) docData.get(Constants.address);

                                    HashMap<String, Object> _myAddressMap;
                                    if (addressList.containsKey("home")) {
                                        _myAddressMap = (HashMap<String, Object>) addressList.get("home");
                                    } else if (addressList.containsKey("office")) {
                                        _myAddressMap = (HashMap<String, Object>) addressList.get("office");
                                    } else {
                                        _myAddressMap = (HashMap<String, Object>) addressList.get("other");
                                    }

                                    String address = _myAddressMap.get(Constants.flatNo) + "\n" +
                                            _myAddressMap.get(Constants.street) + ", " + _myAddressMap.get(Constants.locality) + "\n" +
                                            _myAddressMap.get(Constants.city) + ", " + _myAddressMap.get(Constants.state) + "\n" +
                                            _myAddressMap.get(Constants.pincode) + "\n" +
                                            "Mobile: " + _myAddressMap.get(Constants.phone);

                                    tvAddress = findViewById(R.id.tvChosenAddress);
                                    tvAddress.setText(address);
                                }
                            }
                        }

                    }
                });
    }

    private void setViews() {
        new StorageUtil().setUploadListener(this);
        PrescriptionPhotoAdapter adapterClass = new PrescriptionPhotoAdapter(this, photosList);
        adapterClass.setPhotoListListener(this);//setListeners

        adapter = adapterClass;
        final RecyclerView recyclerView = findViewById(R.id.rvPrescriptionPhoto);
        recyclerView.removeAllViews();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }

    public void addPhoto(String imageUri) {
        photosList.add(imageUri);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void removePhoto(int index) {
        photosList.remove(index);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Constants.REQUEST_CAMERA) {
                    Uri uri = imageUri;
                    addPhoto(Objects.requireNonNull(uri).toString());
                } else if (requestCode == Constants.REQUEST_GALLERY) {
                    InputStream imageStream = getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
                    Uri uri = CommonUtil.getImageUri(this, BitmapFactory.decodeStream(imageStream));
                    addPhoto(Objects.requireNonNull(uri).toString());
                } else if (requestCode == 3) {
                    String address = data.getStringExtra("address");
                    tvAddress.setText(address);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addPrescription(View view) {
        chooseImage();
    }

    private void chooseImage() {
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_image_settings);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(window).getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;

        // set the custom dialog components - text and button
        TextView gallery = dialog.findViewById(R.id.dialog_gallery);
        TextView camera = dialog.findViewById(R.id.dialog_camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryIntent();
                dialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraIntent();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void galleryIntent() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                        pickPhoto.setType("image/*");
                        startActivityForResult(pickPhoto, Constants.REQUEST_GALLERY);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        DialogUtils.appToastShort(BuyMedicinesActivity.this, "Need permission to choose prescription from gallery");
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

    private void cameraIntent() {
        Log.i("Camera click", "yes");
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = getContentResolver()
                                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, Constants.REQUEST_CAMERA);
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

    public void addMedicine(View view) {

    }

    public void changeAddress(View view) {
        startActivityForResult(new Intent(BuyMedicinesActivity.this, ChooseAddressActivity.class), 3);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BuyMedicinesActivity.this.onSuperBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onSuperBackPressed() {
        super.onBackPressed();
    }

    public void placeOrder(final View view) {

        new FirebaseDB().lastOrderIDRT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DialogUtils.showProgressDialog(BuyMedicinesActivity.this, "Loading...");
                if (dataSnapshot.getValue() != null) {
                    int lastOrderID = Integer.parseInt(dataSnapshot.getValue().toString());
                    lastOrderID++;
                    if (isSOD) {

                    } else {
                        new StorageUtil().uploadImage(photosList, String.valueOf(lastOrderID));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addPhoto(List<String> uri, final String orderID) {
        if (uri.size() == photosList.size()) {

            HashMap<String, Object> prescriptionOrder = new HashMap<>();

            prescriptionOrder.put(Constants.userID, Constants.AUTH.getUid());
            prescriptionOrder.put(Constants.prescriptions, uri);
            prescriptionOrder.put(Constants.timeStamp, new Timestamp(System.currentTimeMillis()));
            prescriptionOrder.put(Constants.orderID, String.valueOf(orderID));
            prescriptionOrder.put(Constants.address, tvAddress.getText().toString());
            prescriptionOrder.put(Constants.status, Constants.pending);
            prescriptionOrder.put(Constants.prescriptionType, prescriptionTypeForOrder);

            if (prescriptionTypeForOrder.equalsIgnoreCase(Constants.orderSpecify)) {
                EditText medSpecify = findViewById(R.id.etMedSpecify);
                prescriptionOrder.put(Constants.medSpecify, medSpecify.getText().toString().trim());
            }

            new FirebaseDB().orderListFS.document()
                    .set(prescriptionOrder)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            new FirebaseDB().lastOrderIDRT.setValue(String.valueOf(orderID));
                            DialogUtils.dismissProgressDialog();
                            startActivity(new Intent(BuyMedicinesActivity.this, ConfirmOrderActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Error", e.getMessage());
                            DialogUtils.appToastShort(BuyMedicinesActivity.this, e.getMessage());
                        }
                    });
        }
    }
}
