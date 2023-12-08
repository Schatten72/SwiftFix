package com.romega_swiftfix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.romega_swiftfix.model.ShakeDetector;
import com.romega_swiftfix.util.NotificationUtils;


public class MainActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener {

    private ShakeDetector shakeDetector;
    private SensorManager sensorManager;
public static final String TAG = MainActivity.class.getName();
    public static final int LOGIN_FRAGMENT = 1;
    public static final int REGISTER_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragment(MainActivity.LOGIN_FRAGMENT);

        // Get the root layout
        View rootLayout = findViewById(R.id.fragmentContainerView);

        // Load the animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply the animation to the root layout
        rootLayout.startAnimation(fadeInAnimation);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i(TAG,db.toString());

        FirebaseApp.initializeApp(this);


        // Initialize ShakeDetector
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(this);

        // Initialize SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            // Register ShakeDetector as a listener for the accelerometer sensor
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometerSensor != null) {
                sensorManager.registerListener(shakeDetector, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        // Initialize NotificationChannel
        NotificationUtils.createNotificationChannel(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister ShakeDetector when the activity is destroyed
        if (sensorManager != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    // Implement the onShake method
    @Override
    public void onShake() {
        showToast("SwiftFix Closed due to shake");
        showNotification("SwiftFix", "App Interrupted Unexpectedly");
        finish();
    }

    // Show notification method
    private void showNotification(String title, String content) {
        NotificationUtils.showNotification(this, title, content);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void setFragment(int fragmentType) {
        Class fragmentClass = null;

        if(fragmentType == MainActivity.LOGIN_FRAGMENT) {
            fragmentClass = Login.class;
        } else if(fragmentType == MainActivity.REGISTER_FRAGMENT) {
            fragmentClass = Register.class;
        }
        FragmentContainerView containerView = findViewById(R.id.fragmentContainerView);

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, fragmentClass, null)
                    .commit();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}