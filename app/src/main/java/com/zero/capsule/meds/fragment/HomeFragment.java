package com.zero.capsule.meds.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.activity.BuyMedicinesActivity;
import com.zero.capsule.meds.activity.MainActivity;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.model.CarouselModel;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("unchecked")
public class HomeFragment extends Fragment {


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Capsule Meds");
        /* Content */
        buyMedicines();
        getCarouselImage();
        return rootView;
    }

    /*---------------------------------- Upload prescription -------------------------------------*/
    private void buyMedicines() {
        CardView buyMedicine = rootView.findViewById(R.id.buyMedicine);
        buyMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuyMedicinesActivity.class));
            }
        });

    }

    /*------------------------------------ Carousel View -----------------------------------------*/
    private void getCarouselImage() {
        new FirebaseDB()
                .carouselListRT
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap<String, Object> carouselMap = (HashMap<String, Object>) dataSnapshot.getValue();

                            final List<CarouselModel> carouselList = new ArrayList<>();

                            for (String key : carouselMap.keySet()) {
                                HashMap<String, Object> carousel = (HashMap<String, Object>) carouselMap.get(key);

                                String url = carousel.get(Constants.url).toString();
                                String link = carousel.get(Constants.link).toString();
                                carouselList.add(new CarouselModel(url, link));
                            }

                            if (carouselList.size() > 0) {
                                SliderLayout sliderShow = rootView.findViewById(R.id.imgSlider);
                                PagerIndicator customIndicator = rootView.findViewById(R.id.customIndicator);

                                sliderShow.setCustomIndicator(customIndicator);

                                for (int i = 0; i < carouselList.size(); i++) {

                                    final CarouselModel carouselModel = carouselList.get(i);

                                    DefaultSliderView sliderView = new DefaultSliderView(getActivity());

                                    sliderView.empty(R.drawable.placeholder_banners);
                                    sliderView.image(carouselModel.getUrl());

                                    sliderShow.addSlider(sliderView);

                                    sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            try {
                                                // TODO: 05/05/18
                                                DialogUtils.appToastShort(getActivity(), carouselModel.getLink());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        DialogUtils.appToastShort(getActivity(), databaseError.getMessage());
                    }
                });
    }

}
