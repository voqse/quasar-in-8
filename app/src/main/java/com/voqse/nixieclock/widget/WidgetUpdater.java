package com.voqse.nixieclock.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.voqse.nixieclock.utils.NixieUtils;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class WidgetUpdater {

    private static final long MODE_CHANGE_DELAY_MS = 2000;
    private final AlarmManager alarmManager;
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public WidgetUpdater(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNextUpdate() {
        PendingIntent pendingIntent = newPendingUpdateIntent();
        alarmManager.cancel(pendingIntent);

        long nextUpdateTime = NixieUtils.getNextMinuteStart();
        alarmManager.set(AlarmManager.RTC, nextUpdateTime, pendingIntent);
    }

    public void updateImmediately() {
        PendingIntent pendingIntent = newPendingUpdateIntent();
        alarmManager.cancel(pendingIntent);

        Intent intent = newUpdateIntent();
        context.sendBroadcast(intent);
    }

    public void showDate(int widgetId) {
        changeMode(widgetId, TextMode.DATE, 0);
        changeMode(widgetId, TextMode.YEAR, MODE_CHANGE_DELAY_MS);
        changeMode(widgetId, TextMode.TIME, MODE_CHANGE_DELAY_MS * 2);
    }

    private void changeMode(final int widgetId, final TextMode textMode, long delay) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = newUpdateIntent(new int[]{widgetId}, textMode);
                context.sendBroadcast(intent);
            }
        }, delay);
    }

    private PendingIntent newPendingUpdateIntent() {
        return newPendingUpdateIntent(newUpdateIntent());
    }

    private PendingIntent newPendingUpdateIntent(Intent broadcastIntent) {
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private Intent newUpdateIntent() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int widgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        return newUpdateIntent(widgetIds, TextMode.TIME);
    }

    private Intent newUpdateIntent(int widgetIds[], TextMode textMode) {
        return new Intent(context, WidgetProvider.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                .putExtra(WidgetProvider.EXTRA_TEXT_MODE, textMode);
    }
}
