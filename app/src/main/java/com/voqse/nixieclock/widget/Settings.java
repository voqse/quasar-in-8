package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.TimeZone;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class Settings {

    private static final String KEY_24_TIME_FORMAT = "24_time_format";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_MONTH_FIRST = "month_first";
    private static final String KEY_THEME = "theme";
    private static final String KEY_HIDE_ICON = "hide_icon";

    private final SharedPreferences preferences;

    public Settings(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean is24TimeFormat(int widgetId) {
        String widgetKey = getWidgetKey(KEY_24_TIME_FORMAT, widgetId);
        return preferences.getBoolean(widgetKey, true);
    }

    public void setTimeFormat(int widgetId, boolean format24) {
        String widgetKey = getWidgetKey(KEY_24_TIME_FORMAT, widgetId);
        put(widgetKey, format24);
    }

    public boolean isMonthFirst(int widgetId) {
        String widgetKey = getWidgetKey(KEY_MONTH_FIRST, widgetId);
        return preferences.getBoolean(widgetKey, false);
    }

    public void setMonthFirst(int widgetId, boolean monthFirst) {
        String widgetKey = getWidgetKey(KEY_MONTH_FIRST, widgetId);
        put(widgetKey, monthFirst);
    }

    public String getTimeZone(int widgetId) {
        String widgetKey = getWidgetKey(KEY_TIMEZONE, widgetId);
        return preferences.getString(widgetKey, TimeZone.getDefault().getID());
    }

    public void setTimezone(int widgetId, String timezoneId) {
        String widgetKey = getWidgetKey(KEY_TIMEZONE, widgetId);
        put(widgetKey, timezoneId);
    }

    public Theme getTheme(int widgetId) {
        String widgetKey = getWidgetKey(KEY_THEME, widgetId);
        String themeName = preferences.getString(widgetKey, Theme.QUASAR_1.name());
        return Theme.valueOf(themeName);
    }

    public void setTheme(int widgetId, Theme theme) {
        String widgetKey = getWidgetKey(KEY_THEME, widgetId);
        put(widgetKey, theme.name());
    }

    public boolean isHideIcon() {
        return preferences.getBoolean(KEY_HIDE_ICON, false);
    }

    public void setHideIcon(boolean hide) {
        put(KEY_HIDE_ICON, hide);
    }

    public void remove(int... widgetIds) {
        SharedPreferences.Editor editor = preferences.edit();
        for (int widgetId : widgetIds) {
            String widgetKey = getWidgetKey(KEY_24_TIME_FORMAT, widgetId);
            editor.remove(widgetKey);
        }
        editor.apply();
    }

    private String getWidgetKey(String key, int widgetId) {
        return key + "_" + widgetId;
    }

    private void put(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    private void put(String key, String value) {
        preferences.edit()
                .putString(key, value)
                .apply();
    }
}
