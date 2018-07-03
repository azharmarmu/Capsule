package com.zero.capsule.meds.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        getUserDetails();
        return rootView;
    }

    private void getUserDetails() {
        new FirebaseDB().userFS
                .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null) {
                            HashMap<String, Object> userDetails = (HashMap<String, Object>) documentSnapshot.getData();
                            TextView tvUserName = rootView.findViewById(R.id.tvUserName);
                            TextView tvUserPhone = rootView.findViewById(R.id.tvUserPhone);
                            TextView tvUserMailID = rootView.findViewById(R.id.tvUserMailID);

                            tvUserName.setText(userDetails.get(Constants.userName).toString());
                            tvUserPhone.setText(userDetails.get(Constants.phoneNumber).toString());
                            tvUserMailID.setText(userDetails.get(Constants.emailID).toString());

                            tvUserMailID.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DialogUtils.appToastShort(getActivity(), "Coming soon");
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}
