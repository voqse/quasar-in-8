package com.voqse.nixieclock.timezone;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.voqse.nixieclock.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class TimeZones {

    private static TimeZones SHARED_INSTANCE;
    private final List<TimeZoneInfo> timeZones;

    private TimeZones(Context context) {
        this.timeZones = parse(context);
    }

    public static List<TimeZoneInfo> getTimeZoneInfo(Context context) {
        TimeZones timeZones = getSharedInstance(context);
        return Collections.unmodifiableList(timeZones.timeZones);
    }

    public static TimeZoneInfo getTimeZoneInfo(Context context, String id) {
        List<TimeZoneInfo> timeZones = getTimeZoneInfo(context);
        for (TimeZoneInfo timeZoneInfo : timeZones) {
            if (TextUtils.equals(id, timeZoneInfo.id)) {
                return timeZoneInfo;
            }
        }
        throw new IllegalArgumentException("Unknown timezone with id " + id);
    }

    @NonNull
    private static TimeZones getSharedInstance(Context context) {
        return SHARED_INSTANCE == null ? (SHARED_INSTANCE = new TimeZones(context)) : SHARED_INSTANCE;
    }

    // https://github.com/android/platform_frameworks_base/blob/master/packages/SettingsLib/src/com/android/settingslib/datetime/ZoneGetter.java
    private List<TimeZoneInfo> parse(Context context) {
        List<TimeZoneInfo> result = new ArrayList<>();
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getXml(R.xml.timezones);
            while (parser.next() != XmlResourceParser.START_TAG) {
                continue;
            }
            parser.next();
            String timeZoneId = null;
            while (parser.getEventType() != XmlResourceParser.END_TAG) {
                while (parser.getEventType() != XmlResourceParser.START_TAG) {
                    if (parser.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return result;
                    }
                    parser.next();
                }
                if (parser.getName().equals("timezone")) {
                    timeZoneId = parser.getAttributeValue(0);
                }
                while (parser.getEventType() != XmlResourceParser.TEXT) {
                    parser.next();
                }
                String city = parser.getText();
                result.add(new TimeZoneInfo(timeZoneId, city));
                while (parser.getEventType() != XmlResourceParser.END_TAG) {
                    parser.next();
                }
                parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new IllegalStateException("Can't parse timezones.xml", e);
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
        return result;
    }
}
