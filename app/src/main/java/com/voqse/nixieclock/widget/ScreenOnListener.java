package com.voqse.nixieclock.widget;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.voqse.nixieclock.App;

import hugo.weaving.DebugLog;

/**
 * Слушатель включения экрана девайса.
 * <p>
 * При включении обновляет виджеты. Будит сервис для обновления виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ScreenOnListener extends BroadcastReceiver {

    private static final String TAG = "ScreenOnListener";

    public static void listenForScreenOn(Application application) {
        application.registerReceiver(new ScreenOnListener(), new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
//
//        }

        Log.d(TAG, "onReceive: Screen ON action caught");

        if (App.getWidgetUpdater(context) == null) {
            Log.d(TAG, "onReceive: There's no WidgetUpdater, setup another one");
            App.setWidgetUpdater(context);
        }

        App.getWidgetUpdater(context).updateImmediately();
//        WidgetServiceUpdater.enqueueWork(context);
    }
}
