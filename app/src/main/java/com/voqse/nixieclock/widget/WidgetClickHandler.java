package com.voqse.nixieclock.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.clock.ExternalApp;

import java.util.UUID;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;

/**
 * Handles tab and double tap to widget.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetClickHandler extends BroadcastReceiver {

    private static final String TAG = "WidgetClickHandler";
    private static final ClickHandler CLICK_HANDLER = new ClickHandler(500);

    public static Intent newIntent(Context context, int widgetId) {
        return new Intent(context, WidgetClickHandler.class)
                .setAction(UUID.randomUUID().toString())
                .putExtra(EXTRA_APPWIDGET_ID, widgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        validateIntent(intent);
        int widgetId = intent.getIntExtra(EXTRA_APPWIDGET_ID, -1);
        CLICK_HANDLER.onClick(context.getApplicationContext(), widgetId);
    }

    private void validateIntent(Intent intent) {
        if (intent == null) {
            throw new NullPointerException("Click intent is null!");
        }
        if (!intent.hasExtra(EXTRA_APPWIDGET_ID)) {
            throw new IllegalArgumentException("Invalid click intent: there is no widget's id in extras!");
        }
    }

    private static class ClickHandler extends Handler {

        private final int doubleClickDelay;

        private ClickHandler(int doubleClickDelay) {
            this.doubleClickDelay = doubleClickDelay;
        }

        void onClick(Context context, int widgetId) {
            Message message = obtainMessage(0, widgetId, 0, context);
            sendMessageDelayed(message, doubleClickDelay);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = (Context) msg.obj;
            int widgetId = msg.arg1;
            if (hasMessages(0)) {
                removeMessages(0);
                onDoubleClick(context, widgetId);
            } else {
                onSingleClick(context, widgetId);
            }
        }

        private void onSingleClick(Context context, int widgetId) {
            Settings settings = new Settings(context);
            WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);
            ExternalApp app = widgetOptions.appToLaunch;
            try {
                app.launch(context);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "onSingleClick: Error launching external app " + this, e);
                Toast.makeText(context, context.getString(R.string.error_open_app, app.getName(context)), Toast.LENGTH_LONG).show();
            }
        }

        private void onDoubleClick(Context context, int widgetId) {
            WidgetUpdater widgetUpdater = new WidgetUpdater(context);
            widgetUpdater.showDate(widgetId);
        }
    }
}
