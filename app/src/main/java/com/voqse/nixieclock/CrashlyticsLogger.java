package com.voqse.nixieclock;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.slf4j.helpers.MessageFormatter;
import org.slf4j.impl.custom.Level;
import org.slf4j.impl.custom.LogRecord;
import org.slf4j.impl.custom.Logger;

public class CrashlyticsLogger implements Logger {

    @Override
    public boolean isEnabled(String name, Level level) {
        return true;
    }

    @Override
    public void log(LogRecord logRecord) {
        Crashlytics.log(logRecord.getLogger() + " - " + format(logRecord));

        Throwable error = logRecord.getThrowable();
        if (logRecord.getLevel() == Level.ERROR || error != null) {
            error = error == null ? new NonFatalError("") : error; // create exception to notify crashlytics
            Crashlytics.log(Log.getStackTraceString(error));
            Crashlytics.logException(error);
        }
    }

    private String format(LogRecord logRecord) {
        return format(logRecord.getPattern(), logRecord.getParameters());
    }

    private String format(final String format, final Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}