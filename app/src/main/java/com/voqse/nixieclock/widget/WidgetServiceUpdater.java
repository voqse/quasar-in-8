package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.voqse.nixieclock.App;
import com.voqse.nixieclock.utils.NixieUtils;

/**
 * Легкий сервис для обновления виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class WidgetServiceUpdater extends JobIntentService {

    private static final String TAG = "WidgetServiceUpdater";
    public static final int JOB_ID = 42;
    private static Intent mIntent = null;

    public static void enqueueWork(Context context) {
        if (mIntent == null) {
            Log.d(TAG, "enqueueWork: Service enqueueWork called");
            enqueueWork(context, WidgetServiceUpdater.class, JOB_ID, new Intent(context, WidgetServiceUpdater.class));
        } else {
            Log.d(TAG, "enqueueWork: Service skipped the same work");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Service ready");
    }

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        mIntent = intent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d(TAG, "run: Schedule widgets update from Service id: " + System.identityHashCode(this));
//                    WidgetProvider.widgetUpdater.scheduleNextUpdate();

                    long thisMinuteEnd = NixieUtils.getNextMinuteStart() - 5000;
                    long delay = thisMinuteEnd - System.currentTimeMillis();

                    try {
                        if (delay >= 0) {
                            Log.d(TAG, "run: Service planned next run on short " +
                                    NixieUtils.formatTimeDetails(System.currentTimeMillis() + delay));
                            Thread.sleep(delay);
                        } else {
                            Log.d(TAG, "run: Service planned next run on long " +
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
        Log.d(TAG, "onDestroy: Service killed");
    }
}
