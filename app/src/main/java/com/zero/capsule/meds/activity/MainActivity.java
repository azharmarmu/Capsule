package com.zero.capsule.meds.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.fragment.HomeFragment;
import com.zero.capsule.meds.fragment.MyConsultationsFragment;
import com.zero.capsule.meds.fragment.MyOrdersFragment;
import com.zero.capsule.meds.fragment.SettingsFragment;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import java.util.Objects;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constants.AUTH.getCurrentUser() != null) {
            new FirebaseDB().userFS
                    .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                DialogUtils.appToastShort(MainActivity.this, e.getMessage());
                                return;
                            }

                            if (documentSnapshot != null && documentSnapshot.getData() != null) {
                                setContentView(R.layout.activity_main);
                                /* Toolbar */
                                Toolbar toolbar = findViewById(R.id.toolbar);
                                setSupportActionBar(toolbar);
                                Objects.requireNonNull(getSupportActionBar()).setTitle("");

                                openFragment(new HomeFragment());
                                setBottomNavigation();

                            } else {
                                Intent registrationActivity = new Intent(MainActivity.this, RegistrationActivity.class);
                                registrationActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(registrationActivity);
                                finish();
                            }

                        }
                    });

        } else {
            /* Start index activity */
            Intent indexActivity = new Intent(MainActivity.this, IndexActivity.class);
            indexActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(indexActivity);
            finish();
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void setBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        openFragment(new HomeFragment());
                        return true;
                    case R.id.nav_orders:
                        openFragment(new MyOrdersFragment());
                        return true;
                    case R.id.nav_consultation:
                        openFragment(new MyConsultationsFragment());
                        return true;
                    case R.id.nav_settings:
                        openFragment(new SettingsFragment());
                        return true;
                }
                return false;
            }
        });
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cart:
                // TODO: 05/05/18
                break;
            case R.id.action_notification:
                // TODO: 05/05/18
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        appExitDialog();
    }

    private void appExitDialog() {
        //fetch the user information to show in alert
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.app_name));
        dialog.setMessage("Do you want to exit Capsule App ?");
        dialog.setCancelable(true);

        //positive button
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                dialogInterface.cancel();
            }
        });

        //negative button
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialog.show();
    }
}
