package com.tecmov.acmeexplorer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmov.acmeexplorer.adapters.TripAdapter;
import com.tecmov.acmeexplorer.adapters.TripListAdapter;
import com.tecmov.acmeexplorer.entity.Trip;

import java.util.ArrayList;
import java.util.List;

public class ListMyTripsActivity extends AppCompatActivity {

    RecyclerView incoming_recycler_view;
    private Switch switchColumn;
    private GridLayoutManager gridLayoutManager;
    private TripListAdapter tripListAdapter;
    List<Trip> myTripsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trips);
        switchColumn = findViewById(R.id.switchColumn);
        incoming_recycler_view = findViewById(R.id.TripListAdapter);

        /*if (Constants.chargedTrips != null) {
            for (Trip trip : Constants.chargedTrips) {
                if (trip.isLike())
                    myTripsList.add(trip);
            }
        }*/

        switchColumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int spanCount = isChecked ? 2 : 1;
                gridLayoutManager.setSpanCount(spanCount);
            }
        });

        tripListAdapter = new TripListAdapter(true);
        gridLayoutManager = new GridLayoutManager(this, 1);
        incoming_recycler_view.setLayoutManager(gridLayoutManager);
        incoming_recycler_view.setAdapter(tripListAdapter);

        tripListAdapter.setDataChangedListener(() -> {
            if (tripListAdapter.getItemCount() > 0) {
                //TODO add image background
                incoming_recycler_view.setVisibility(View.VISIBLE);
                Toast.makeText(this, "TOTAL TRIPS: " + tripListAdapter.getItemCount() + " elements", Toast.LENGTH_SHORT).show();
            } else {
                incoming_recycler_view.setVisibility(View.GONE);
            }
        });

    }

    public void FilterView(View view) {

    }

    // delete listener registration in dabatase updates
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(tripListAdapter != null && tripListAdapter.listenerRegistration != null)
            tripListAdapter.listenerRegistration.remove();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
