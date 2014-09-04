package com.nth.ikiam;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by svt on 8/27/2014.
 */
public class ToolsFragment extends Fragment implements SensorEventListener {

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    MapActivity activity;
    View view;
    TextView latitud;
    TextView longitud;
    TextView altura;

    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    TextView readingAzimuth, readingPitch, readingRoll;

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            updateInterface(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    private ImageView image;
    private float currentDegree = 0f;
    TextView tvHeading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tools_layout, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        String provider, long minTime, float minDistance, LocationListener listener
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*2, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100 * 30, 5, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        activity = (MapActivity) getActivity();
        //myCompass = (Compass)view.findViewById(R.id.mycompass);
        latitud = (TextView) view.findViewById(R.id.lbl_valor_latitud);
        longitud = (TextView) view.findViewById(R.id.lbl_valor_longitud);
        altura = (TextView) view.findViewById(R.id.lbl_valor_altura);
        latitud.setText("" + location.getLatitude());
        longitud.setText("" + location.getLongitude());
        altura.setText("" + location.getAltitude() + " m");

        sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        //sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        image = (ImageView) view.findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) view.findViewById(R.id.tvHeading);


        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];


        return view;
    }

    public void updateInterface(Location location) {
        // System.out.println("cambio location "+location.getLatitude()+"  "+location.getLongitude()+"  "+location.getAltitude());
        latitud.setText("" + location.getLatitude());
        longitud.setText("" + location.getLongitude());
        altura.setText("" + location.getAltitude() + " m");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
     /*   switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                for(int i =0; i < 3; i++){
                    valuesAccelerometer[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i =0; i < 3; i++){
                    valuesMagneticField[i] = event.values[i];
                }
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField);

        if(success){
            SensorManager.getOrientation(matrixR, matrixValues);

            double azimuth = Math.toDegrees(matrixValues[0]);
            double pitch = Math.toDegrees(matrixValues[1]);
            double roll = Math.toDegrees(matrixValues[2]);

            //readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
            //readingPitch.setText("Pitch: " + String.valueOf(pitch));
            //readingRoll.setText("Roll: " + String.valueOf(roll));
            myCompass.setPadding(10,10,10,10);
            myCompass.update(matrixValues[0]);
        }*/
        // get the angle around the z-axis rotated

        float degree = Math.round(event.values[0]);
        tvHeading.setText("Orientación: " + Float.toString(degree) + " grados");
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        /*sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);*/

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

        activity.setTitle(R.string.tools_title);
        super.onResume();

    }


    @Override
    public void onPause() {
        /*sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);
                */
        sensorManager.unregisterListener(this);

        super.onPause();

    }
}