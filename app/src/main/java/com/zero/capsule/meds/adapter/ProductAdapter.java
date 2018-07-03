package com.zero.capsule.meds.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zero.capsule.meds.utils.Constants;
import com.squareup.picasso.Picasso;
import com.zero.capsule.meds.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Activity activity;
    private List<HashMap<String, Object>> productList;

    public ProductAdapter(Activity activity, List<HashMap<String, Object>> productList) {
        this.activity = activity;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_products, parent, false);

        return new ProductAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final HashMap<String, Object> productDetails = productList.get(position);

        if (productDetails.containsKey(Constants.productImageLink)) {
            Picasso.with(activity)
                    .load(productDetails.get(Constants.productImageLink).toString())
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerInside()
                    .fit()
                    .into(holder.productImage);
        }

        if (productDetails.containsKey(Constants.productName)) {
            holder.productName.setText(productDetails.get(Constants.productName).toString());
        }

        double offer = 0;
        if (productDetails.containsKey(Constants.productOffer)) {
            offer = Double.parseDouble(productDetails.get(Constants.productOffer).toString());
        }

        double MRP = 0;
        if (productDetails.containsKey(Constants.productMRP)) {
            MRP = Double.parseDouble(productDetails.get(Constants.productMRP).toString());
        }

        if (offer > 0) {
            holder.productOffer.setText(Html.fromHtml("MRP" + " <strike>\u20B9" + MRP + "</strike> " + offer + "% OFF"));
        }

        double rate = Math.round(MRP - ((MRP * offer) / 100));

        String rates = "\u20B9" + rate;
        holder.productRate.setText(rates);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productOffer, productRate;

        MyViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productOffer = itemView.findViewById(R.id.tvProductOffer);
            productRate = itemView.findViewById(R.id.tvProductRate);
        }
    }
}
