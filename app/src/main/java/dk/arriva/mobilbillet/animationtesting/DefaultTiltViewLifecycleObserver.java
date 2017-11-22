package dk.arriva.mobilbillet.animationtesting;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

class DefaultTiltViewLifecycleObserver implements TiltViewLifecycleObserver {
    private List<LifecycleListener> listeners = new ArrayList<>();

    @Override
    public void register(@NonNull LifecycleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(@NonNull LifecycleListener listener) {
        listeners.remove(listener);
    }

    public void onViewStarted() {
        for (LifecycleListener listener : listeners) {
            listener.onViewStarted();
        }
    }

    public void onViewStopped() {
        for (LifecycleListener listener : listeners) {
            listener.onViewStopped();
        }
    }
}
