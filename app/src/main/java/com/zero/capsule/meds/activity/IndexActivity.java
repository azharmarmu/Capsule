package com.zero.capsule.meds.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zero.capsule.meds.R;


public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
    }

    public void getStarted(View view) {
        startActivity(new Intent(IndexActivity.this, LoginActivity.class));
        finish();
    }
}
