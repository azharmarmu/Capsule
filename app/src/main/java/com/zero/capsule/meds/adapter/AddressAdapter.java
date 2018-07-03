package com.zero.capsule.meds.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zero.capsule.meds.R;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.SharePref;

import java.util.HashMap;
import java.util.List;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {
    private Activity activity;
    private List<Object> addressList;
    private static String _myAddress;

    public AddressAdapter(Activity activity, List<Object> addressList) {
        this.activity = activity;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_address, parent, false);

        return new AddressAdapter.MyViewHolder(itemView);
    }

    private MyViewHolder previousHolder;

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final HashMap<String, Object> addressDetails = (HashMap<String, Object>) addressList.get(position);

        if (addressDetails.containsKey(Constants.name)) {
            holder.tvName.setText(addressDetails.get(Constants.name).toString());
        } else {
            holder.tvName.setText(SharePref.getUSerName(activity));
        }

        final String address = addressDetails.get(Constants.flatNo) + "\n" +
                addressDetails.get(Constants.street) + "\n" +
                addressDetails.get(Constants.locality) + "\n" +
                addressDetails.get(Constants.city) + ", " + addressDetails.get(Constants.state) + " - " + addressDetails.get(Constants.pincode) +
                "\nMobile: " + addressDetails.get(Constants.phone);

        holder.tvAddress.setText(address);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousHolder.rbAddress.setChecked(false);

                holder.rbAddress.setChecked(true);
                previousHolder = holder;
                _myAddress = address;
            }
        });

        holder.rbAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousHolder != holder)
                    previousHolder.rbAddress.setChecked(false);

                holder.rbAddress.setChecked(true);
                previousHolder = holder;
                _myAddress = address;
            }
        });

        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (position == 0) {
            holder.rbAddress.setChecked(true);
            previousHolder = holder;
            _myAddress = address;
        }
    }

    public static String getAddress() {
        return _myAddress;
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton rbAddress;
        TextView tvName, tvAddress;
        ImageView ivEdit, ivDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            rbAddress = itemView.findViewById(R.id.rbAddress);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivEdit = itemView.findViewById(R.id.ivAddressEdit);
            ivDelete = itemView.findViewById(R.id.ivAddressDelete);
        }
    }
}
