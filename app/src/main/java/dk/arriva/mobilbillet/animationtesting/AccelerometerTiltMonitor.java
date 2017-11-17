package dk.arriva.mobilbillet.animationtesting;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

public class AccelerometerTiltMonitor implements TiltMonitor {
    private static final String TAG = "AccelerometerMonitor";
    private static final float FORCE_OF_GRAVITY = 9.81f;
    public static final int FLAG_NONE = 0;
    public static final int FLAG_LOGGING = 1;
    public static final int FLAG_ZERO_TO_ONE = 1 << 2;
    private final int flags;
    private final SensorManager sensorManager;
    private float[] stabilized = new float[3];
    private float[] gravity = new float[3];
    private TiltListener listener = TiltListener.EMPTY;

    public AccelerometerTiltMonitor(@NonNull SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        this.flags = FLAG_NONE;
    }

    public AccelerometerTiltMonitor(
            @NonNull SensorManager sensorManager,
            @AccelerometerFlags int flags
    ) {
        this.sensorManager = sensorManager;
        this.flags = flags;
    }

    @Override
    public void register(TiltListener listener) {
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        this.listener = listener;
    }

    @Override
    public void unregister() {
        this.listener = TiltListener.EMPTY;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
            stabilized[0] = constrainMinMaxValues(gravity[0] / FORCE_OF_GRAVITY);
            stabilized[1] = constrainMinMaxValues(gravity[1] / FORCE_OF_GRAVITY);
            stabilized[2] = constrainMinMaxValues(gravity[2] / FORCE_OF_GRAVITY);
            listener.onTiltChanged(stabilized[0], stabilized[1], stabilized[2]);
            logStabilizedValues();
        }
    }

    private float constrainMinMaxValues(float initial) {
        float constrained = constrainMinusToPlus(initial);
        if (flagAppended(FLAG_ZERO_TO_ONE)) {
            constrained = constrainZeroToOne(constrained);
        }
        return constrained;
    }

    private float constrainZeroToOne(float initial) {
        float constrained = initial;
        constrained += 1;
        constrained /= 2;
        return constrained;
    }

    private float constrainMinusToPlus(float initial) {
        float constrained = initial;
        if (initial > 1) {
            constrained = 1;
        } else if (initial < -1) {
            constrained = -1;
        }
        return constrained;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing, accuracy is not important
    }

    private void logStabilizedValues() {
        if (flagAppended(FLAG_LOGGING)) {
            Log.d(TAG, String.format("Accelerometer, x: %s, y: %s, z: %s",
                    stabilized[0], stabilized[1], stabilized[2]));
        }
    }

    private boolean flagAppended(int flag) {
        return flag == (flags & flag);
    }

    @IntDef(value = {FLAG_NONE, FLAG_LOGGING, FLAG_ZERO_TO_ONE}, flag = true)
    public @interface AccelerometerFlags {
    }
}