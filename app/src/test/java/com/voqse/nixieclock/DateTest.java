package com.voqse.nixieclock;

import android.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DateTest {

    @Test
    public void testDate24And12Formats() throws Exception {
        String xor = Utils.xor("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvmcZAAbSrSPw+0HpFPfUSjG2N8k55k2TakLf6lxTNF3I40OXavR9K6Rm+GosYEqa0olqlWuD1q8A9r2Gggz0g1BK5qN1gJIFqEMyZE1JcbY8VfrETPdEC9wKuI8TVME/N9mXmSP6ckSVMpKW+Xsf14qEWOASvT1tzRoPGOkv0uWkNR5JjUiv6m1/KMCLtGi7lrpOUN+tLtdIeEy1Vcwfb3YJtDjJBgSEOkH6KPuC7jMGoHQXO+lhEMfRZnu+XG1XJHQX0WlXeW73/tmev0kakcXfibk7ZxCH1kKUYHqlercF/DeF9KBqJ9YuzW8/Mzqii1ih4S0zGT8EDkY9PJZtjQIDAQAB", "&1M*h^j03n619nbjs");
        byte[] encode = Base64.encode(xor.getBytes(), 0);
        System.out.print(new String(encode));
    }

    private Date newDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("HH:mm").parse(dateStr);
    }

}
