package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.voqse.nixieclock.clock.ExternalApp;
import com.voqse.nixieclock.theme.Theme;

import java.util.Set;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class Settings {

    private static final String KEY_24_TIME_FORMAT = "24_time_format";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_MONTH_FIRST = "month_first";
    private static final String KEY_THEME = "theme";
    private static final String KEY_HIDE_ICON = "hide_icon";
    private static final String KEY_APP_TO_LAUNCH = "app_to_launch";

    private final SharedPreferences preferences;

    public Settings(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public WidgetOptions getWidgetOptions(int widgetId) {
        String format24Key = getWidgetKey(KEY_24_TIME_FORMAT, widgetId);
        String timeZoneKey = getWidgetKey(KEY_TIMEZONE, widgetId);
        String monthFirstKey = getWidgetKey(KEY_MONTH_FIRST, widgetId);
        String appToLaunchKey = getWidgetKey(KEY_APP_TO_LAUNCH, widgetId);
        String themeKey = getWidgetKey(KEY_THEME, widgetId);
        return new WidgetOptions(
                preferences.getBoolean(format24Key, WidgetOptions.DEFAULT.format24),
                preferences.getString(timeZoneKey, WidgetOptions.DEFAULT.timeZoneId),
                preferences.getBoolean(monthFirstKey, WidgetOptions.DEFAULT.monthFirst),
                ExternalApp.fromString(preferences.getString(appToLaunchKey, ExternalApp.DEFAULT_APP.pack())),
                Theme.valueOf(preferences.getString(themeKey, WidgetOptions.DEFAULT.theme.name()))
        );
    }

    public void setWidgetOptions(int widgetId, WidgetOptions widgetOptions) {
        preferences.edit()
                .putBoolean(getWidgetKey(KEY_24_TIME_FORMAT, widgetId), widgetOptions.format24)
                .putString(getWidgetKey(KEY_TIMEZONE, widgetId), widgetOptions.timeZoneId)
                .putBoolean(getWidgetKey(KEY_MONTH_FIRST, widgetId), widgetOptions.monthFirst)
                .putString(getWidgetKey(KEY_APP_TO_LAUNCH, widgetId), widgetOptions.appToLaunch.pack())
                .putString(getWidgetKey(KEY_THEME, widgetId), widgetOptions.theme.name())
                .apply();
    }

    public boolean isHideIcon() {
        return preferences.getBoolean(KEY_HIDE_ICON, false);
    }

    public void setHideIcon(boolean hide) {
        put(KEY_HIDE_ICON, hide);
    }

    public void remove(int... widgetIds) {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> keys = preferences.getAll().keySet();
        for (int widgetId : widgetIds) {
            for (String key : keys) {
                String widgetIdString = Integer.toString(widgetId);
                if (key.endsWith(widgetIdString)) {
                    editor.remove(key);
                }
            }
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
