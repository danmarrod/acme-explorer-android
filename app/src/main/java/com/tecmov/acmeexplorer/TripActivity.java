package com.tecmov.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.tecmov.acmeexplorer.entity.Trip;

public class TripActivity extends AppCompatActivity {

    private TextView textViewPriceTrip, textViewStartedDateTrip, textViewFinishedDateTrip, textViewTitleTrip, textViewDescriptionTrip, textViewTickerTrip;
    private ImageView imageViewTrip;
    private Trip trip;
    private Button buttonBuyTrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        imageViewTrip = findViewById(R.id.imageViewDetailsTrip);
        textViewPriceTrip = findViewById(R.id.textViewPriceTrip);
        textViewStartedDateTrip = findViewById(R.id.textViewStartedDateTrip);
        textViewFinishedDateTrip = findViewById(R.id.textViewFinishedDateTrip);
        textViewDescriptionTrip = findViewById(R.id.textViewDescriptionTrip);
        textViewTitleTrip = findViewById(R.id.textViewTitleTrip);
        textViewTickerTrip = findViewById(R.id.textViewTickerTrip);
        buttonBuyTrip = findViewById(R.id.buttonBuyTrip);

        buttonBuyTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "PAID TRIP:  " + trip.getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });

        try {
            trip = (Trip) getIntent().getSerializableExtra("Trip");
            if (trip != null) {
                textViewPriceTrip.setText(trip.getPrice().toString());
                textViewStartedDateTrip.setText(trip.getStartedDate().toString());
                textViewFinishedDateTrip.setText(trip.getFinishedDate().toString());
                textViewDescriptionTrip.setText(trip.getDescription());
                textViewTitleTrip.setText(trip.getTitle());
                textViewTickerTrip.setText(trip.getTicker());
                Picasso.get()
                        .load(trip.getPicture())
                        .placeholder(android.R.drawable.ic_dialog_map)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(imageViewTrip);
            }
            if (!trip.isLike())
                buttonBuyTrip.setEnabled(false);

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

}