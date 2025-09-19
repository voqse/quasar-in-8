package com.voqse.nixieclock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class DateTest {

    @Test
    public void testDate24And12Formats() throws Exception {
        Date date = newDate("08:10");
        System.out.println(format(date, true, "Europe/Athens"));
        System.out.println(format(date, false, "Europe/Athens"));

        date = newDate("18:10");
        System.out.println(format(date, true, "Europe/Athens"));
        System.out.println(format(date, false, "Europe/Athens"));

        date = newDate("08:10");
        System.out.println(format(date, true, "Australia/Brisbane"));
        System.out.println(format(date, false, "Australia/Brisbane"));
    }

    private String format(Date date, boolean format24, String timeZoneId) {
        String format = format24 ? "HH:mm" : "hh:mm";
        DateFormat dateFormat = new SimpleDateFormat(format);
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    private Date newDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("HH:mm").parse(dateStr);
    }

}
