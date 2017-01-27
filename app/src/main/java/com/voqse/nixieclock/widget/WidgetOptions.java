package com.voqse.nixieclock.widget;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class WidgetOptions {

    public final boolean format24;
    public final String timeZoneId;
    public final boolean monthFirst;
    public final Theme theme;

    public WidgetOptions(boolean format24, String timeZoneId, boolean monthFirst, Theme theme) {
        this.format24 = format24;
        this.timeZoneId = timeZoneId;
        this.monthFirst = monthFirst;
        this.theme = theme;
    }
}
