package com.voqse.nixieclock.timezone;

import android.text.TextUtils;

import java.util.TimeZone;

/**
 * Базовая информация о таймзоне.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TimeZoneInfo {

    public final String id;
    public final String deviceTimeZoneId;
    public final String city;

    public TimeZoneInfo(String id, String deviceTimeZoneId, String city) {
        this.id = id;
        this.deviceTimeZoneId = deviceTimeZoneId;
        this.city = city;
    }

    public String getOffset() {
        return TimeZone.getTimeZone(id).getDisplayName(false, TimeZone.SHORT);
    }

    public boolean hasId(String id) {
        // timezone.xml doesn't contain all know timezones. so compare as id from xml as id from device
        return TextUtils.equals(id, this.id) || TextUtils.equals(id, this.deviceTimeZoneId);
    }
}
