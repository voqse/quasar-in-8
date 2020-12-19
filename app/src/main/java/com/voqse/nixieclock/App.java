package com.voqse.nixieclock;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;

import com.voqse.nixieclock.widget.ScreenOnListener;
import com.voqse.nixieclock.widget.WidgetServiceUpdater;
import com.voqse.nixieclock.widget.WidgetUpdater;

import org.slf4j.impl.custom.Level;
import org.slf4j.impl.custom.NativeLoggerAdapter;
import org.slf4j.impl.custom.loggers.CompositeLogger;
import org.slf4j.impl.custom.loggers.LogcatLogger;

import static com.voqse.nixieclock.BuildConfig.DEBUG;

/**
 * Nixie Clock app.
 *
 * Своевременное обнвления виджетов (при смене минуты) обеспечивается несколькими механизмами:
 * - updatePeriodMillis в widget.xml . Наиболее слабый механизм. Система не будет обновлять виджеты каждую минуту
 * - {@link ScreenOnListener} лисенер включения экрана. Не сработает, если система убъет процесс
 * - {@link WidgetUpdater} использует {@link android.app.AlarmManager}, чтобы запланировать следующий апдейт.
 *      Работает неплохо, но, заснув, система не реагирует на алярмы.
 * - {@link WidgetServiceUpdater} легковесный сервис, использующий {@link android.os.Handler} для планирования апдейтов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class App extends Application {

    private WidgetUpdater widgetUpdater;

    @Override
    public void onCreate() {
        super.onCreate();

        initLogger();

        this.widgetUpdater = new WidgetUpdater(this);
        this.widgetUpdater.scheduleNextUpdate();
        ScreenOnListener.listenForScreenOn(this);
        WidgetServiceUpdater.enqueueWork(this);
    }

    private void initLogger() {
        Level minLoggableLevel = DEBUG ? Level.TRACE : Level.DEBUG;
        NativeLoggerAdapter.setLogger(new CompositeLogger(new LogcatLogger(minLoggableLevel)));
    }

    public static WidgetUpdater getWidgetUpdater(@NonNull Context context) {
        return ((App) context.getApplicationContext()).widgetUpdater;
    }
}
