package com.sqm.advert_helper.adv;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityLifecycleCallbackHelper implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivityLifecycle";
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"onActivityCreated~~~~" + activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG,"onActivityStarted~~~~" + activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

        Log.d(TAG,"onActivityResumed~~~~" + activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

        Log.d(TAG,"onActivityPaused~~~~" + activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

        Log.d(TAG,"onActivityStopped~~~~" + activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        Log.d(TAG,"onActivitySaveInstanceState~~~~" + activity);
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d(TAG,"onActivityDestroyed~~~~" + activity);

    }
}
