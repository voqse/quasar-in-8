package com.voqse.nixieclock.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.voqse.nixieclock.utils.NixieUtils;

import java.util.Arrays;


/**
 * класс для обновления виджета, использующий {@link AlarmManager}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetUpdater {

    private static final String TAG = "WidgetUpdater";
    private static final long MODE_CHANGE_DELAY_MS = 2000;
    private final AlarmManager alarmManager;
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public WidgetUpdater(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNextUpdate() {
        PendingIntent pendingIntent = newPendingUpdateIntent();
        alarmManager.cancel(pendingIntent);

        long nextUpdateTime = NixieUtils.getNextMinuteStart();
        Log.d(TAG, "scheduleNextUpdate: Schedule next update on: " + NixieUtils.formatTimeDetails(nextUpdateTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC, nextUpdateTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC, nextUpdateTime, pendingIntent);
        }
    }

    public void updateImmediately() {
        PendingIntent pendingIntent = newPendingUpdateIntent();
        alarmManager.cancel(pendingIntent);

        Intent intent = newUpdateIntent();
        context.sendBroadcast(intent);
    }

    public void cancelNextUpdate() {
        Log.d(TAG, "cancelNextUpdate: Canceling next update");
        PendingIntent pendingIntent = newPendingUpdateIntent();
        alarmManager.cancel(pendingIntent);
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
                Intent intent = newUpdateIntent(new int[] { widgetId }, textMode);
                context.sendBroadcast(intent);
            }
        }, delay);
    }

    private PendingIntent newPendingUpdateIntent() {
        return newPendingUpdateIntent(newUpdateIntent());
    }

    private PendingIntent newPendingUpdateIntent(Intent broadcastIntent) {
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent newUpdateIntent() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = getWidgetsIds(appWidgetManager);
        return newUpdateIntent(widgetIds, TextMode.TIME);
    }

    private Intent newUpdateIntent(int[] widgetIds, TextMode textMode) {
        Log.d(TAG, "newUpdateIntent: Put IDs in Update Intent: " + Arrays.toString(widgetIds));
        return new Intent(context, WidgetProvider.class)
                .setAction(WidgetProvider.CLOCK_TICK_ACTION)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                .putExtra(WidgetProvider.EXTRA_TEXT_MODE, textMode)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    private int[] getWidgetsIds(AppWidgetManager appWidgetManager) {
        try {
            return appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        } catch (RuntimeException e) {
            // it seems due to aggressive politic to using system resources OS kills IAppWidgetService http://crashes.to/s/6661e7d5bf1
            Log.d(TAG, "getWidgetsIds: Error querying list of widget ids", e);
            return new int[0];
        }
    }


}
