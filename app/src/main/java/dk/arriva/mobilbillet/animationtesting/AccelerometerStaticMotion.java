package dk.arriva.mobilbillet.animationtesting;

import android.app.Activity;
import android.os.Bundle;

public class AccelerometerStaticMotion extends Activity {
    private TiltMonitor tiltMonitor;
    private DefaultTiltViewLifecycleObserver lifecycleObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);
        lifecycleObserver = new DefaultTiltViewLifecycleObserver();
        MaskTiltImageView tiltImageView = findViewById(R.id.tiltImageView);
        tiltImageView.setLifecycleObserver(lifecycleObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleObserver.onViewStarted();
    }

    @Override
    protected void onStop() {
        lifecycleObserver.onViewStopped();
        super.onStop();
    }
}