package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.utils.NixieUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Легкий сервис для обновления виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetServiceUpdater extends JobIntentService {

    private static final Logger LOG = LoggerFactory.getLogger("WidgetServiceUpdater");
    public static final int JOB_ID = 42;

    private UpdateSchedulerHandler handler;

    public static void enqueueWork(Context context) {
        LOG.debug("Service enqueueWork called");
        enqueueWork(context, WidgetServiceUpdater.class, JOB_ID,
                new Intent(context, WidgetServiceUpdater.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.debug("Service created");
        WidgetUpdater widgetUpdater = App.getWidgetUpdater(this);
        this.handler = new UpdateSchedulerHandler(widgetUpdater);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        handler.schedule();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOG.debug("Service destroyed");
    }

    private static final class UpdateSchedulerHandler extends Handler {
        private final WidgetUpdater widgetUpdater;

        private UpdateSchedulerHandler(WidgetUpdater widgetUpdater) {
            this.widgetUpdater = widgetUpdater;
        }

        private void schedule() {
            removeMessages(0);
            long thisMinuteEnd = NixieUtils.getNextMinuteStart() - 10000;
            long delay = thisMinuteEnd - System.currentTimeMillis();
            sendMessageDelayed(obtainMessage(0), delay);
            LOG.debug("Service planed next update on " + NixieUtils.formatTimeDetails(thisMinuteEnd));
        }

        @Override
        public void handleMessage(Message msg) {
            LOG.debug("Schedule widget update form service");
            widgetUpdater.scheduleNextUpdate();
        }
    }
}
