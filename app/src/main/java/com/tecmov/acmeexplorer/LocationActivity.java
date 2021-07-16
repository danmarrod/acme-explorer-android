package com.tecmov.acmeexplorer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.tecmov.acmeexplorer.resttypes.WeatherResponse;
import com.tecmov.acmeexplorer.resttypes.WeatherRetrofitInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;
    private TextView location;
    private TextView location_weather_temp;
    private GoogleMap googleMap;
    private Location userLocation;
    private Retrofit retrofit;
    private SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        location = findViewById(R.id.location);
        location_weather_temp = findViewById(R.id.location_weather_temp);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Snackbar.make(location, R.string.location_rationale, Snackbar.LENGTH_LONG).setAction(R.string.location_rationale_ok, view ->
                        ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION)
                ).show();
            } else {
                ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
            }
        } else {
            startLocationServices();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationServices();
            } else {
                Toast.makeText(this, R.string.location_cancel, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationServices() {

        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(this);

        /*locationServices.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userLocation = task.getResult();
                Log.i("Acme-Explorer", "Location: " + userLocation.getLatitude() + "," + userLocation.getLongitude() + "," + userLocation.getAccuracy());
                supportMapFragment.getMapAsync(this);
            } else {
                Log.i("Acme-Explorer", "Location error: " + task.getResult());
            }
        });*/

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationServices.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null || locationResult.getLastLocation() == null || !locationResult.getLastLocation().hasAccuracy()) {
                Toast.makeText(LocationActivity.this, R.string.location_gps, Toast.LENGTH_LONG).show();
                return;
            }
            userLocation = locationResult.getLastLocation();
            Log.i("Acme-Explorer", "Location: " + userLocation.getLatitude() + "," + userLocation.getLongitude() + "," + userLocation.getAccuracy());
            supportMapFragment.getMapAsync(LocationActivity.this);
        }
    };


    public void stopService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng location;

        location = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(location).title("ACTUAL USER LOCATION"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 11.0f));

        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrentWeather((float) userLocation.getLatitude(), (float) userLocation.getLongitude(), getString(R.string.open_weather_map_api_key), "Celsius");
        response.enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    location_weather_temp.setText("TEMPERATURE (" + response.body().getName() + ")" + response.body().getMain().getTemp() + " C");
                    Log.i("Acme-Explorer", "Actual temperature is " + response.body().getMain().getTemp());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("Acme-Explorer", "REST: calling weather error. " + t.getMessage());
            }
        });
    }
}