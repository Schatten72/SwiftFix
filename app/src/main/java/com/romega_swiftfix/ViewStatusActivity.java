package com.romega_swiftfix;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.romega_swiftfix.model.Assignment;
import com.romega_swiftfix.model.Job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ViewStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewStatus;
    private TextView textViewStatusPopupTrigger;
    private RelativeLayout relativeLayoutStatusPopup;
    private TextView textViewClosePopup;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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


        firestore.collection("jobs")
                .whereEqualTo("userEmail", userEmail)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {

                        return;
                    }

                    for (DocumentSnapshot document : value) {
                        // Convert the document to a Job object
                        Job job = document.toObject(Job.class);

                        updateUIWithJobData(job);

                        fetchAndDisplayAssignmentData(job.getJobId());
                    }
                });
    }

    private void fetchAndDisplayAssignmentData(String jobId) {
        // Assuming you have an 'assignments' collection in Firestore
        firestore.collection("assignments")
                .whereEqualTo("jobId", jobId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {

                        return;
                    }
                    for (DocumentSnapshot document : value) {
                        // Convert the document to an Assignment object
                        Assignment assignment = document.toObject(Assignment.class);

                        updateUIWithAssignmentData(assignment);
                    }
                });
    }

    private void updateUIWithAssignmentData(Assignment assignment) {
        // Update your UI elements with assignment data
        if (assignment != null) {

            TextView textViewStatusDetails = findViewById(R.id.textViewStatusDetails);
            textViewStatusDetails.setText("Details: " + assignment.getDetails());

            TextView textViewStatus = findViewById(R.id.textViewCompleteStatus);
            textViewStatus.setText("Status: " + assignment.getStatus());

            TextView textViewStatustime = findViewById(R.id.textViewCompleteStatustime);
            textViewStatustime.setText("Time: " + assignment.getAssignmentTime());

        }
    }

    private void updateUIWithJobData(Job job) {

        if (job != null) {
            // Example: set job number to a TextView
            TextView textViewJobNumber = findViewById(R.id.textViewJobNumber);
            textViewJobNumber.setText("JOB " + job.getJobNumber());
            // Set job description to a TextView
            TextView textViewJobDetails = findViewById(R.id.textViewJobDetails);
            textViewJobDetails.setText(job.getDescription());

            // Retrieve timestamp as Object
            Object timestampObject = job.getTimestamp();

            if (timestampObject instanceof Map) {
                // Cast to Map and get the value for ".sv"
                Map<String, Object> timestampMap = (Map<String, Object>) timestampObject;
                Object svValue = timestampMap.get(".sv");

                if (svValue instanceof String && ((String) svValue).equals("timestamp")) {
                    // placeholder for server timestamp
                    Date timestampDate = new Date();

                    // Format the date as needed (e.g., using SimpleDateFormat)
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTimestamp = dateFormat.format(timestampDate);

                    // Set the formatted timestamp to the TextView
                    TextView textViewJobTimestamp = findViewById(R.id.textViewJobAddedTime);
                    textViewJobTimestamp.setText("Added on: " + formattedTimestamp);
                }
            }
        }
    }
}
