package com.romega_swiftfix;

import androidx.appcompat.app.AppCompatActivity;

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

public class FeedbackActivity extends AppCompatActivity {

    private ListView listViewJobs;
    private RelativeLayout relativeLayoutJobDetails;
    private TextView textViewJobNumberDetails;
    private TextView textViewRepairmanDetails;
    private TextView textViewJobDescriptionDetails;
    private EditText editTextFeedbackDescriptionDetails;
    private RatingBar ratingBarDetails;
    private Button buttonSubmitFeedbackDetails;

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

        // Populate job list
        String[] jobList = {"Job #123 - Date 1", "Job #456 - Date 2", "Job #456 - Date 2", "Job #456 - Date 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jobList);
        listViewJobs.setAdapter(adapter);

        // Set item click listener for job list
        listViewJobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show job details popup
                showJobDetailsPopup(jobList[position]);
            }
        });

        // Set click listener for submit button in the job details popup
        buttonSubmitFeedbackDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle feedback submission
                // You can access feedback details using editTextFeedbackDescriptionDetails.getText().toString()
                // and ratingBarDetails.getRating()
                // Add your logic to save the feedback for the selected job
                // ...

                // After handling feedback, you can hide the popup
                relativeLayoutJobDetails.setVisibility(View.GONE);
            }
        });
    }

    // Method to show job details popup
    private void showJobDetailsPopup(String jobDetails) {
        // Split job details to get job number and date
        String[] parts = jobDetails.split(" - ");
        String jobNumber = parts[0];

        // Populate job details in the popup
        textViewJobNumberDetails.setText(getString(R.string.job_number_details, jobNumber));
        textViewRepairmanDetails.setText(getString(R.string.repairman_details, "John Doe")); // Replace with actual repairman details
        textViewJobDescriptionDetails.setText(getString(R.string.job_description_details, "Replace faucet")); // Replace with actual job description

        // Set visibility of the popup
        relativeLayoutJobDetails.setVisibility(View.VISIBLE);
    }
    public void closePopup(View view) {
        RelativeLayout relativeLayoutPopup = findViewById(R.id.relativeLayoutJobDetails);
        relativeLayoutPopup.setVisibility(View.GONE);
    }



}
