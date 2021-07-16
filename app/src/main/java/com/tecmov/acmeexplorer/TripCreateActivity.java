package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.utils.Util;

import java.util.Calendar;
import java.util.UUID;

public class TripCreateActivity extends AppCompatActivity {

    private static final String PREF_UNIQUE_ID = "TK";
    private static final String DEFAULT_PICTURE = "https://iconape.com/wp-content/png_logo_vector/beach-tour-logo.png";
    private Button trip_create_button;
    private Trip trip;

    Calendar calendar = Calendar.getInstance();
    int year, month, day, hour, minute;

    private TextInputLayout trip_create_ticker;
    private AutoCompleteTextView trip_create_ticker_et;

    private TextInputLayout trip_create_title;
    private AutoCompleteTextView trip_create_title_et;

    private TextInputLayout trip_create_description;
    private AutoCompleteTextView trip_create_description_et;

    private TextInputLayout trip_create_start_date;
    private AutoCompleteTextView trip_create_start_date_et;

    private TextInputLayout trip_create_finish_date;
    private AutoCompleteTextView trip_create_finish_date_et;

    private TextInputLayout trip_create_picture;
    private AutoCompleteTextView trip_create_picture_et;

    private TextInputLayout trip_create_price;
    private AutoCompleteTextView trip_create_price_et;

    private TextInputLayout trip_create_latitude;
    private AutoCompleteTextView trip_create_latitude_et;

    private TextInputLayout trip_create_longitude;
    private AutoCompleteTextView trip_create_longitude_et;

    private FirebaseAuth mAuth;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_create);

        mAuth = FirebaseAuth.getInstance();
        firestoreService = firestoreService.getServiceInstance();

        trip_create_button = findViewById(R.id.trip_create_button);
        //trip_create_ticker = findViewById(R.id.trip_create_ticker);
        //trip_create_ticker_et = findViewById(R.id.trip_create_ticker_et);
        trip_create_title = findViewById(R.id.trip_create_title);
        trip_create_title_et = findViewById(R.id.trip_create_title_et);
        trip_create_description = findViewById(R.id.trip_create_description);
        trip_create_description_et = findViewById(R.id.trip_create_description_et);
        trip_create_start_date = findViewById(R.id.trip_create_start_date);
        trip_create_start_date_et = findViewById(R.id.trip_create_start_date_et);
        trip_create_finish_date = findViewById(R.id.trip_create_finish_date);
        trip_create_finish_date_et = findViewById(R.id.trip_create_finish_date_et);
        trip_create_picture = findViewById(R.id.trip_create_picture);
        trip_create_picture_et = findViewById(R.id.trip_create_picture_et);
        trip_create_price = findViewById(R.id.trip_create_price);
        trip_create_price_et = findViewById(R.id.trip_create_price_et);
        trip_create_latitude = findViewById(R.id.trip_create_latitude);
        trip_create_latitude_et = findViewById(R.id.trip_create_latitude_et);
        trip_create_longitude = findViewById(R.id.trip_create_longitude);
        trip_create_longitude_et = findViewById(R.id.trip_create_longitude_et);

        trip_create_button.setOnClickListener(l -> this.attemptTripCreate());
    }

    private void attemptTripCreate() {

        trip_create_title.setError(null);
        trip_create_description.setError(null);
        trip_create_price.setError(null);
        trip_create_start_date.setError(null);
        trip_create_finish_date.setError(null);
        trip_create_picture.setError(null);
        trip_create_latitude.setError(null);
        trip_create_longitude.setError(null);
        boolean fails = false;

        if (trip_create_title_et.getText().length() == 0) {
            trip_create_title.setErrorEnabled(true);
            trip_create_title.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (trip_create_description_et.getText().length() == 0) {
            trip_create_description.setErrorEnabled(true);
            trip_create_description.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (trip_create_price_et.getText().length() == 0) {
            trip_create_price.setErrorEnabled(true);
            trip_create_price.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (trip_create_start_date_et.getText().length() == 0) {
            trip_create_start_date.setErrorEnabled(true);
            trip_create_start_date.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (trip_create_finish_date_et.getText().length() == 0) {
            trip_create_finish_date.setErrorEnabled(true);
            trip_create_finish_date.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (Util.StringToDate(trip_create_start_date_et.getText().toString()).after(Util.StringToDate(trip_create_finish_date_et.getText().toString()))) {
            trip_create_start_date.setErrorEnabled(true);
            trip_create_finish_date.setErrorEnabled(true);
            trip_create_start_date.setError(getString(R.string.trip_create_verication_date_before));
            fails = true;
        }

        if (trip_create_latitude_et.getText().length() == 0) {
            trip_create_latitude.setErrorEnabled(true);
            trip_create_latitude.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }

        if (trip_create_longitude_et.getText().length() == 0) {
            trip_create_longitude.setErrorEnabled(true);
            trip_create_longitude.setError(getString(R.string.trip_create_verication_empty));
            fails = true;
        }


        if (!fails)
            this.tripCreate();

    }

    private void tripCreate() {

        trip = new Trip();
        trip.setTitle(trip_create_title_et.getText().toString());
        trip.setDescription(trip_create_description_et.getText().toString());
        trip.setPrice(Double.parseDouble(trip_create_price_et.getText().toString()));
        trip.setStartedDate(Util.StringToDate(trip_create_start_date_et.getText().toString()));
        trip.setFinishedDate(Util.StringToDate(trip_create_finish_date_et.getText().toString()));
        trip.setTicker(generateTicker());
        trip.setLike(false);
        trip.setLatitude(Double.parseDouble(trip_create_latitude_et.getText().toString()));
        trip.setLatitude(Double.parseDouble(trip_create_longitude_et.getText().toString()));

        if (trip_create_picture_et.getText().toString().isEmpty())
            trip.setPicture(DEFAULT_PICTURE);
        else
            trip.setPicture(trip_create_picture_et.getText().toString());

        firestoreService.saveTrip(trip, task -> {
            if (task.isSuccessful()) {
                DocumentReference documentReference = task.getResult();
                // listener for reading, before this one, it's for writing
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        // documentSnapshot get properties values of the object
                        Trip trip = documentSnapshot.toObject(Trip.class);
                        Log.i("Acme-Explorer", "Trip created feedback: " + trip.toString());
                    }
                });
                Log.i("Acme-Explorer", "Trip created successfully " + task.getResult().getId());
                Toast.makeText(TripCreateActivity.this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TripCreateActivity.this, MainActivity.class));

            } else {
                Log.i("Acme-Explorer", "Error creating trip");
                Toast.makeText(TripCreateActivity.this, "Error creating trip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateTicker() {
        String result;
        result = PREF_UNIQUE_ID + UUID.randomUUID().toString().substring(0,6);
        return result;
    }

    public void setStartDate(View view) {

        if (trip_create_start_date_et.getText().toString().isEmpty()) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String split[] = trip_create_start_date_et.getText().toString().split("/");
            day = Integer.valueOf(split[0]);
            month = Integer.valueOf(split[1]) - 1;
            year = Integer.valueOf(split[2]);
        }

        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                        trip_create_start_date_et.setText(dd + "/" + (mm + 1) + "/" + yy);
                    }
                }, year, month, day);
        pickerDialog.show();

    }

    public void setFinishDate(View view) {

        if (trip_create_finish_date_et.getText().toString().isEmpty()) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String split[] = trip_create_finish_date_et.getText().toString().split("/");
            day = Integer.valueOf(split[0]);
            month = Integer.valueOf(split[1]) - 1;
            year = Integer.valueOf(split[2]);
        }

        DatePickerDialog pickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int yy, int mm, int dd) {
                        trip_create_finish_date_et.setText(dd + "/" + (mm + 1) + "/" + yy);
                    }
                }, year, month, day);
        //pickerDialog.getDatePicker().setMinDate(textViewDateIn);
        pickerDialog.show();
    }

    public void setPrice(View view) {

        if (!trip_create_price_et.getText().toString().contains(".")) {
            trip_create_price_et.setText(trip_create_price_et.getText().toString() + ".00");
        }


    }

}