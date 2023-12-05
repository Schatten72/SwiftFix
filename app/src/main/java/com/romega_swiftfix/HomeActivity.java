package com.romega_swiftfix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.romega_swiftfix.adapter.SliderImageAdapter;
import com.romega_swiftfix.model.SliderImage;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private int currentPage = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set App Bar
        AppBar.setAppBar(getSupportFragmentManager(), R.id.fragmentContainerView3);

        // Set Bottom Navigation
        BottomNavigation.setNavigationBar(getSupportFragmentManager(), R.id.fragmentContainerView2);

        // Set Image Slider
        setupImageSlider();


        // Set Report Problem Button Click Listener
        Button buttonReportProblem = findViewById(R.id.buttonReportProblem);
        buttonReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Report Problem Activity
                Intent intent = new Intent(HomeActivity.this, ReportProblemActivity.class);
                startActivity(intent);
            }
        });

        // Set View Status Button Click Listener
        Button buttonViewStatus = findViewById(R.id.buttonViewStatus);
        buttonViewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Report Problem Activity
                Intent intent = new Intent(HomeActivity.this, ViewStatusActivity.class);
                startActivity(intent);
            }
        });

        // Set Track Repairman Click Listener
        Button buttonTrackRepair = findViewById(R.id.buttonTrackRepairman);
        buttonTrackRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Report Problem Activity
                Intent intent = new Intent(HomeActivity.this, TrackRepairmenActivity.class);
                startActivity(intent);
            }
        });
    }

    private RecyclerView setMoviesToRecyclerView(@IdRes int resId) {
        RecyclerView recyclerView = findViewById(resId);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this,
                LinearLayoutManager.HORIZONTAL, false));

        return recyclerView;
    }

    private void setupImageSlider() {
        ArrayList<SliderImage> images = new ArrayList<>();
        images.add(new SliderImage(R.drawable.slide_1));
        images.add(new SliderImage(R.drawable.slide_6));
        images.add(new SliderImage(R.drawable.slide_3));

        ViewPager viewPager = findViewById(R.id.viewPager);
        SliderImageAdapter imageAdapter = new SliderImageAdapter(this, images);
        viewPager.setAdapter(imageAdapter);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(currentPage == images.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 8000);
            }
        };

        handler.postDelayed(runnable, 8000);
    }



}