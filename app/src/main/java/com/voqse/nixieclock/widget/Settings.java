package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.voqse.nixieclock.clock.ExternalApp;
import com.voqse.nixieclock.theme.Theme;

import java.util.HashSet;
import java.util.Set;

/**
 * Настройки виджетов и приложения.
 * <p>
 * Для хранения используется {@link SharedPreferences}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class Settings {

    private static final String KEY_24_TIME_FORMAT = "24_time_format";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_MONTH_FIRST = "month_first";
    private static final String KEY_THEME = "theme";
    private static final String KEY_HIDE_ICON = "hide_icon";
    private static final String KEY_APP_TO_LAUNCH = "app_to_launch";
    private static final String KEY_USE_SYSTEM_PREFERENCES = "use_system_preferences";
    private static final String KEY_LIVE_WIDGETS = "live_widgets"; // to avoid problem with phantom widgets https://code.google.com/p/android/issues/detail?id=2539

    private final SharedPreferences preferences;
    private final Context context;

    public Settings(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public WidgetOptions getWidgetOptions(int widgetId) {
        String format24Key = getWidgetKey(KEY_24_TIME_FORMAT, widgetId);
        String timeZoneKey = getWidgetKey(KEY_TIMEZONE, widgetId);
        String monthFirstKey = getWidgetKey(KEY_MONTH_FIRST, widgetId);
        String appToLaunchKey = getWidgetKey(KEY_APP_TO_LAUNCH, widgetId);
        String themeKey = getWidgetKey(KEY_THEME, widgetId);
        String useSystemDefault = getWidgetKey(KEY_USE_SYSTEM_PREFERENCES, widgetId);
        WidgetOptions defaultOptions = WidgetOptions.getDefault(context);
        return new WidgetOptions(
                preferences.getBoolean(format24Key, defaultOptions.format24),
                preferences.getString(timeZoneKey, defaultOptions.timeZoneId),
                preferences.getBoolean(monthFirstKey, defaultOptions.monthFirst),
                ExternalApp.fromString(preferences.getString(appToLaunchKey, defaultOptions.appToLaunch.pack())),
                Theme.valueOf(preferences.getString(themeKey, defaultOptions.theme.name())),
                preferences.getBoolean(useSystemDefault, defaultOptions.useSystemDefault)
        );
    }

    public void setWidgetOptions(int widgetId, WidgetOptions widgetOptions) {
        Set<String> liveWidgetIds = preferences.getStringSet(KEY_LIVE_WIDGETS, new HashSet<String>());
        liveWidgetIds.add(Integer.toString(widgetId));
        preferences.edit()
                .putBoolean(getWidgetKey(KEY_24_TIME_FORMAT, widgetId), widgetOptions.format24)
                .putString(getWidgetKey(KEY_TIMEZONE, widgetId), widgetOptions.timeZoneId)
                .putBoolean(getWidgetKey(KEY_MONTH_FIRST, widgetId), widgetOptions.monthFirst)
                .putString(getWidgetKey(KEY_APP_TO_LAUNCH, widgetId), widgetOptions.appToLaunch.pack())
                .putString(getWidgetKey(KEY_THEME, widgetId), widgetOptions.theme.name())
                .putBoolean(getWidgetKey(KEY_USE_SYSTEM_PREFERENCES, widgetId), widgetOptions.useSystemDefault)
                .putStringSet(KEY_LIVE_WIDGETS, liveWidgetIds)
                .apply();
    }

    public boolean isHideIcon() {
        return preferences.getBoolean(KEY_HIDE_ICON, false);
    }

    public boolean isUseSystemPreferences() {
        return preferences.getBoolean(KEY_USE_SYSTEM_PREFERENCES, true);
    }

    public void setHideIcon(boolean hide) {
        put(KEY_HIDE_ICON, hide);
    }

//    public void setUseSystemPreferences(boolean useSystemPreferences) {
//        put(KEY_USE_SYSTEM_PREFERENCES, useSystemPreferences);
//    }

    public void remove(int... widgetIds) {
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> keys = preferences.getAll().keySet();
        Set<String> liveWidgetIds = preferences.getStringSet(KEY_LIVE_WIDGETS, new HashSet<String>());
        for (int widgetId : widgetIds) {
            liveWidgetIds.remove(Integer.toString(widgetId));
            for (String key : keys) {
                String widgetIdString = Integer.toString(widgetId);
                if (key.endsWith(widgetIdString)) {
                    editor.remove(key);
                }
            }
        }
        editor.putStringSet(KEY_LIVE_WIDGETS, liveWidgetIds);
        editor.apply();
    }

    public Set<Integer> getLiveWidgets() {
        Set<String> widgetIdsSet = preferences.getStringSet(KEY_LIVE_WIDGETS, new HashSet<String>());
        Set<Integer> result = new HashSet<>();
        for (String widgetId : widgetIdsSet) {
            result.add(Integer.parseInt(widgetId));
        }
        return result;
    }

    private String getWidgetKey(String key, int widgetId) {
        return key + "_" + widgetId;
    }

    private void put(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }
}
