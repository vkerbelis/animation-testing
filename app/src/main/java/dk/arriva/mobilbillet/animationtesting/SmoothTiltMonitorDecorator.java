package dk.arriva.mobilbillet.animationtesting;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.LinkedList;
import java.util.Queue;

class SmoothTiltMonitorDecorator implements TiltMonitor {
    private static final int VALUE_COUNT = 10;
    private final Queue<Float> queue = new LinkedList<>();
    private final TiltMonitor tiltMonitor;

    public SmoothTiltMonitorDecorator(TiltMonitor tiltMonitor) {
        super();
        this.tiltMonitor = tiltMonitor;
    }

    @Override
    public void register(final TiltListener tiltListener) {
        tiltMonitor.register(new TiltListener() {
            @Override
            public void onTiltChanged(float x, float y, float z) {
                queue.add(x);
                float normalizedX = createNormalizedValue(x);
                tiltListener.onTiltChanged(normalizedX, y, z);
            }
        });
    }

    private float createNormalizedValue(float x) {
        float normalizedValue = x;
        if (queue.size() > VALUE_COUNT) {
            float fullStack = 0f;
            for (float value : queue) {
                fullStack += value;
            }
            normalizedValue = fullStack / VALUE_COUNT;
            queue.remove();
        }
        return normalizedValue;
    }

    @Override
    public void unregister() {
        tiltMonitor.unregister();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        tiltMonitor.onSensorChanged(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        tiltMonitor.onAccuracyChanged(sensor, accuracy);
    }
}
