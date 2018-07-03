package com.zero.capsule.meds.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.zero.capsule.meds.R;
import com.zero.capsule.meds.utils.KeyboardUtils;


public class SearchMedicineActivity extends AppCompatActivity {

    String TAG = SearchMedicineActivity.class.getSimpleName();
    Activity activity;
    EditText etSearchMedicine;
    RecyclerView rvMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);

        activity = this;

        etSearchMedicine = findViewById(R.id.etSearchMedicine);
        rvMedicine = findViewById(R.id.rvMedicine);
    }

    public void searchMedicine(View view) {
        new KeyboardUtils().hideSoftKeyboard(activity);
        String searchField = etSearchMedicine.getText().toString().trim();
    }

}
