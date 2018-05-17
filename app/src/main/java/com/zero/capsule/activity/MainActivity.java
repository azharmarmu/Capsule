package com.zero.capsule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zero.capsule.R;
import com.zero.capsule.adapter.ProductAdapter;
import com.zero.capsule.firebase.FirebaseDB;
import com.zero.capsule.model.CarouselModel;
import com.zero.capsule.utils.Constants;
import com.zero.capsule.utils.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Constants.AUTH.getCurrentUser() != null) {
            /* Toolbar */
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            setLocation();

            /* Content */
            uploadPrescription();
            getCarouselImage();
            getPopularProducts();

            /* Navigation Drawer */
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        } else {
            /* Start index activity */
            Intent indexActivity = new Intent(MainActivity.this, IndexActivity.class);
            indexActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(indexActivity);
            finish();
        }
    }

    /*------------------------------------ Set Location ------------------------------------------*/
    private void setLocation() {
        TextView tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.appToastShort(MainActivity.this, "Currently we deliver only vellore");
            }
        });
    }

    /*---------------------------------- Upload prescription -------------------------------------*/
    private void uploadPrescription() {
        CardView uploadPrescription = findViewById(R.id.uploadPrescription);
        uploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadPrescriptionActivity.class));
            }
        });

    }

    /*------------------------------------ Carousel View -----------------------------------------*/
    private void getCarouselImage() {
        new FirebaseDB().carouselListRT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
                        SliderLayout sliderShow = findViewById(R.id.imgSlider);
                        PagerIndicator customIndicator = findViewById(R.id.customIndicator);

                        sliderShow.setCustomIndicator(customIndicator);

                        for (int i = 0; i < carouselList.size(); i++) {

                            final CarouselModel carouselModel = carouselList.get(i);

                            DefaultSliderView sliderView = new DefaultSliderView(MainActivity.this);

                            sliderView.empty(R.drawable.placeholder_banners);
                            sliderView.image(carouselModel.getUrl());

                            sliderShow.addSlider(sliderView);

                            sliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    try {
                                        // TODO: 05/05/18
                                        DialogUtils.appToastShort(MainActivity.this, carouselModel.getLink());
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
            public void onCancelled(DatabaseError databaseError) {
                DialogUtils.appToastShort(MainActivity.this, databaseError.getMessage() + " ooo");
            }
        });
    }

    /*------------------------------------ Products List -----------------------------------------*/
    private void getPopularProducts() {
        new FirebaseDB().popularProductsListFS
                .orderBy(Constants.popularity, Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(Constants.TAG, "Listen failed.", e);
                            return;
                        }

                        List<HashMap<String, Object>> popularProductsList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                            popularProductsList.add((HashMap<String, Object>) doc.getData());
                        }

                        populatePopularProducts(popularProductsList);
                    }
                });
    }

    private void populatePopularProducts(List<HashMap<String, Object>> popularProductsList) {
        RecyclerView.Adapter adapter = new ProductAdapter(this, popularProductsList);
        final RecyclerView recyclerView = findViewById(R.id.rvProducts);
        recyclerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);
    }


    /*----------------------------------- Navigation Drawer --------------------------------------*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_share:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
