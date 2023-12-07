package com.romega_swiftfix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.romega_swiftfix.adapter.SliderImageAdapter;
import com.romega_swiftfix.model.SliderImage;
import com.romega_swiftfix.model.User;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private int currentPage = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Initialize FirebaseFirestore
        firestore = FirebaseFirestore.getInstance();

        // Set App Bar
        AppBar.setAppBar(getSupportFragmentManager(), R.id.fragmentContainerView3);

        // Set Bottom Navigation
        BottomNavigation.setNavigationBar(getSupportFragmentManager(), R.id.fragmentContainerView2);

        // Set Image Slider
        setupImageSlider();


        // Find the Welcome Message TextView
        TextView welcomeMessage = findViewById(R.id.textWelcomeMessage);

        // Set initial alpha to 0 (completely transparent)
        welcomeMessage.setAlpha(0f);


        welcomeMessage.animate()
                .alpha(1f)
                .setDuration(3000)
                .start();

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


        // Set Feedback button Click Listener
        Button buttonFeedback = findViewById(R.id.buttonFeedback);
        buttonFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Report Problem Activity
                Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });


        // Fetch and display user's name
        displayUserName();
    }
    private void displayUserName() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Get a reference to the users collection in Firestore
            Query userQuery = firestore.collection("users").whereEqualTo("email", userEmail);

            // Fetch user data from Firestore
            userQuery.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Assuming there is only one user with the given email
                        QueryDocumentSnapshot documentSnapshot = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);

                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                String welcomeMessage = "Welcome, " + user.getFirstName() + " " + user.getLastName() +"!";
                                // Update the UI with the welcome message
                                TextView welcomeTextView = findViewById(R.id.textWelcomeMessage);
                                welcomeTextView.setText(welcomeMessage);
                            }
                        }
                    }
                } else {
                    // Handle the failure to fetch user data
                    Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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