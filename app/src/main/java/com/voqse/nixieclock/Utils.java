package com.voqse.nixieclock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <b>Not thread safe implementation!<b/>
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class Utils {

    private static final DateFormat DATE_FORMAT_24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final DateFormat DATE_FORMAT_12 = new SimpleDateFormat("hh:mm", Locale.getDefault());

    public static String getCurrentTime(boolean format24, String timeZoneId) {
        DateFormat dateFormat = format24 ? DATE_FORMAT_24 : DATE_FORMAT_12;
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        dateFormat.setTimeZone(timeZone);
        Date now = new Date();
        return dateFormat.format(now);
    }
}
