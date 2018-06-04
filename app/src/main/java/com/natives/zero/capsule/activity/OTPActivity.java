package com.natives.zero.capsule.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.natives.zero.capsule.firebase.FirebasePhoneAuthentication;
import com.natives.zero.capsule.R;
import com.natives.zero.capsule.utils.Constants;
import com.natives.zero.capsule.utils.DialogUtils;;

public class OTPActivity extends AppCompatActivity {

    String verificationId, phoneNumber;
    PhoneAuthProvider.ForceResendingToken ForceResendingToken;
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
            ForceResendingToken = Constants.OTP_RESEND_TOKEN;
        }
    }

    public void resendOTP(View view) {
        FirebasePhoneAuthentication.resendVerificationCode(OTPActivity.this, phoneNumber, ForceResendingToken);
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
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        FirebasePhoneAuthentication.signInWithPhoneAuthCredential(
                OTPActivity.this,
                credential,
                phoneNumber
        );
    }
}
