package com.example.octav.aarhusfitness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.octav.aarhusfitness.model.FitnessApiResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
                            if (location != null) {
                                Log.i("Location", location.getLatitude() + "" + location.getLongitude());
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
            public void onResponse(Call<FitnessApiResponse> call, Response<FitnessApiResponse> response) {
                List<FitnessApiResponse.Feature> features = response.body().getFeatures();
                              if (features != null) {
                    for (FitnessApiResponse.Feature feature : features) {
                        List<Double> coordinates = feature.getGeometry().getCoordinates();
                        LatLng latLng = new LatLng(coordinates.get(1), coordinates.get(0));
                        MarkerOptions marker = new MarkerOptions()
                                .position(latLng)
                                .title("Fitness place")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.fitness_icon))
                                .snippet(feature.getProperties().getNavn());
                        mMap.addMarker(marker);
                    }
                    LatLng aarhus = new LatLng(56.162939, 10.203921);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aarhus, 10));
                }
            }

            @Override
            public void onFailure(Call<FitnessApiResponse> call, Throwable t) {
                Log.e("ERROR", t.getMessage());
            }
        });
    }

}
