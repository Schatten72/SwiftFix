package com.romega_swiftfix.model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {


    private static final float SHAKE_THRESHOLD = 20.0f;


    private static final int SHAKE_INTERVAL = 1000; // in milliseconds

    private long lastShakeTime;

    private OnShakeListener onShakeListener;

    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.onShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate acceleration
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // Check if acceleration is above the threshold
            if (acceleration > SHAKE_THRESHOLD) {
                long currentTime = System.currentTimeMillis();

                // Check if enough time has passed since the last shake
                if (currentTime - lastShakeTime > SHAKE_INTERVAL) {
                    lastShakeTime = currentTime;

                    // Trigger the shake listener
                    if (onShakeListener != null) {
                        onShakeListener.onShake();
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
