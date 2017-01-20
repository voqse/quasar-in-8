package com.voqse.nixieclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.voqse.nixieclock.widget.WidgetProvider;

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
        PendingIntent pendingIntent = newPendingIntent();
        alarmManager.cancel(pendingIntent);
        long nextUpdateTime = SystemClock.elapsedRealtime() + UPDATE_INTERVAL_MS;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, nextUpdateTime, pendingIntent);
    }

    private PendingIntent newPendingIntent() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int widgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        Intent intent = new Intent(context, WidgetProvider.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
