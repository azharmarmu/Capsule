package com.zero.capsule.meds.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nullable;

public class OrderInformationActivity extends AppCompatActivity {

    private final String TAG = OrderInformationActivity.class.getSimpleName();

    private int selectedPrescriptionType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);

        toolbar();

        selectTypeForPrescription();
    }


    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void selectTypeForPrescription() {
        final RadioButton rbOrderEverything = findViewById(R.id.rbOrderEverything);
        final RadioButton rbSpecifyMedicines = findViewById(R.id.rbSpecifyMedicines);
        final RadioButton rbCallMe = findViewById(R.id.rbCallMe);

        final LinearLayout orderEverythingLayout = findViewById(R.id.orderEverythingLayout);
        final LinearLayout specifyMedicinesLayout = findViewById(R.id.specifyMedicinesLayout);

        rbOrderEverything.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderEverythingLayout.setVisibility(View.GONE);
                specifyMedicinesLayout.setVisibility(View.GONE);

                if (b) {
                    if (rbSpecifyMedicines.isChecked()) rbSpecifyMedicines.setChecked(false);
                    if (rbCallMe.isChecked()) rbCallMe.setChecked(false);

                    orderEverythingLayout.setVisibility(View.VISIBLE);
                }

                selectedPrescriptionType = 1;
            }
        });

        rbSpecifyMedicines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderEverythingLayout.setVisibility(View.GONE);
                specifyMedicinesLayout.setVisibility(View.GONE);

                if (b) {
                    if (rbOrderEverything.isChecked()) rbOrderEverything.setChecked(false);
                    if (rbCallMe.isChecked()) rbCallMe.setChecked(false);

                    specifyMedicinesLayout.setVisibility(View.VISIBLE);
                }
                selectedPrescriptionType = 2;
            }
        });

        rbCallMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderEverythingLayout.setVisibility(View.GONE);
                specifyMedicinesLayout.setVisibility(View.GONE);

                if (b) {
                    if (rbOrderEverything.isChecked()) rbOrderEverything.setChecked(false);
                    if (rbSpecifyMedicines.isChecked()) rbSpecifyMedicines.setChecked(false);
                }
                selectedPrescriptionType = 3;
            }
        });
    }

    public void continuePrescription(View view) {
        if (selectedPrescriptionType == 0) {
            DialogUtils.appToastShort(this, "Choose any Type");
        }

        new FirebaseDB().userFS
                .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            HashMap<String, Object> docData = (HashMap<String, Object>) documentSnapshot.getData();
                            if (docData != null) {
                                if (!docData.containsKey(Constants.address)) {
                                    startActivity(new Intent(OrderInformationActivity.this, AddAddressActivity.class));
                                } else {
                                    startActivity(new Intent(OrderInformationActivity.this, ChooseAddressActivity.class));
                                }
                            }
                        }

                    }
                });
    }
}
