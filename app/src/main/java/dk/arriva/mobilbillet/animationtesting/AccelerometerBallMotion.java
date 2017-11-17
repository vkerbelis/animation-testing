package dk.arriva.mobilbillet.animationtesting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import static dk.arriva.mobilbillet.animationtesting.AccelerometerTiltMonitor.FLAG_ZERO_TO_ONE;

public class AccelerometerBallMotion extends Activity {
    private float xPos = 0.0f;
    private float yPos = 0.0f;
    private float xMax, yMax;
    private float xCenter, yCenter;
    private Bitmap ball;
    private AccelerometerTiltMonitor tiltMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tiltMonitor = new AccelerometerTiltMonitor(sensorManager, FLAG_ZERO_TO_ONE);
        BallView ballView = new BallView(this);
        setContentView(ballView);

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        xMax = (float) size.x - 100;
        yMax = (float) size.y - 100;
        xCenter = xMax / 2;
        yCenter = yMax / 2;
        xPos = xCenter;
        yPos = yCenter;

    }

    @Override
    protected void onStart() {
        super.onStart();
        tiltMonitor.register(new TiltMonitor.TiltListener() {
            @Override
            public void onTiltChanged(float x, float y, float z) {
                yPos = y * yMax;
                xPos = (1 - x) * xMax;
                constraintBall();
            }
        });
    }

    @Override
    protected void onStop() {
        tiltMonitor.unregister();
        super.onStop();
    }

    private void constraintBall() {
        if (xPos > xMax) {
            xPos = xMax;
        } else if (xPos < 0) {
            xPos = 0;
        }

        if (yPos > yMax) {
            yPos = yMax;
        } else if (yPos < 0) {
            yPos = 0;
        }
    }

    private class BallView extends View {
        public BallView(Context context) {
            super(context);
            Bitmap ballSrc = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            final int dstWidth = 100;
            final int dstHeight = 100;
            ball = Bitmap.createScaledBitmap(ballSrc, dstWidth, dstHeight, true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(ball, xPos, yPos, null);
            invalidate();
        }
    }
}