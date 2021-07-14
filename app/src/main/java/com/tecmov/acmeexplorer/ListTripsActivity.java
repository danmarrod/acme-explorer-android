package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.tecmov.acmeexplorer.adapters.TripAdapter;
import com.tecmov.acmeexplorer.adapters.TripListAdapter;
import com.tecmov.acmeexplorer.entity.Trip;
import com.tecmov.acmeexplorer.utils.Util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ListTripsActivity extends AppCompatActivity {

    private static final int FILTER = 1;
    RecyclerView incoming_recycler_view;
    private static List<Trip> tripsList;
    private Switch switchColumn;
    private GridLayoutManager gridLayoutManager;
    private TripListAdapter tripListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_trips);

        switchColumn = findViewById(R.id.switchColumn);
        incoming_recycler_view = findViewById(R.id.TripListAdapter);


        if (Constants.chargedTrips == null)
            Constants.chargedTrips = Trip.generateTrips(5);

        tripsList = Constants.chargedTrips;

        switchColumn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int spanCount = isChecked ? 2 : 1;
                gridLayoutManager.setSpanCount(spanCount);
            }
        });

        tripListAdapter = new TripListAdapter();
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

        tripListAdapter.setErrorListener(error -> {
            incoming_recycler_view.setVisibility(View.GONE);
        });

        setupActionBar();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void FilterView(View view) {
        Intent pickFilterListIntent = new Intent(view.getContext(), FilterActivity.class);
        startActivityForResult(pickFilterListIntent, FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTER) {
            if (resultCode == RESULT_OK) {
                String filter_priceMin = data.getStringExtra("filter_priceMin");
                String filter_priceMax = data.getStringExtra("filter_priceMax");
                String filter_startedDate = data.getStringExtra("filter_startedDate");
                String filter_finishedDate = data.getStringExtra("filter_finishedDate");
                this.refreshRecycle(filter_startedDate, filter_finishedDate, filter_priceMin, filter_priceMax);
            }
        }
    }

    private void refreshRecycle(String filter_startedDate, String filter_finishedDate, String filter_priceMin, String filter_priceMax) {

        List<Trip> filterTripsList = new ArrayList<Trip>();
        Long finishedDate, startedDate;
        Double priceMin, priceMax;

        if (filter_finishedDate.contentEquals("FinishDate"))
            finishedDate = Util.Calendar2long(new GregorianCalendar(2021, 12, 31));
        else
            finishedDate = Util.StringToLong(filter_finishedDate);

        if (filter_startedDate.contentEquals("StartDate"))
            startedDate = Util.Calendar2long(new GregorianCalendar(2021, 01, 01));
        else
            startedDate = Util.StringToLong(filter_startedDate);

        if (filter_priceMin.contentEquals("PriceMin"))
            priceMin = 0.0;
        else
            priceMin = Double.parseDouble(filter_priceMin);

        if (filter_priceMax.contentEquals("PriceMax"))
            priceMax = 10000.0;
        else
            priceMax = Double.parseDouble(filter_priceMax);


        for (Trip trip : tripsList) {
            if (trip.getStartedDate().getTime() / 1000 >= startedDate)
                if (trip.getFinishedDate().getTime() / 1000 <= finishedDate)
                    if (trip.getPrice() >= priceMin)
                        if (trip.getPrice() <= priceMax)
                            filterTripsList.add(trip);
        }

        incoming_recycler_view.setAdapter(new TripAdapter(filterTripsList));

        Toast.makeText(this, "TOTAL TRIPS: " + filterTripsList.size() + " elements", Toast.LENGTH_SHORT).show();


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