package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tecmov.acmeexplorer.adapters.TripListAdapter;

public class TripList extends AppCompatActivity {

    private TripListAdapter tripListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        tripListAdapter = new TripListAdapter("userId");
        RecyclerView incoming_recycler_view = findViewById(R.id.TripListAdapter);
        incoming_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

    // back button
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    public void FilterView(View view) {

    }
}