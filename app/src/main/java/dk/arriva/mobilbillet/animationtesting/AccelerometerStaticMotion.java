package dk.arriva.mobilbillet.animationtesting;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;

import static dk.arriva.mobilbillet.animationtesting.AccelerometerTiltMonitor.FLAG_ZERO_TO_ONE;

public class AccelerometerStaticMotion extends Activity {
    private AccelerometerTiltMonitor tiltMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tiltMonitor = new AccelerometerTiltMonitor(sensorManager, FLAG_ZERO_TO_ONE);
        setContentView(R.layout.activity_static);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tiltMonitor.register(new TiltMonitor.TiltListener() {
            @Override
            public void onTiltChanged(float x, float y, float z) {
                // Do nothing
            }
        });
    }

    @Override
    protected void onStop() {
        tiltMonitor.unregister();
        super.onStop();
    }
}