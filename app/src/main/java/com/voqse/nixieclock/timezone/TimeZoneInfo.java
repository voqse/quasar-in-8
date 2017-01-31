package com.voqse.nixieclock.timezone;

import java.util.TimeZone;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class TimeZoneInfo {

    public final String id;
    public final String city;

    public TimeZoneInfo(String id, String city) {
        this.id = id;
        this.city = city;
    }

    public String getOffset() {
        return TimeZone.getTimeZone(id).getDisplayName(false, TimeZone.SHORT);
    }
}
