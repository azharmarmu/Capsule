package com.zero.capsule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zero.capsule.R;
import com.zero.capsule.firebase.FirebaseDB;
import com.zero.capsule.utils.Constants;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();


    }

    private void checkUser() {
        if (Constants.AUTH.getCurrentUser() == null) {
            Intent indexActivity = new Intent(MainActivity.this, IndexActivity.class);
            indexActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(indexActivity);
            finish();
        }
    }
}
