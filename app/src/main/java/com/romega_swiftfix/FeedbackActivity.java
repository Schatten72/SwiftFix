package com.romega_swiftfix;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private ListView listViewJobs;
    private RelativeLayout relativeLayoutJobDetails;
    private TextView textViewJobNumberDetails;
    private TextView textViewRepairmanDetails;
    private TextView textViewJobDescriptionDetails;
    private EditText editTextFeedbackDescriptionDetails;
    private RatingBar ratingBarDetails;
    private Button buttonSubmitFeedbackDetails;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize views
        listViewJobs = findViewById(R.id.listViewJobs);
        relativeLayoutJobDetails = findViewById(R.id.relativeLayoutJobDetails);
        textViewJobNumberDetails = findViewById(R.id.textViewJobNumberDetails);
        textViewRepairmanDetails = findViewById(R.id.textViewRepairmanDetails);
        textViewJobDescriptionDetails = findViewById(R.id.textViewJobDescriptionDetails);
        editTextFeedbackDescriptionDetails = findViewById(R.id.editTextFeedbackDescriptionDetails);
        ratingBarDetails = findViewById(R.id.ratingBarDetails);
        buttonSubmitFeedbackDetails = findViewById(R.id.buttonSubmitFeedbackDetails);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Fetch completed jobs and populate the list view
        fetchCompletedJobs();

        // Set item click listener for job list
        listViewJobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show job details popup
                showJobDetailsPopup((String) parent.getItemAtPosition(position));
            }
        });

        // Set click listener for submit button in the job details popup
        buttonSubmitFeedbackDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle feedback submission


                //  hide the popup
                relativeLayoutJobDetails.setVisibility(View.GONE);
            }
        });
    }

    // Method to fetch completed jobs and populate the list view
    private void fetchCompletedJobs() {
        String userEmail = firebaseAuth.getCurrentUser().getEmail();

        firestore.collection("assignments")
                .whereEqualTo("clientemail", userEmail)
                .whereEqualTo("status", "completed")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> completedJobs = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Get jobId and add it to the list
                                String repairmanId = document.getString("repairmanId");
                                String jobDescription = document.getString("details");
                                // Fetch repairman's name using repairmanId

                                String jobId = document.getString("jobId");
                                fetchRepairmanName(repairmanId, jobId, jobDescription);
                                completedJobs.add("Job #" + jobId);
                            }

                            // Populate job list
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(FeedbackActivity.this, android.R.layout.simple_list_item_1, completedJobs);
                            listViewJobs.setAdapter(adapter);
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    // Method to fetch repairman's name
    private void fetchRepairmanName(String repairmanId, String jobNumber, String jobDescription) {
        firestore.collection("repairmen")
                .whereEqualTo("repairmanId", repairmanId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get repairman's name and update UI
                                String repairmanName = document.getString("name");

                                // Now you have all the details, update your UI
                                updateUIWithJobDetails(jobNumber, repairmanName, jobDescription);
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    // Method to update UI with job details
    private void updateUIWithJobDetails(String jobNumber, String repairmanName, String jobDescription) {
        // Update UI elements with job details
        textViewRepairmanDetails.setText(getString(R.string.repairman_details, repairmanName));
        textViewJobDescriptionDetails.setText(getString(R.string.job_description_details, jobDescription));
        // You can also update other UI elements as needed
    }
    // Method to show job details popup
    private void showJobDetailsPopup(String jobDetails) {
        // Split job details to get job number
        String[] parts = jobDetails.split(" ");
        String jobNumber = parts[1]; // Extract the job number

        // Populate job details in the popup
        textViewJobNumberDetails.setText(getString(R.string.job_number_details, jobNumber));

        fetchAndDisplayJobDetails(jobNumber);

        relativeLayoutJobDetails.setVisibility(View.VISIBLE);
    }

    // Method to fetch and display job details
    private void fetchAndDisplayJobDetails(String jobNumber) {

        firestore.collection("jobs")
                .whereEqualTo("jobNumber", jobNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get job details and update UI
                                String repairmanName = document.getString("repairmanName");
                                String jobDescription = document.getString("description");

                                textViewRepairmanDetails.setText(getString(R.string.repairman_details, repairmanName));
                                textViewJobDescriptionDetails.setText(getString(R.string.job_description_details, jobDescription));
                            }
                        } else {
                            // Handle errors
                        }
                    }
                });
    }

    public void closePopup(View view) {
        RelativeLayout relativeLayoutPopup = findViewById(R.id.relativeLayoutJobDetails);
        relativeLayoutPopup.setVisibility(View.GONE);
    }
}
