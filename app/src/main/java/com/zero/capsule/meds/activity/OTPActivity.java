package com.zero.capsule.meds.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.zero.capsule.meds.firebase.FirebaseCustomAuthentication;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("StaticFieldLeak")
public class OTPActivity extends AppCompatActivity {

    String phoneNumber, verificationId;
    EditText etOTP;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.d("Text", message);
                etOTP.setText(message);
                String code = etOTP.getText().toString();
                etOTP.setSelection(code.length());
                verifyOTP(code);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOTP = findViewById(R.id.etOTP);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString(Constants.phoneNumber);
            verificationId = bundle.getString(Constants.verificationID);
        }
    }

    public void resendOTP(View view) {
        // TODO: 12/06/18
    }

    public void checkOTP(View view) {
        String code = etOTP.getText().toString();
        if (!code.isEmpty()) {
            verifyOTP(code);
        } else {
            DialogUtils.appToastShort(OTPActivity.this, "OTP cannot be empty");
        }
    }

    private void verifyOTP(String code) {
        new OTPVerifyTask().execute(code);
    }

    class OTPVerifyTask extends AsyncTask<String, Void, Response> {

        String code;

        protected okhttp3.Response doInBackground(String... code) {
            this.code = code[0];
            try {
                String url = "http://2factor.in/API/V1/" + Constants._api2Factor + "/SMS/VERIFY/" + verificationId + "/" + this.code;
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
                new CreateCustomTokenTask().execute();
            } else {
                DialogUtils.appToastShort(OTPActivity.this, response.message());
            }

        }
    }


    @SuppressWarnings("ConstantConditions")
    class CreateCustomTokenTask extends AsyncTask<Void, Void, Response> {

        @Override
        protected okhttp3.Response doInBackground(Void... voids) {
            try {
                String url = "https://us-central1-capsule-7c24c.cloudfunctions.net/createCustomAuthToken";
                OkHttpClient client = new OkHttpClient();


                RequestBody requestBody = new FormBody.Builder()
                        .add("uid", phoneNumber)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
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
                    JSONObject _jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    String customToken = _jsonObject.getString("token");
                    FirebaseCustomAuthentication.signInWithCustomToken(OTPActivity.this, phoneNumber, customToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                DialogUtils.appToastShort(OTPActivity.this, response.message());
            }
        }

    }
}
