package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.utils.Util;

public class TripCreateActivity extends AppCompatActivity {

    private Button trip_create_button;
    private Trip trip;

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

    private FirebaseAuth mAuth;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_create);

        mAuth = FirebaseAuth.getInstance();
        firestoreService = firestoreService.getServiceInstance();

        trip_create_button = findViewById(R.id.trip_create_button);
        trip_create_ticker = findViewById(R.id.trip_create_ticker);
        trip_create_ticker_et = findViewById(R.id.trip_create_ticker_et);
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

        trip_create_button.setOnClickListener(l -> this.attemptTripCreate());
    }

    private void attemptTripCreate() {

        trip = new Trip();

        trip.setTitle(trip_create_title_et.getText().toString());
        trip.setDescription(trip_create_description_et.getText().toString());
        trip.setPrice(Double.parseDouble(trip_create_price_et.getText().toString()));
        trip.setStartedDate(Util.StringToDate(trip_create_start_date_et.getText().toString()));
        trip.setFinishedDate(Util.StringToDate(trip_create_finish_date_et.getText().toString()));
        trip.setPicture(trip_create_picture_et.getText().toString());
        trip.setTicker(trip_create_ticker_et.getText().toString());
        trip.setLike(false);

        this.tripCreate(trip);

    }

    private void tripCreate(Trip trip) {

        firestoreService.saveTrip(trip, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = task.getResult();
                    // listener for reading, before this one, it's for writing
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                // documentSnapshot get properties values of the object
                                Trip trip = documentSnapshot.toObject(Trip.class);
                                Log.i("Acme-Explorer", "Trip created feedback: " + trip.toString());
                            }

                        }
                    });
                    Log.i("Acme-Explorer", "Trip created successfully " + task.getResult().getId());
                    Toast.makeText(TripCreateActivity.this, "Trip created successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TripCreateActivity.this, MainActivity.class));

                } else {
                    Log.i("Acme-Explorer", "Error creating trip");
                    Toast.makeText(TripCreateActivity.this, "Error creating trip", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}