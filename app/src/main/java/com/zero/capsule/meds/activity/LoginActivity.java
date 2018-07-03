package com.zero.capsule.meds.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;
import com.zero.capsule.meds.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements Serializable, PermissionListener, PermissionRequestErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void nextClick(View view) {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.RECEIVE_SMS)
                .withListener(this)
                .check();
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse response) {
        validatePhoneNumber();
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse response) {
        validatePhoneNumber();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        token.continuePermissionRequest();
    }

    @Override
    public void onError(DexterError error) {
        Log.e("Dexter", "There was an error: " + error.toString());
    }

    private void validatePhoneNumber() {
        TextView phoneNumber = findViewById(R.id.etPhoneNumber);
        String phone = phoneNumber.getText().toString();
        if (!phone.isEmpty()) {
            new OTPCallTask().execute(phone);
        } else {
            DialogUtils.appToastShort(getApplicationContext(), "Enter valid mobile number");
        }
    }

    @SuppressLint("StaticFieldLeak")
    class OTPCallTask extends AsyncTask<String, Void, Response> {

        String phone;

        protected okhttp3.Response doInBackground(String... phone) {
            this.phone = phone[0];
            try {
                String url = "http://2factor.in/API/V1/" + Constants._api2Factor + "/SMS/" + this.phone + "/AUTOGEN";
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("content-type", "application/json")
                        .build();


                return client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(okhttp3.Response response) {
            if (response.isSuccessful()) {
                try {
                    JSONObject body = new JSONObject(Objects.requireNonNull(response.body()).string());

                    String details = body.getString("Details");

                    Intent otpActivity = new Intent(LoginActivity.this, OTPActivity.class);
                    otpActivity.putExtra(Constants.phoneNumber, phone);
                    otpActivity.putExtra(Constants.verificationID, details);
                    startActivity(otpActivity);
                    finish();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
