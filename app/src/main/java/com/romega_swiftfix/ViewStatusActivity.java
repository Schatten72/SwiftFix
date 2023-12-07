package com.romega_swiftfix;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.romega_swiftfix.model.Job;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
public class ViewStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewStatus;
    private TextView textViewStatusPopupTrigger;
    private RelativeLayout relativeLayoutStatusPopup;
    private TextView textViewClosePopup;


    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        // Initialize views
        cardViewStatus = findViewById(R.id.cardViewStatus);
        textViewStatusPopupTrigger = findViewById(R.id.textViewStatusPopupTrigger);
        relativeLayoutStatusPopup = findViewById(R.id.relativeLayoutStatusPopup);
        textViewClosePopup = findViewById(R.id.textViewClosePopup);

        // Set click listeners
        textViewStatusPopupTrigger.setOnClickListener(this);
        textViewClosePopup.setOnClickListener(this);
        // Fetch and display job data
        fetchAndDisplayJobData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewStatusPopupTrigger) {
            // Show detailed status popup
            relativeLayoutStatusPopup.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.textViewClosePopup) {
            // Close detailed status popup
            relativeLayoutStatusPopup.setVisibility(View.GONE);
        }
    }

    private void fetchAndDisplayJobData() {
        String userEmail = firebaseAuth.getCurrentUser().getEmail();

        // Assuming you have a 'jobs' collection in Firestore
        firestore.collection("jobs")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Convert the document to a Job object
                            Job job = document.toObject(Job.class);
                            // Now you can use the 'job' object to update your UI
                            updateUIWithJobData(job);
                        }
                    } else {
                        // Handle errors
                    }
                });
    }

    private void updateUIWithJobData(Job job) {
        // Here, you can update your UI elements with the job data
        // For example, you can set text to TextViews, load images, etc.
        if (job != null) {
            // Example: set job number to a TextView
            TextView textViewJobNumber = findViewById(R.id.textViewJobNumber);
            textViewJobNumber.setText("JOB "+job.getJobNumber());
            // Set job description to a TextView
            TextView textViewJobDetails = findViewById(R.id.textViewJobDetails);
            textViewJobDetails.setText(job.getDescription());


        }
    }

}
