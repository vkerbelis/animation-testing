package dk.arriva.mobilbillet.animationtesting;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

public class MainActivity extends AppCompatActivity {
    private static final float FORCE_OF_GRAVITY = 9.81f;
    public static final int LOG_RAW = 1;
    public static final int LOG_GRAVITY = 1 << 1;
    public static final int LOG_LINEAR = 1 << 2;
    public static final int LOG_STAT = 1 << 3;
    private static final String TAG = "AnimationTest";
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private int logMode = LOG_STAT;
    float[] gravity = new float[3];
    float[] stat = new float[3];
    float[] linear = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(getListener(), accelerometer, SENSOR_DELAY_GAME);
        findViewById(R.id.raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLogMode(LOG_RAW);
            }
        });
        findViewById(R.id.gravity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLogMode(LOG_GRAVITY);
            }
        });
        findViewById(R.id.linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLogMode(LOG_LINEAR);
            }
        });
        findViewById(R.id.linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLogMode(LOG_STAT);
            }
        });
    }

    private void toggleLogMode(int mode) {
        logMode ^= mode;
    }

    private SensorEventListener getListener() {
        return new SensorEventCallback() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    long curTime = System.currentTimeMillis();
//                    if ((curTime - lastUpdate) > 100) {
                    doStuffWithTheSensor(event);
//                        long diffTime = (curTime - lastUpdate);
//                        lastUpdate = curTime;
//                    }
                }
            }
        };
    }

    private void doStuffWithTheSensor(SensorEvent event) {
        final float alpha = 0.8f;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear[0] = event.values[0] - gravity[0];
        linear[1] = event.values[1] - gravity[1];
        linear[2] = event.values[2] - gravity[2];

        stat[0] = constraint(gravity[0] / FORCE_OF_GRAVITY);
        stat[1] = constraint(gravity[1] / FORCE_OF_GRAVITY);
        stat[2] = constraint(gravity[2] / FORCE_OF_GRAVITY);

        log(event, gravity, linear, stat);
    }

    private float constraint(float initial) {
        float constrained = initial;
        if (initial > 1) {
            constrained = 1;
        } else if (initial < -1) {
            constrained = -1;
        }
        return constrained;
    }

    private void log(SensorEvent event, float[] gravity, float[] linear, float[] stat) {
        if (LOG_RAW == (logMode & LOG_RAW)) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.d(TAG, String.format("x: %f, y: %f, z: %f", x, y, z));
        }
        if (LOG_GRAVITY == (logMode & LOG_GRAVITY)) {
            Log.d(TAG, String.format("Gravity x: %f, y: %f, z: %f",
                    gravity[0], gravity[1], gravity[2]));
        }
        if (LOG_LINEAR == (logMode & LOG_LINEAR)) {
            Log.d(TAG, String.format("Linear x: %f, y: %f, z: %f",
                    linear[0], linear[1], linear[2]));
        }
        if (LOG_STAT == (logMode & LOG_STAT)) {
            Log.d(TAG, String.format("Stat x: %f, y: %f, z: %f",
                    stat[0], stat[1], stat[2]));
        }
    }
}
