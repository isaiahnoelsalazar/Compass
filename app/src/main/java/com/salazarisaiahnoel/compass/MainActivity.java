package com.salazarisaiahnoel.compass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImageView compass;
    TextView degreesText;
    SensorManager sensorManager;
    Sensor magneticSensor, accelerometer;
    float[] lastMagneticSensorData = new float[3];
    float[] lastAccelerometerData = new float[3];
    float[] rotationMatrix = new float[9];
    float[] orientation = new float[3];
    boolean lastMagneticSensorDataSet = false;
    boolean lastAccelerometerDataSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = findViewById(R.id.compass);
        degreesText = findViewById(R.id.degrees);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, magneticSensor);
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magneticSensor){
            System.arraycopy(event.values, 0, lastMagneticSensorData, 0, event.values.length);
            lastMagneticSensorDataSet = true;
        }
        if (event.sensor == accelerometer){
            System.arraycopy(event.values, 0, lastAccelerometerData, 0, event.values.length);
            lastAccelerometerDataSet = true;
        }

        if (lastMagneticSensorDataSet && lastAccelerometerDataSet){
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometerData, lastMagneticSensorData);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float radians = orientation[0];
            float degrees = (float) (Math.toDegrees(radians) + 360) % 360;

            compass.setRotation(-degrees);
            degreesText.setText(String.valueOf((int) degrees));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}