package com.voqse.nixieclock;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class App extends Application {

    private WidgetUpdater widgetUpdater;

    @Override
    public void onCreate() {
        super.onCreate();

        widgetUpdater = new WidgetUpdater(this);
        widgetUpdater.scheduleNextUpdate();
    }

    public static WidgetUpdater getWidgetUpdater(@NonNull Context context) {
        return ((App) context.getApplicationContext()).widgetUpdater;
    }
}
