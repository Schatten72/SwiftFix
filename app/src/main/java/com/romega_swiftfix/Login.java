package com.romega_swiftfix;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends Fragment {

    public static final String TAG= Login.class.getName();
    private  FirebaseAuth firebaseAuth;
    private SignInClient signInClient;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(requireContext());
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

        ////////google signin Btn Function///////
        fragment.findViewById(R.id.googleSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         GetSignInIntentRequest signInIntentRequest =  GetSignInIntentRequest.builder()
                        .setServerClientId(getString(R.string.web_client_id)).build();

                Task<PendingIntent> signInIntent = signInClient.getSignInIntent(signInIntentRequest);
                signInIntent.addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                           IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent)
                                   .build();
                           signInLauncher.launch(intentSenderRequest);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


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
////////google signin after result get from launcher///////
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    handleSignInResult(o.getData());
                }
            });


    private void handleSignInResult(Intent intent) {

        try {
              SignInCredential signInCredential = signInClient.getSignInCredentialFromIntent(intent);
              String idToken = signInCredential.getGoogleIdToken();
              firebaseAuthWithGoogle(idToken);
        }catch (ApiException e){
            Log.e(TAG, e.getMessage());
        }

    }
    private void firebaseAuthWithGoogle(String idToken){
       AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
       Task<AuthResult> authResultTask =  firebaseAuth.signInWithCredential(authCredential);
       authResultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   FirebaseUser user = firebaseAuth.getCurrentUser();
                   updateUI(user);
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               showToast("Login failed. Please check your credentials.");
           }
       });
    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            showToast("Welcome, " + user.getDisplayName() + "!");
            // Navigate to the home page
            navigateToHome();
        }
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
