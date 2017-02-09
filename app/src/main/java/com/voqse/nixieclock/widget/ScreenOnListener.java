package com.voqse.nixieclock.widget;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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

    public static void listenForScreenOn(Application application) {
        application.registerReceiver(new ScreenOnListener(), new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @DebugLog
    @Override
    public void onReceive(Context context, Intent intent) {
        App.getWidgetUpdater(context).updateImmediately();
        WidgetServiceUpdater.wakeUp(context);
    }
}
