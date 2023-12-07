package com.romega_swiftfix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends Fragment {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        EditText emailEditText = fragment.findViewById(R.id.editTextEmail);
        EditText passwordEditText = fragment.findViewById(R.id.editTextTextPassword);
        CheckBox rememberCheckBox = fragment.findViewById(R.id.rememberCheckBox);

        // Retrieve saved credentials from SharedPreferences
        SharedPreferences preferences = fragment.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = preferences.getString(PREF_USERNAME, "");
        String savedPassword = preferences.getString(PREF_PASSWORD, "");

        // Set saved credentials in the EditText fields
        emailEditText.setText(savedUsername);
        passwordEditText.setText(savedPassword);

        Button loginButton = fragment.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validate input
                if (validateInput(email, password)) {
                    // Perform user login
                    loginUser(email, password);

                    // Save credentials if the "Remember Me" checkbox is checked
                    if (rememberCheckBox.isChecked()) {
                        saveCredentials(email, password);
                    }
                }
            }
        });

        TextInputLayout passwordTextInputLayout = fragment.findViewById(R.id.passwordTextInputLayout);
//        EditText passwordEditText = fragment.findViewById(R.id.editTextTextPassword);

        // Toggle password visibility
        passwordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputType = passwordEditText.getInputType();
                if ((inputType & 0x00000080) > 0) {
                    // Password field is visible, hide it
                    passwordEditText.setInputType(inputType | 0x00000081);
                } else {
                    // Password field is hidden, show it
                    passwordEditText.setInputType(inputType & 0x0000007F);
                }
            }
        });
        Button registerButton = fragment.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setFragment(MainActivity.REGISTER_FRAGMENT);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.");
            return false;
        }

        // Add more validation rules as needed

        return true;
    }

    private void loginUser(String email, String password) {
        // Use Firebase Authentication to sign in
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Check if the email is verified
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Email is verified, and login is successful
                            showToast("Welcome, " + user.getDisplayName() + "!");
                            // Navigate to the home page
                            navigateToHome();
                        } else {
                            // Email is not verified
                            showToast("Please verify your email before logging in.");
                        }
                    } else {
                        // Login failed
                        // Handle the error, e.g., show an error message
                        showToast("Login failed. Please check your credentials.");
                    }
                });
    }

    private void saveCredentials(String username, String password) {
        // Save credentials in SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_USERNAME, username);
        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        // Assuming MainActivity has a method to navigate to the home page
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish(); // Close the current activity if needed
    }
}
