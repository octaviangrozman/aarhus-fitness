package com.example.octav.aarhusfitness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.octav.aarhusfitness.model.FitnessApiResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends FragmentActivity implements
        OnMapReadyCallback {

    public static final double AARHUS_LATITUDE = 56.162939;
    public static final double AARHUS_LONGITUDE = 10.203921;
    public static final int MAP_ZOOM = 10;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 42);
            Log.i("permission", "requesting");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Log.i("LOCATION", location.toString());
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions locationMarker = new MarkerOptions().position(latLng).title("You are here!");
                                mMap.addMarker(locationMarker);
                            }
                        }
                    });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(HomeActivity.this, FitnessPlaceActivity.class);
                intent.putExtra("fitnessPlace", marker.getSnippet());
                startActivity(intent);
            }
        });

        App.getApi().getData().enqueue(new Callback<FitnessApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<FitnessApiResponse> call, @NonNull Response<FitnessApiResponse> response) {
                FitnessApiResponse responseBody = response.body();
                if (responseBody != null) {
                    List<FitnessApiResponse.Feature> features = responseBody.getFeatures();
                    if (features != null) {
                        for (FitnessApiResponse.Feature feature : features) {
                            List<Double> coordinates = feature.getGeometry().getCoordinates();
                            LatLng latLng = new LatLng(coordinates.get(1), coordinates.get(0));
                            MarkerOptions marker = createMarker(feature, latLng);
                            mMap.addMarker(marker);
                        }
                        LatLng aarhusCoordinates = new LatLng(AARHUS_LATITUDE, AARHUS_LONGITUDE);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aarhusCoordinates, MAP_ZOOM));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FitnessApiResponse> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    @NonNull
    private MarkerOptions createMarker(FitnessApiResponse.Feature feature, LatLng latLng) {
        return new MarkerOptions()
                                    .position(latLng)
                                    .title("JOIN fitness place")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.fitness_icon))
                                    .snippet(feature.getProperties().getNavn());
    }

}
