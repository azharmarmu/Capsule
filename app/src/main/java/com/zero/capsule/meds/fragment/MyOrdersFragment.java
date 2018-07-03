package com.zero.capsule.meds.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.activity.MainActivity;
import com.zero.capsule.meds.adapter.AddressAdapter;
import com.zero.capsule.meds.adapter.MyOrdersAdapter;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class MyOrdersFragment extends Fragment {

    String TAG = MyOrdersFragment.class.getSimpleName();

    public MyOrdersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        ((MainActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Order History");
        TextView noData = rootView.findViewById(R.id.tvNoData);
        noData.setText(R.string.my_orders);

        getMyOrders();
        return rootView;
    }

    private void getMyOrders() {
        new FirebaseDB().orderListFS
                .whereEqualTo(Constants.userID, Constants.AUTH.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (querySnapshot != null) {
                            List<HashMap<String, Object>> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                HashMap<String, Object> data = (HashMap<String, Object>) doc.getData();
                                orders.add(data);
                            }
                            Log.d(TAG, "Current cites in CA: " + orders);

                            populateViews(orders);
                        }
                    }
                });
    }

    private void populateViews(List<HashMap<String, Object>> orders) {
        RecyclerView.Adapter adapter = new MyOrdersAdapter(getActivity(), orders);
        RecyclerView recyclerView = rootView.findViewById(R.id.rvMyOrders);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }
}
