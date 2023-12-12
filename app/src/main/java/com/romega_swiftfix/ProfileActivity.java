package com.romega_swiftfix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.romega_swiftfix.model.User;


public class ProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEdit;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String currentUserEmail;
    private TextView infoText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Get current user's email
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserEmail = firebaseAuth.getCurrentUser().getEmail();
        }

        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEdit = findViewById(R.id.editTextPassword);
         infoText = findViewById(R.id.textView11);
        // Retrieve user details from Firestore and populate UI
        retrieveUserDetails();


        Button updateProfileButton = findViewById(R.id.buttonUpdateProfile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(view);
            }
        });

    }

    private void retrieveUserDetails() {
        // Get the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Use the logged-in user's email to identify the user in Firestore
            String userEmail = currentUser.getEmail();


            // Check if the user signed in with Google
            boolean isGoogleSignIn = currentUser.getProviderData().stream()
                    .anyMatch(userInfo -> userInfo.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID));

            if (isGoogleSignIn) {


                String displayName = currentUser.getDisplayName();

                infoText.setText("Profile Details are Restricted since you signed in from Google Account");
                firstNameEditText.setText(displayName);
                lastNameEditText.setText(displayName);
                emailEditText.setText(userEmail);
                passwordEdit.setText("Restricted");
                // Disable editing UI components
                disableEditing();
            }
            // Reference to the "users" collection
            CollectionReference usersCollection = firestore.collection("users");

            // Query to find the document with the matching email
            Query query = usersCollection.whereEqualTo("email", userEmail);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Check if there is any matching document
                        if (!task.getResult().isEmpty()) {
                            // Get the first document (assuming email is unique)
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            // Map the document data to a User object
                            User user = document.toObject(User.class);

                            // Populate UI with user details
                            if (user != null) {
                                firstNameEditText.setText(user.getFirstName());
                                lastNameEditText.setText(user.getLastName());
                                emailEditText.setText(userEmail); // Display email
                                passwordEdit.setTransformationMethod(null);
                                passwordEdit.setText(user.getPassword());
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "User not found in Firestore", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    private void disableEditing() {
        // Disable editing of UI components
        firstNameEditText.setEnabled(false);
        lastNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEdit.setEnabled(false);

    }

    public void updateProfile(View view) {
             // Retrieve updated details from UI elements
        String updatedFirstName = firstNameEditText.getText().toString();
        String updatedLastName = lastNameEditText.getText().toString();
        String updatedPassword = passwordEdit.getText().toString();

            // Perform validation
        if (TextUtils.isEmpty(updatedFirstName) || TextUtils.isEmpty(updatedLastName) || TextUtils.isEmpty(updatedPassword)) {

                     // Show an error message or toast and return if the name fields are empty
            Toast.makeText(this, "Please Fill all the Fields !", Toast.LENGTH_SHORT).show();
            return;
        }

           // Perform password validation
        if (!isValidPassword(updatedPassword)) {
                        // Show an error message or toast and return if the password is not valid
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

            // Reference to the "users" collection
        CollectionReference usersCollection = firestore.collection("users");

           // Query to find the document with the matching email
        Query query = usersCollection.whereEqualTo("email", currentUserEmail);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Check if there is any matching document
                    if (!task.getResult().isEmpty()) {
                        // Get the first document (assuming email is unique)
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Update the user document in Firestore
                        document.getReference().update("firstName", updatedFirstName, "lastName", updatedLastName, "password", updatedPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ProfileActivity.this, "User not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Validate password
    private boolean isValidPassword(String password) {

        return password.length() >= 6;
    }



}