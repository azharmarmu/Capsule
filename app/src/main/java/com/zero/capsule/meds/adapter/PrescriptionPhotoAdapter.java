package com.zero.capsule.meds.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zero.capsule.meds.listeners.PhotoListListener;
import com.squareup.picasso.Picasso;
import com.zero.capsule.meds.R;

import java.util.List;


/**
 * Created by azharuddin on 3/8/17.
 */

@SuppressWarnings("unchecked")
public class PrescriptionPhotoAdapter extends RecyclerView.Adapter<PrescriptionPhotoAdapter.MyViewHolder> {

    private List<String> photosList;
    private Activity activity;
    private static PhotoListListener photoListListener;

    public PrescriptionPhotoAdapter(Activity activity, List<String> photosList) {
        this.photosList = photosList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_photos, parent, false);

        return new PrescriptionPhotoAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            Uri image = Uri.parse(photosList.get(position));

            Picasso.with(activity)
                    .load(image)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerInside()
                    .fit()
                    .into(holder.photo);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoListListener.removePhoto(holder.getAdapterPosition());
                }
            });
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView photo, delete;

        MyViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public void setPhotoListListener(PhotoListListener photoListListener) {
        PrescriptionPhotoAdapter.photoListListener = photoListListener;
    }
}
