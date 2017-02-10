package com.voqse.nixieclock.timezone;

import java.util.TimeZone;

/**
 * Базовая информация о таймзоне.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TimeZoneInfo {

    public final String id;
    public final String city;
    public final int rawOffset;

    public TimeZoneInfo(String id, String city, int rawOffset) {
        this.id = id;
        this.city = city;
        this.rawOffset = rawOffset;
    }

    public String getPrettyOffset() {
        return TimeZone.getTimeZone(id).getDisplayName(false, TimeZone.SHORT);
    }
}
