package com.voqse.nixieclock;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;

import com.voqse.nixieclock.widget.LaunchConfigurationActivity;

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

    private static final DateFormat TIME_FORMAT_24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final DateFormat TIME_FORMAT_12 = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private static final DateFormat DATE_FORMAT_MONTH_FIRST = new SimpleDateFormat("MM.dd.yyyy", Locale.getDefault());
    private static final DateFormat DATE_FORMAT_DAY_FIRST = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static String getCurrentTime(boolean format24, String timeZoneId) {
        DateFormat dateFormat = format24 ? TIME_FORMAT_24 : TIME_FORMAT_12;
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        dateFormat.setTimeZone(timeZone);
        Date now = new Date();
        return dateFormat.format(now);
    }

    public static String getCurrentDate(boolean monthFirst, String timeZoneId) {
        DateFormat dateFormat = monthFirst ? DATE_FORMAT_MONTH_FIRST : DATE_FORMAT_DAY_FIRST;
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        dateFormat.setTimeZone(timeZone);
        Date now = new Date();
        return dateFormat.format(now);
    }

    // http://stackoverflow.com/a/8135377/999458 . Note that the icon may not be gone until the next reboot.
    public static void setLauncherIconVisibility(Context context, boolean show) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName launcherComponent = new ComponentName(context, LaunchConfigurationActivity.class);
        int showLauncherIcon = show ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        packageManager.setComponentEnabledSetting(launcherComponent, showLauncherIcon, PackageManager.DONT_KILL_APP);
    }

    public static String getAppPublicKey(Context context) {
        // The key itself is not secret information, but we don't want to make it easy for an attacker
        // to replace the public key with one  of their own and then fake messages from the server.
        String based64XoredKey = context.getString(R.string.public_key);
        byte[] xoredKeyBytes = Base64.decode(based64XoredKey, 0);
        return xor(new String(xoredKeyBytes), "&1M*h^j03n619nbjs");
    }

    public static String xor(String input, String secret) {
        byte[] inputBytes = input.getBytes();
        byte[] secretBytes = secret.getBytes();
        byte[] outputBytes = new byte[inputBytes.length];
        int secretIndex = 0;
        for (int inputIndex = 0; inputIndex < inputBytes.length; ++inputIndex) {
            outputBytes[inputIndex] = (byte) (inputBytes[inputIndex] ^ secretBytes[secretIndex]);
            ++secretIndex;
            if (secretIndex >= secretBytes.length) {
                secretIndex = 0;
            }
        }
        return new String(outputBytes);
    }

}
