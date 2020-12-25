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
    private final Handler mHandler = new Handler();
    private static Intent mIntent = null;
    private WidgetUpdater widgetUpdater;

    public static void enqueueWork(Context context) {
        if (mIntent == null) {
            LOG.debug("Service enqueueWork called");
            enqueueWork(context, WidgetServiceUpdater.class, JOB_ID,
                    new Intent(context, WidgetServiceUpdater.class));
        } else {
            LOG.debug("Service skipped the same work");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.debug("Service created");
        widgetUpdater = App.getWidgetUpdater(this);
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        mIntent = intent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    LOG.debug("Schedule widgets update from Service id: {}",
                            System.identityHashCode(this));
                    widgetUpdater.scheduleNextUpdate();

                    long thisMinuteEnd = NixieUtils.getNextMinuteStart() - 5000;
                    long delay = thisMinuteEnd - System.currentTimeMillis();

                    try {
                        if (delay >= 0) {
                            LOG.debug("Service planned next run on short " +
                                    NixieUtils.formatTimeDetails(System.currentTimeMillis() + delay));
                            Thread.sleep(delay);
                        } else {
                            LOG.debug("Service planned next run on long " +
                                    NixieUtils.formatTimeDetails(System.currentTimeMillis() + delay + 60000));
                            Thread.sleep(delay + 60000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOG.debug("Service destroyed");
    }
}
