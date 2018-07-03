package com.zero.capsule.meds.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zero.capsule.meds.R;
import com.zero.capsule.meds.activity.MainActivity;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class MyConsultationsFragment extends Fragment {


    public MyConsultationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_common, container, false);
        ((MainActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Consultations");
        TextView noData = rootView.findViewById(R.id.tvNoData);
        noData.setText(R.string.my_consultations);
        return rootView;
    }
}
