package com.voqse.nixieclock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.Drawer;

/**
 * Defines the basic methods that allow you to programmatically interface with the App Widget, based on broadcast events.
 * It will receive broadcasts when the App Widget is updated, enabled, disabled and deleted.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Settings settings = new Settings(context);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, settings, appWidgetManager, appWidgetId);
        }

        if (appWidgetIds.length > 0) {
            App.getWidgetUpdater(context).scheduleNextUpdate();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Settings settings = new Settings(context);
        settings.remove(appWidgetIds);
    }

    private void updateWidget(Context context, Settings settings, AppWidgetManager appWidgetManager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);
        Bitmap bitmap = getWidgetBitmap(context, widgetOptions);
        views.setImageViewBitmap(R.id.imageView, bitmap);
        views.setOnClickPendingIntent(R.id.imageView, newClickIntent(context, widgetId));
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private Bitmap getWidgetBitmap(Context context, WidgetOptions widgetOptions) {
        Drawer drawer = new Drawer(context);
        return drawer.draw(widgetOptions, null);
    }

    private PendingIntent newClickIntent(Context context, int widgetId) {
        Intent intent = WidgetClickHandler.newIntent(context, widgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
