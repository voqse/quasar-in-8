package com.voqse.nixieclock.clock;

/**
 * http://stackoverflow.com/a/23016012/999458
 * http://stackoverflow.com/questions/4115649/listing-of-manufacturers-clock-alarm-package-and-class-name-please-add
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public enum ClockApp {

    STANDART_ALARM("com.android.alarmclock", "com.android.alarmclock.AlarmClock"),
    STANDART_ALARM_CLOCK_DT("com.android.deskclock", "com.android.deskclock.AlarmClock"),
    NEXUS_ALARM_CLOCK_DT_0("com.android.deskclock", "com.android.deskclock.DeskClock"),
    NEXUS_ALARM_CLOCK_DT_1("com.google.android.deskclock", "com.android.deskclock.DeskClock"),
    SAMSUNG_GALAXY_S("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"),
    SAMSUNG_GALAXY_TAB("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.AlarmActivity"),
    SAMSUNG_GALAXY_WORLD_CLOCK("com.sec.android.app.worldclock", "com.sec.android.app.worldclock.activity.HomeMainActivity"),
    LG_NEW("com.lge.clock", "com.lge.clock.AlarmClockActivity"),
    LG_OLD("com.lge.clock", "com.lge.clock.DefaultAlarmClockActivity"),
    HTC_ALARM_CLOCK_DT("com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl"),
    ASUS_ALARM_CLOCK("com.asus.alarmclock", "com.asus.alarmclock.AlarmClock"),
    ASUS_DESK_CLOCK("com.asus.deskclock", "com.asus.deskclock.DeskClock"),
    SONY_ALARM("com.sonyericsson.alarm", "com.sonyericsson.alarm.Alarm"),
    SONY_ERICSSON_XPERIA_Z("com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock"),
    MOTO_BLUR_ALARM_CLOCK_DT("com.motorola.blur.alarmclock", "com.motorola.blur.alarmclock.AlarmClock");

    public final String packageName;
    public final String className;

    ClockApp(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    @Override
    public String toString() {
        return new StringBuilder("ClockApp ")
                .append(name()).append(" {")
                .append("packageName='").append(packageName).append('\'')
                .append(", className='").append(className).append('\'')
                .append('}').toString();
    }
}
