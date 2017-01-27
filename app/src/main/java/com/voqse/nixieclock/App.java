package com.voqse.nixieclock;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.voqse.nixieclock.log.CrashlyticsLogger;
import com.voqse.nixieclock.widget.WidgetUpdater;

import org.slf4j.impl.custom.Level;
import org.slf4j.impl.custom.NativeLoggerAdapter;
import org.slf4j.impl.custom.loggers.CompositeLogger;
import org.slf4j.impl.custom.loggers.LogcatLogger;

import io.fabric.sdk.android.Fabric;

import static com.voqse.nixieclock.BuildConfig.DEBUG;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class App extends Application {

    private WidgetUpdater widgetUpdater;

    @Override
    public void onCreate() {
        super.onCreate();

        initLogger();

        this.widgetUpdater = new WidgetUpdater(this);
        this.widgetUpdater.scheduleNextUpdate();
    }

    private void initLogger() {
        // TODO:
        Level minLoggableLevel = DEBUG ? Level.TRACE : Level.DEBUG;
        NativeLoggerAdapter.setLogger(new CompositeLogger(new LogcatLogger(minLoggableLevel)));
        if (!DEBUG) {
            Fabric.with(this, new Crashlytics());
            NativeLoggerAdapter.addLogger(new CrashlyticsLogger());
        }
    }

    public static WidgetUpdater getWidgetUpdater(@NonNull Context context) {
        return ((App) context.getApplicationContext()).widgetUpdater;
    }
}
