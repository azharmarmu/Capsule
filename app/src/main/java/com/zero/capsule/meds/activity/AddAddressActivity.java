package com.zero.capsule.meds.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zero.capsule.meds.R;
import com.zero.capsule.meds.firebase.FirebaseDB;
import com.zero.capsule.meds.utils.Constants;
import com.zero.capsule.meds.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddAddressActivity extends AppCompatActivity {

    private final String TAG = AddAddressActivity.class.getSimpleName();

    EditText etFlatNo, etStreet, etPincode, etLandmark, etCity, etState, etName, etPhone;
    TextView tvFlatNoError, tvStreetError, tvPincodeError, tvLocalityError, tvLandmarkError, tvPhoneError;
    AutoCompleteTextView etLocality;
    RadioButton rbHome, rbOffice, rbOther;

    List<String> pincodes = new ArrayList<>();
    List<String> locality = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        toolbar();

        initViews();

        getServingPincode();

        textFieldReader(etFlatNo, tvFlatNoError);
        textFieldReader(etStreet, tvStreetError);
        textFieldReader(etLocality, tvLocalityError);
        textFieldReader(etLandmark, tvLandmarkError);
        textFieldReader(etPhone, tvPhoneError);

        pincodeReader();
    }

    private void textFieldReader(EditText editText, final TextView textView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) textView.setVisibility(View.GONE);
            }
        });
    }

    private void initViews() {
        etFlatNo = findViewById(R.id.etFlatNo);
        etStreet = findViewById(R.id.etStreet);
        etPincode = findViewById(R.id.etPincode);
        etLocality = findViewById(R.id.etLocality);
        etPincode = findViewById(R.id.etPincode);
        etLandmark = findViewById(R.id.etLandmark);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        rbHome = findViewById(R.id.rbHome);
        rbOffice = findViewById(R.id.rbOffice);
        rbOther = findViewById(R.id.rbOther);

        tvFlatNoError = findViewById(R.id.tvFlatNoError);
        tvStreetError = findViewById(R.id.tvStreetError);
        tvPincodeError = findViewById(R.id.tvPincodeError);
        tvLocalityError = findViewById(R.id.tvLocalityError);
        tvLandmarkError = findViewById(R.id.tvLandmarkError);
        tvPhoneError = findViewById(R.id.tvPhoneError);

        rbHome.setChecked(true);
    }

    private void pincodeReader() {

        etPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 6) {
                    tvPincodeError.setVisibility(View.VISIBLE);
                    tvPincodeError.setText(R.string._invalid_pincode);
                } else {
                    if (pincodes.contains(editable.toString())) {
                        tvPincodeError.setVisibility(View.GONE);
                        new PinCodeCallTask().execute(editable.toString());
                    } else {
                        tvPincodeError.setVisibility(View.VISIBLE);
                        tvPincodeError.setText(R.string._city_limit);
                    }
                }
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    class PinCodeCallTask extends AsyncTask<String, Void, Response> {

        String pincode;

        protected okhttp3.Response doInBackground(String... pincode) {
            this.pincode = pincode[0];
            try {
                String url = "http://postalpincode.in/api/pincode/" + this.pincode;
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .addHeader("content-type", "application/json")
                        .build();


                return client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(okhttp3.Response response) {

            try {
                if (response.isSuccessful()) {


                    locality.clear();

                    JSONObject body = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONArray postOffice = (JSONArray) body.get("PostOffice");


                    String city = "";
                    String state = "";
                    for (int i = 0; i < postOffice.length(); i++) {
                        JSONObject valueObject = (JSONObject) postOffice.get(i);
                        locality.add(valueObject.getString("Name"));
                        city = valueObject.getString("District");
                        state = valueObject.getString("State");
                    }

                    etLocality.setEnabled(true);
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddAddressActivity.this,
                            android.R.layout.select_dialog_item, locality
                    );
                    etLocality.setAdapter(adapter);

                    etCity.setText(city);
                    etState.setText(state);
                }
            } catch (Exception e) {
                e.printStackTrace();
                etLocality.setEnabled(true);
                etCity.setEnabled(true);
                etState.setEnabled(true);
            }
        }
    }

    private void getServingPincode() {
        new FirebaseDB().servingPincodeFS
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documentSnapshot = querySnapshot.getDocuments();

                        for (int i = 0; i < documentSnapshot.size(); i++) {
                            pincodes.add(documentSnapshot.get(i).getId());
                        }
                    }
                });
    }


    private void toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void done(View view) {
        String flatNo = etFlatNo.getText().toString();
        String street = etStreet.getText().toString();
        String pincode = etPincode.getText().toString();
        String locality = etLocality.getText().toString();
        String landmark = etLandmark.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();

        String place = "home";

        if (rbOffice.isChecked()) place = "office";
        else if (rbOther.isChecked()) place = "other";

        if (flatNo.isEmpty()) {
            tvFlatNoError.setVisibility(View.VISIBLE);
            tvFlatNoError.setText(R.string._required);
        } else if (street.isEmpty()) {
            tvStreetError.setVisibility(View.VISIBLE);
            tvStreetError.setText(R.string._required);
        } else if (pincode.isEmpty()) {
            tvPincodeError.setVisibility(View.VISIBLE);
            tvPincodeError.setText(R.string._required);
        } else if (locality.isEmpty()) {
            tvLocalityError.setVisibility(View.VISIBLE);
            tvLocalityError.setText(R.string._required);
        } else if (landmark.isEmpty()) {
            tvLandmarkError.setVisibility(View.VISIBLE);
            tvLandmarkError.setText(R.string._required);
        } else if (phone.isEmpty()) {
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText(R.string._required);
        } else if (phone.length() < 10) {
            tvPhoneError.setVisibility(View.VISIBLE);
            tvPhoneError.setText(R.string.valid_phonenumber);
        } else {

            //Error visibility hide
            tvFlatNoError.setVisibility(View.GONE);
            tvPincodeError.setVisibility(View.GONE);
            tvLocalityError.setVisibility(View.GONE);
            tvLandmarkError.setVisibility(View.GONE);
            tvPhoneError.setVisibility(View.GONE);

            HashMap<String, Object> addressMap = new HashMap<>();
            addressMap.put(Constants.flatNo, flatNo);
            addressMap.put(Constants.street, street);
            addressMap.put(Constants.pincode, pincode);
            addressMap.put(Constants.locality, locality);
            addressMap.put(Constants.landmark, landmark);
            addressMap.put(Constants.city, city);
            addressMap.put(Constants.state, state);
            if (!name.isEmpty()) addressMap.put(Constants.name, name);
            addressMap.put(Constants.phone, phone);
            addressMap.put(Constants.place, place);

            new FirebaseDB().userFS
                    .document(Objects.requireNonNull(Constants.AUTH.getUid()))
                    .update(Constants.address + "." + place, addressMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DialogUtils.appToastShort(AddAddressActivity.this, "Address added successfully");
                            finish();
                        }
                    });
        }

    }
}
