package com.zero.capsule.meds.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.SharePref;

import java.util.HashMap;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        cbTerms = findViewById(R.id.cbTerms);
        TextView tvTerms = findViewById(R.id.tvTerms);

        Spanned spanned = Html.fromHtml("I accept the " +
                "<a href='id.web.freelancer.example.TCActivity://Kode'>terms & conditions</a>");
        tvTerms.setText(spanned);
        tvTerms.setClickable(true);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void registerClick(final View view) {


        if (cbTerms.isChecked()) {

            EditText etName = findViewById(R.id.etName);
            EditText etAge = findViewById(R.id.etAge);
            EditText etEmailID = findViewById(R.id.etEmailID);
            EditText etReferralCode = findViewById(R.id.etReferralCode);

            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String emailID = etEmailID.getText().toString().trim();
            String referralCode = etReferralCode.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name required");
            } else if (age.isEmpty()) {
                etAge.setError("Age required");
            } else if (emailID.isEmpty()) {
                etEmailID.setError("EMail ID required");
            } else {
                int _age = Integer.parseInt(age);
                if (_age <= 0) {
                    etAge.setError("Invalid age");
                } else {
                    HashMap<String, Object> registerDetails = new HashMap<>();
                    SharePref.setUserName(RegistrationActivity.this, name);
                    registerDetails.put(Constants.userName, name);
                    registerDetails.put(Constants.age, age);
                    registerDetails.put(Constants.emailID, emailID);
                    registerDetails.put(Constants.phoneNumber, Constants.AUTH.getUid());
                    registerDetails.put(Constants.deviceToken, SharePref.getToken(RegistrationActivity.this));

                    if (!referralCode.isEmpty())
                        registerDetails.put(Constants.referredCode, referralCode);

                    String uid = Objects.requireNonNull(Constants.AUTH.getUid());
                    if (uid != null) {
                        new FirebaseDB().userFS
                                .document(uid)
                                .set(registerDetails)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(view, "Successfully registered!", Snackbar.LENGTH_SHORT).show();
                                        Intent mainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(mainActivity);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(view, "Could not Register", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }

                }
            }


        } else {
            Snackbar.make(cbTerms, "Please accept terms and condtions to continue", Snackbar.LENGTH_SHORT).show();
        }

    }
}
