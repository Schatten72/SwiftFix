package com.romega_swiftfix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackRepairmenActivity extends AppCompatActivity implements OnMapReadyCallback {

     private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_repairmen);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
    map = googleMap;

        LatLng latLng = new LatLng(7.323915793793079, 80.59453886157856);

        map.addMarker(new MarkerOptions().position(latLng).title("Location"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }


}