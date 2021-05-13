package com.tecmov.acmeexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmov.acmeexplorer.adapters.TripAdapter;
import com.tecmov.acmeexplorer.entity.Trip;

import java.util.ArrayList;
import java.util.List;

public class ListMyTripsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private Switch switchColumn;
    private GridLayoutManager gridLayoutManager;
    List<Trip> myTripsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trips);
        switchColumn = findViewById(R.id.switchColumn);

        if (Constants.chargedTrips != null) {
            for (Trip trip : Constants.chargedTrips) {
                if (trip.isLike())
                    myTripsList.add(trip);
            }
        }

        switchColumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int spanCount = isChecked ? 2 : 1;
                gridLayoutManager.setSpanCount(spanCount);
            }
        });

        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new TripAdapter(myTripsList));
        recyclerView.setLayoutManager(gridLayoutManager);

        Toast.makeText(this, "TOTAL TRIPS: " + myTripsList.size() + " elements", Toast.LENGTH_SHORT).show();
    }

    public void FilterView(View view) {

    }


}
