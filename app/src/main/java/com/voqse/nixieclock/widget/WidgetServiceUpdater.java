package com.voqse.nixieclock.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.utils.NixieUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Легкий сервис для обновления виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetServiceUpdater extends Service {

    private static final Logger LOG = LoggerFactory.getLogger("WidgetServiceUpdater");

    private UpdateSchedulerHandler handler;

    public static void wakeUp(Context context) {
        context.startService(new Intent(context, WidgetServiceUpdater.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        WidgetUpdater widgetUpdater = App.getWidgetUpdater(this);
        this.handler = new UpdateSchedulerHandler(widgetUpdater);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.schedule();
        return START_STICKY;
    }

    private static final class UpdateSchedulerHandler extends Handler {

        private final WidgetUpdater widgetUpdater;

        private UpdateSchedulerHandler(WidgetUpdater widgetUpdater) {
            this.widgetUpdater = widgetUpdater;
        }

        private void schedule() {
            removeMessages(0);
            long thisMinuteEnd = NixieUtils.getNextMinuteStart() - 1000;
            long delay = thisMinuteEnd - System.currentTimeMillis();
            sendMessageDelayed(obtainMessage(0), delay);
        }

        @Override
        public void handleMessage(Message msg) {
            LOG.debug("Schedule widget update form service");
            widgetUpdater.scheduleNextUpdate();
        }
    }
}
