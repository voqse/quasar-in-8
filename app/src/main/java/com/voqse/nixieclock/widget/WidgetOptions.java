package com.voqse.nixieclock.widget;

import com.voqse.nixieclock.theme.Theme;

import java.util.TimeZone;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class WidgetOptions {

    public static final WidgetOptions DEFAULT = new WidgetOptions(true, TimeZone.getDefault().getID(), false, true, Theme.NEO);

    public final boolean format24;
    public final String timeZoneId;
    public final boolean monthFirst;
    public final boolean displayTime;
    public final Theme theme;

    public WidgetOptions(boolean format24, String timeZoneId, boolean monthFirst, boolean displayTime, Theme theme) {
        this.format24 = format24;
        this.timeZoneId = timeZoneId;
        this.monthFirst = monthFirst;
        this.displayTime = displayTime;
        this.theme = theme;
    }
}
