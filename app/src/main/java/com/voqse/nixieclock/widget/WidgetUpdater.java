package com.voqse.nixieclock.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class WidgetUpdater {

    // TODO: change it to 1 minute
    private static final int UPDATE_INTERVAL_MS = 5 * 1000;
    private final AlarmManager alarmManager;
    private final Context context;

    public WidgetUpdater(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNextUpdate() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, newUpdateIntent(), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        long nextUpdateTime = SystemClock.elapsedRealtime() + UPDATE_INTERVAL_MS;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, nextUpdateTime, pendingIntent);
    }

    public void updateImmediately(int widgetId) {
        Intent intent = newUpdateIntent();
        context.sendBroadcast(intent);
    }

    private Intent newUpdateIntent() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int widgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        return new Intent(context, WidgetProvider.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
    }
}
