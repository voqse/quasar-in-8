package com.voqse.nixieclock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DateTest {

    @Test
    public void testDate24And12Formats() throws Exception {
        String format12 = Utils.getCurrentTime(false, "America/Barbados");
        String format24 = Utils.getCurrentTime(true, "America/Barbados");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("America/Barbados"));
//
        System.out.println(format12);
        System.out.println(format24);
    }

    private Date newDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("HH:mm").parse(dateStr);
    }

}
