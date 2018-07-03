package com.zero.capsule.meds.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.adapter.AddressAdapter;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.SharePref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class ChooseAddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        getAddress();

    }

    private void getAddress() {
        new FirebaseDB().userFS
                .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            HashMap<String, Object> document = (HashMap<String, Object>) documentSnapshot.getData();
                            SharePref.setUserName(ChooseAddressActivity.this, document.get(Constants.userName).toString());
                            if (document.containsKey(Constants.address)) {
                                HashMap<String, Object> address = (HashMap<String, Object>) document.get(Constants.address);
                                List<Object> addressList = new ArrayList<>(address.values());
                                populateView(addressList);
                            }
                        }
                    }
                });
    }

    private void populateView(List<Object> addressList) {
        RecyclerView.Adapter adapter = new AddressAdapter(this, addressList);
        RecyclerView recyclerView = findViewById(R.id.rvAddress);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }

    public void addAddress(View view) {
        startActivity(new Intent(ChooseAddressActivity.this, AddAddressActivity.class));
    }

    public void continuePrescription(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("address", AddressAdapter.getAddress());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
