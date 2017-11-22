package dk.arriva.mobilbillet.animationtesting;

import android.support.annotation.NonNull;

interface TiltViewLifecycleObserver {
    void register(@NonNull LifecycleListener listener);

    void unregister(@NonNull LifecycleListener listener);

    interface LifecycleListener {
        void onViewStarted();

        void onViewStopped();
    }
}
