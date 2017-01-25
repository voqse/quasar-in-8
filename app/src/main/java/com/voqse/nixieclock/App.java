package com.voqse.nixieclock;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class App extends Application {

    private WidgetUpdater widgetUpdater;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        widgetUpdater = new WidgetUpdater(this);
        widgetUpdater.scheduleNextUpdate();
    }

    public static WidgetUpdater getWidgetUpdater(@NonNull Context context) {
        return ((App) context.getApplicationContext()).widgetUpdater;
    }
}
