package com.zero.capsule.meds.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zero.capsule.meds.R;
import com.zero.capsule.meds.utils.Constants;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    private Activity activity;
    private List<HashMap<String, Object>> orders;

    public MyOrdersAdapter(Activity activity, List<HashMap<String, Object>> orders) {
        this.activity = activity;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_orders, parent, false);

        return new MyOrdersAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        HashMap<String, Object> orderMap = orders.get(position);

        String orderStatus = "Order";
        if (orderMap.get(Constants.status).toString().equalsIgnoreCase(Constants.pending)) {
            orderStatus = orderStatus + " To be Confirmed";
        } else if (orderMap.get(Constants.status).toString().equalsIgnoreCase(Constants.completed)) {
            orderStatus = orderStatus + " Delivered";
        } else {
            orderStatus = orderStatus + orderMap.get(Constants.status);
        }
        holder.orderStatus.setText(orderStatus);


        String orderID = "Order id: #" + orderMap.get(Constants.orderID);
        holder.orderID.setText(orderID);


        Date date = (Date) orderMap.get(Constants.timeStamp);
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        String orderDate = format1.format(date);
        holder.orderDate.setText(orderDate);


        if (orderMap.containsKey(Constants.prescriptions)) {
            holder.orderItems.setVisibility(View.GONE);
            holder.orderMore.setVisibility(View.GONE);
        } else {
            String orderItems = "";
            holder.orderItems.setText(orderItems);

            String orderMore = "(" + ")";
            holder.orderMore.setText(orderMore);
        }


        String orderAgainHelp = "";
        if (orderMap.get(Constants.status).toString().equalsIgnoreCase(Constants.cancelled)) {
            orderAgainHelp = "NEED HELP ?";
        } else if (orderMap.get(Constants.status).toString().equalsIgnoreCase(Constants.completed)) {
            orderAgainHelp = "ORDER AGAIN";
        } else {
            holder.orderAgainHelp.setVisibility(View.GONE);
        }

        holder.orderAgainHelp.setText(orderAgainHelp);
        holder.orderAgainHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderStatus, orderID, orderDate, orderItems, orderMore, orderAgainHelp;


        MyViewHolder(View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            orderID = itemView.findViewById(R.id.orderID);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderItems = itemView.findViewById(R.id.orderItems);
            orderMore = itemView.findViewById(R.id.orderMore);
            orderAgainHelp = itemView.findViewById(R.id.orderAgainHelp);
        }
    }
}
