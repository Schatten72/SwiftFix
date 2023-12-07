package com.romega_swiftfix;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.romega_swiftfix.model.User;

public class Register extends Fragment {

    private FirebaseFirestore db;

    public Register() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
        EditText firstNameEditText = fragment.findViewById(R.id.editTextText);
        EditText lastNameEditText = fragment.findViewById(R.id.editTextText2);
        EditText emailEditText = fragment.findViewById(R.id.editTextPhone2); // Changed to email
        EditText passwordEditText = fragment.findViewById(R.id.editTextTextPassword2);


        Button registerButton = fragment.findViewById(R.id.button3);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String email = emailEditText.getText().toString(); // Changed to email
                String password = passwordEditText.getText().toString();

                // Validate input
                if (validateInput(firstName, lastName, email, password)) {
                    // Create a User object
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPassword(password);

                    // Perform user registration
                    registerUser(user);
                }
            }
        });


        Button button = fragment.findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setFragment(MainActivity.LOGIN_FRAGMENT);
            }
        });

    }

    private boolean validateInput(String firstName, String lastName, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.");
            return false;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long.");
            return false;
        }

        return true;
    }

    private void registerUser(User user) {
        // Validate email and password
        if (!validateInput(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword())) {
            return; // Stop registration if input is not valid
        }

        // Use Firebase Authentication to create a user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        // Set display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getFirstName() + " " + user.getLastName())
                                .build();

                        if (firebaseUser != null) {
                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileUpdateTask -> {
                                        if (profileUpdateTask.isSuccessful()) {
                                            // Display name set successfully

                                            addUserDataToFirestore(user);
                                        } else {
                                            // Display name set failed
                                            showToast("Failed to set display name.");
                                        }
                                    });
                        }

                    } else {
                        // Registration failed

                        showToast("Registration failed. Please try again.");
                    }
                });
    }

    private void addUserDataToFirestore(User user) {
        // Add user data to Firestore
        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    // Firestore data added successfully
                    showToast("Registration successful");
                    // send email veriifcation
                    sendEmailVerification();

                })
                .addOnFailureListener(e -> {
                    // Firestore data addition failed
                    // Handle the error, e.g., show an error message
                    showToast("Registration failed. Please try again.");
                    // Also delete the user from Firebase Authentication to keep data consistent
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                });
    }
    private void sendEmailVerification() {
        // Send email verification
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email verification sent successfully
                            navigateToLoginFragment();
                            showToast("Verification email sent. Please check your email.");
                        } else {
                            // Email verification sending failed
                            // Handle the error, e.g., show an error message
                            showToast("Failed to send verification email.");
                        }
                    });
        }
    }
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLoginFragment() {

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setFragment(MainActivity.LOGIN_FRAGMENT);
        }
    }
}