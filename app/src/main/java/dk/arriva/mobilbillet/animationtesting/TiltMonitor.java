package dk.arriva.mobilbillet.animationtesting;

import android.hardware.SensorEventListener;

interface TiltMonitor extends SensorEventListener {
    void register(TiltListener tiltListener);

    void unregister();

    interface TiltListener {
        TiltListener EMPTY = new TiltListener() {
            @Override
            public void onTiltChanged(float x, float y, float z) {
                // Do nothing
            }
        };

        void onTiltChanged(float x, float y, float z);
    }
}
