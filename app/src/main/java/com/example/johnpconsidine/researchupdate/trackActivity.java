package com.example.johnpconsidine.researchupdate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class trackActivity extends AppCompatActivity implements SensorEventListener{



    //DATABASE STUFF
    DatabaseHelper mDb;
    boolean datacheck = true;

    //SENSOR EQUIPMENTL: ACCELEROMETER
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor mSensor;
    private float speed=0; //accel value

    //GRAVITY VECTOR
    private Sensor myGravitySensor;
    float standardGravity;
    float thresholdGraqvity;
    private float grav=0;

    //SENSOR EQUIPMENT: GYROSCOPE
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private float gyro=0;

    private boolean checkGyro = false;
    private static final String TAG = trackActivity.class.getSimpleName();
    private int interval = 0;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

   //layout stuff
    private  EditText updateField;
    private TextView accelVal;
    private TextView gyroVal;
    private TextView gravVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        standardGravity = SensorManager.STANDARD_GRAVITY;
        thresholdGraqvity = standardGravity/2;


        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myGravitySensor = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        updateField = (EditText) findViewById(R.id.updateField);
        accelVal = (TextView) findViewById(R.id.accelValue);
        gyroVal = (TextView) findViewById(R.id.gyroVal);
        gravVal = (TextView) findViewById(R.id.gravRead);

        //DATABASE
        mDb = new DatabaseHelper(this);

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);

    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;




        //convert string to int
        try {
            interval = Integer.parseInt(updateField.getText().toString());
        } catch(NumberFormatException nfe) {
           Log.i(TAG, "Could not parse ");
        }



        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];

            float z = sensorEvent.values[2];
            Log.i(TAG, " THE INTERVAL IS " + interval);

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > interval) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                checkGyro = true;
                speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }


                //SET CONTENT VALUE HERE


                accelVal.setText(speed + "");
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

        //get gyroscope reading

            if (checkGyro) {
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                // Axis of the rotation sample, not normalized yet.
                double axisX = sensorEvent.values[0];
                double axisY = sensorEvent.values[1];
                double axisZ = sensorEvent.values[2];


                // Calculate the angular speed of the sample
                double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                float thetaOverTwo = (float) (omegaMagnitude * dT / 2.0f);
                float sinThetaOverTwo = (float)(Math.sin(thetaOverTwo));
                float cosThetaOverTwo = (float)(Math.cos(thetaOverTwo));
                deltaRotationVector[0] = (float)(sinThetaOverTwo * axisX);
                deltaRotationVector[1] = (float)(sinThetaOverTwo * axisY);
                deltaRotationVector[2] = (float)(sinThetaOverTwo * axisZ);
                deltaRotationVector[3] = cosThetaOverTwo;
                gyroVal.setText(deltaRotationVector[3] + "");
                gyro = deltaRotationVector[3];
            }
            timestamp = sensorEvent.timestamp;
            float[] deltaRotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;


            //gravity
        Sensor source = sensorEvent.sensor;
        if (checkGyro) {
            float z = sensorEvent.values[2];
            gravVal.setText(z + "");
            checkGyro = false;
            grav = z;
         }
        addData(speed+"", gyro+"", grav+"");
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    public void addData(String accel, String gyro, String grav) {
            boolean isInserted = mDb.insertData(accel, gyro, grav);
            if (datacheck) { // one time deal
                if (isInserted == true) {
                    Toast.makeText(trackActivity.this, "data inserted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(trackActivity.this, "data not inserted", Toast.LENGTH_LONG).show();
                }
                datacheck = false;
            }

    }



}
