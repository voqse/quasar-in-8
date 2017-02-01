package com.voqse.nixieclock.widget;

import com.voqse.nixieclock.clock.ExternalApp;
import com.voqse.nixieclock.theme.Theme;

import java.util.TimeZone;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class WidgetOptions {

    public static final WidgetOptions DEFAULT = new WidgetOptions(true, TimeZone.getDefault().getID(), false, ExternalApp.DEFAULT_APP, Theme.NEO);

    public final boolean format24;
    public final String timeZoneId;
    public final boolean monthFirst;
    public final ExternalApp appToLaunch;
    public final Theme theme;

    public WidgetOptions(boolean format24, String timeZoneId, boolean monthFirst, ExternalApp appToLaunch, Theme theme) {
        this.format24 = format24;
        this.timeZoneId = timeZoneId;
        this.monthFirst = monthFirst;
        this.appToLaunch = appToLaunch;
        this.theme = theme;
    }

    public WidgetOptions changeFormat24(boolean format24) {
        return new WidgetOptions(format24, this.timeZoneId, this.monthFirst, this.appToLaunch, this.theme);
    }

    public WidgetOptions changeTimeZoneId(String timeZoneId) {
        return new WidgetOptions(this.format24, timeZoneId, this.monthFirst, this.appToLaunch, this.theme);
    }

    public WidgetOptions changeMonthFirst(boolean monthFirst) {
        return new WidgetOptions(this.format24, this.timeZoneId, monthFirst, this.appToLaunch, this.theme);
    }

    public WidgetOptions changeAppToLaunch(ExternalApp appToLaunch) {
        return new WidgetOptions(this.format24, this.timeZoneId, this.monthFirst, appToLaunch, this.theme);
    }

    public WidgetOptions changeTheme(Theme theme) {
        return new WidgetOptions(this.format24, this.timeZoneId, this.monthFirst, appToLaunch, theme);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WidgetOptions that = (WidgetOptions) o;

        if (format24 != that.format24) return false;
        if (monthFirst != that.monthFirst) return false;
        if (!timeZoneId.equals(that.timeZoneId)) return false;
        if (!appToLaunch.equals(that.appToLaunch)) return false;
        return theme == that.theme;

    }

    @Override
    public int hashCode() {
        int result = (format24 ? 1 : 0);
        result = 31 * result + timeZoneId.hashCode();
        result = 31 * result + (monthFirst ? 1 : 0);
        result = 31 * result + appToLaunch.hashCode();
        result = 31 * result + theme.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WidgetOptions{");
        sb.append("format24=").append(format24);
        sb.append(", timeZoneId='").append(timeZoneId).append('\'');
        sb.append(", monthFirst=").append(monthFirst);
        sb.append(", appToLaunch=").append(appToLaunch);
        sb.append(", theme=").append(theme);
        sb.append('}');
        return sb.toString();
    }
}
