package com.voqse.nixieclock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.theme.drawer.Drawer;
import com.voqse.nixieclock.theme.drawer.DrawerNew;
import com.voqse.nixieclock.utils.NixieUtils;

import java.util.Arrays;

import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT;
import static android.appwidget.AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH;

/**
 * Defines the basic methods that allow you to programmatically interface with the App Widget, based on broadcast events.
 * It will receive broadcasts when the App Widget is updated, enabled, disabled and deleted.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_DATA_CHANGED_ACTION = "com.voqse.nixieclock.widget.WIDGET_DATA_CHANGED";
    public static final String CLOCK_TICK_ACTION = "com.voqse.nixieclock.widget.CLOCK_TICK";

    private static final String TAG = "WidgetProvider";
    public static final String EXTRA_TEXT_MODE = BuildConfig.APPLICATION_ID + ".EXTRA_TEXT_MODE";
    private static int widgetsCount = 0;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        App.setWidgetUpdater(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        if (App.getWidgetUpdater(context) == null) {
            Log.d(TAG, "onDisabled: There's no WidgetUpdater, setup another one");
            App.setWidgetUpdater(context);
        }

        App.getWidgetUpdater(context).cancelNextUpdate();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CLOCK_TICK_ACTION.equals(intent.getAction()) || WIDGET_DATA_CHANGED_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: Clock tick or data changed action received");

            Bundle extras = intent.getExtras();

            if (extras != null) {
//                Log.d(TAG, "onReceive: Intent have extras: " + extras);

                for (String key : extras.keySet()) {
                    Log.e(TAG, key + " : " + (extras.get(key) != null ? extras.get(key).getClass().isArray() ? Arrays.toString(extras.getIntArray(key)) : extras.get(key) : "NULL"));
                }

                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);

                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    Log.d(TAG, "onReceive: Intent has widget IDs: " + Arrays.toString(appWidgetIds));
                    TextMode textMode = extras.containsKey(EXTRA_TEXT_MODE) ?
                            (TextMode) extras.getSerializable(EXTRA_TEXT_MODE) : TextMode.TIME;
                    doUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds, textMode);
                } else {
                    Log.d(TAG, "onReceive: Intent has no widget IDs, update all the widgets");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    doUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass())), TextMode.TIME);
                }
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        doUpdate(context, appWidgetManager, appWidgetIds, TextMode.TIME);
    }

    private void doUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, TextMode textMode) {
        widgetsCount = appWidgetIds.length;
        Log.d(TAG, "doUpdate: Update widgets (" + widgetsCount + ")");

        if (App.getWidgetUpdater(context) == null) {
            Log.d(TAG, "doUpdate: There's no WidgetUpdater, setup another one");
            App.setWidgetUpdater(context);
        }

        if (widgetsCount > 0) {
            App.getWidgetUpdater(context).scheduleNextUpdate();
//            WidgetServiceUpdater.enqueueWork(context);
        }

        if (!NixieUtils.isDeviceActive(context)) {
            return;
        }

        Settings settings = new Settings(context);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, settings, appWidgetManager, appWidgetId, textMode);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        Settings settings = new Settings(context);
        settings.remove(appWidgetIds);
    }

    private void updateWidget(Context context, Settings settings, AppWidgetManager appWidgetManager, int widgetId, TextMode textMode) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);
        boolean maxQuality = isMaxQuality(context, widgetOptions.theme, appWidgetManager, widgetId);
        Bitmap bitmap = getWidgetBitmap(context, widgetOptions, textMode, maxQuality);
        views.setImageViewBitmap(R.id.imageView, bitmap);
        views.setOnClickPendingIntent(R.id.imageView, newClickIntent(context, widgetId));

        try {
            // it seems due to aggressive politic to using system resources OS kills IAppWidgetService http://crashes.to/s/57acde098f7
            appWidgetManager.updateAppWidget(widgetId, views);
        } catch (RuntimeException e) {
            Log.d(TAG, "updateWidget: Error updating widget " + widgetId, e);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        this.doUpdate(context, appWidgetManager, new int[]{appWidgetId}, TextMode.TIME);
    }

    private Bitmap getWidgetBitmap(Context context, WidgetOptions widgetOptions, TextMode textMode, boolean maxQuality) {
        Drawer drawer;
        if (widgetOptions.theme.isItNew) {
            drawer = new DrawerNew(context);
        } else {
            drawer = new Drawer(context);
        }
        return drawer.draw(widgetOptions, textMode, maxQuality);
    }

    private boolean isMaxQuality(Context context, Theme theme, AppWidgetManager appWidgetManager, int widgetId) {
        // https://developer.android.com/reference/android/appwidget/AppWidgetManager.html#updateAppWidget(int, android.widget.RemoteViews)
        // The total Bitmap memory used by the RemoteViews object cannot exceed
        // that required to fill the screen 1.5 times, ie. (screen width x screen height x 4 x 1.5) bytes.
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        boolean x2exceedQuota = theme.x2BytesSize > displayMetrics.widthPixels * displayMetrics.heightPixels * 4 * 1.5;
        if (x2exceedQuota) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(widgetId);
            int maxWidthDp = appWidgetOptions.getInt(OPTION_APPWIDGET_MAX_WIDTH);
            int maxHeightDp = appWidgetOptions.getInt(OPTION_APPWIDGET_MAX_HEIGHT);
            int maxWidthPx = NixieUtils.dipToPixels(context, maxWidthDp);
            int maxHeightPx = NixieUtils.dipToPixels(context, maxHeightDp);
            return maxWidthPx > Drawer.X2_MIN_WIDTH_PX && maxHeightPx >= Drawer.X2_MIN_HEIGHT_PX;
        } else {
            return displayMetrics.widthPixels > Drawer.X2_MIN_WIDTH_PX && displayMetrics.heightPixels > Drawer.X2_MIN_HEIGHT_PX;
        }
    }

    private PendingIntent newClickIntent(Context context, int widgetId) {
        Intent intent = WidgetClickHandler.newIntent(context, widgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
