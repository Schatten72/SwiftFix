package com.romega_swiftfix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    private Switch switchDarkMode;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_MODE = "darkMode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        loadPreferences();

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleDarkMode(isChecked);
            }
        });


    }

    public void makeCall(View view) {

        String phoneNumber = "tel:" + "0714489500";

        // Create the intent with ACTION_DIAL
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));

        // Start the intent
        startActivity(dialIntent);
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        switchDarkMode.setChecked(prefs.getBoolean(PREF_DARK_MODE, false));
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_DARK_MODE, switchDarkMode.isChecked());
        editor.apply();
    }

    private void toggleDarkMode(boolean darkMode) {
        int mode = darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
        getDelegate().setLocalNightMode(mode);
        savePreferences();
        recreate();
    }
}