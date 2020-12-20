package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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
    private final Handler handler = new Handler(Looper.getMainLooper());
    private WidgetUpdater widgetUpdater;

    public static void enqueueWork(Context context) {
        LOG.debug("Service enqueueWork called");
        enqueueWork(context, WidgetServiceUpdater.class, JOB_ID,
                new Intent(context, WidgetServiceUpdater.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.debug("Service created");
        widgetUpdater = App.getWidgetUpdater(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        long thisMinuteEnd = NixieUtils.getNextMinuteStart() - 10000;
        long delay = thisMinuteEnd - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LOG.debug("Schedule widgets update from Service");
                widgetUpdater.scheduleNextUpdate();
            }
        }, delay);
        LOG.debug("Service planed next update on " + NixieUtils.formatTimeDetails(thisMinuteEnd));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOG.debug("Service destroyed");
    }
}
