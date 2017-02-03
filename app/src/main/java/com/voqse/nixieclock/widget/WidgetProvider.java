package com.voqse.nixieclock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.Drawer;
import com.voqse.nixieclock.utils.NixieUtils;

import hugo.weaving.DebugLog;

/**
 * Defines the basic methods that allow you to programmatically interface with the App Widget, based on broadcast events.
 * It will receive broadcasts when the App Widget is updated, enabled, disabled and deleted.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_TEXT_MODE = BuildConfig.APPLICATION_ID + ".EXTRA_TEXT_MODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    TextMode textMode = extras.containsKey(EXTRA_TEXT_MODE) ?
                            (TextMode) extras.getSerializable(EXTRA_TEXT_MODE) : TextMode.TIME;
                    onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds, textMode);
                }
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        onUpdate(context, appWidgetManager, appWidgetIds, TextMode.TIME);
    }

    @DebugLog
    private void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, TextMode textMode) {
        if (appWidgetIds.length > 0) {
            App.getWidgetUpdater(context).scheduleNextUpdate();
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
        Settings settings = new Settings(context);
        settings.remove(appWidgetIds);
    }

    private void updateWidget(Context context, Settings settings, AppWidgetManager appWidgetManager, int widgetId, TextMode textMode) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);
        Bitmap bitmap = getWidgetBitmap(context, widgetOptions, textMode);
        views.setImageViewBitmap(R.id.imageView, bitmap);
        views.setOnClickPendingIntent(R.id.imageView, newClickIntent(context, widgetId));
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private Bitmap getWidgetBitmap(Context context, WidgetOptions widgetOptions, TextMode textMode) {
        Drawer drawer = new Drawer(context);
        return drawer.draw(widgetOptions, null, textMode);
    }

    private PendingIntent newClickIntent(Context context, int widgetId) {
        Intent intent = WidgetClickHandler.newIntent(context, widgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
