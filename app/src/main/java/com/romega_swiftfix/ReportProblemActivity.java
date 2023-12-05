package com.romega_swiftfix;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ReportProblemActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap map;
    private LatLng selectedLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        // Set App Bar
        AppBar.setAppBar(getSupportFragmentManager(), R.id.fragmentContainerView3);

        // Set Bottom Navigation
        BottomNavigation.setNavigationBar(getSupportFragmentManager(), R.id.fragmentContainerView2);

        Button selectLocationButton = findViewById(R.id.buttonSelectLocation);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });


    }


    private void showMap() {
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.mapContainer, mapFragment);
            ft.addToBackStack(null);
            ft.commit();
            fm.executePendingTransactions();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(7.4373449337030095, 80.43210958509296);
        map.addMarker(new MarkerOptions().position(latLng).title("Location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

        // Hide the button and show the map
        Button selectLocationButton = findViewById(R.id.buttonSelectLocation);
        selectLocationButton.setVisibility(View.GONE);

        View mapContainer = findViewById(R.id.mapContainer);
        mapContainer.setVisibility(View.VISIBLE);
    }


}