package com.voqse.nixieclock.clock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;

import com.voqse.nixieclock.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ExternalApp {

    public static final String DELIMETER = " | ";
    public static final ExternalApp DEFAULT_APP = new ExternalApp();
    private static final Logger LOG = LoggerFactory.getLogger("ExternalApp");

    private final String packageName;
    private final String activityClassName;
    private final String label;

    private ExternalApp() {
        this(null, null, null);
    }

    public ExternalApp(ResolveInfo resolveInfo, PackageManager packageManager) {
        this.label = resolveInfo.loadLabel(packageManager).toString();
        this.activityClassName = resolveInfo.activityInfo.name;
        this.packageName = resolveInfo.activityInfo.applicationInfo.packageName;
    }

    public ExternalApp(String packageName, String activityClassName, String label) {
        this.packageName = packageName;
        this.activityClassName = activityClassName;
        this.label = label;
    }

    public static ExternalApp fromString(String packedInfo) {
        if (packedInfo == null) {
            return DEFAULT_APP;
        }

        String tokens[] = packedInfo.split(DELIMETER);
        return new ExternalApp(tokens[0], tokens[2], tokens[4]);
    }

    public void launch(Context context) throws NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = isDefault() ?
                getDefaultClockIntent(packageManager) :
                newLaunchIntent(packageManager, this.packageName, this.activityClassName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String getName(Context context) {
        return isDefault() ? context.getString(R.string.default_app_name) : label;
    }

    public String pack() {
        return isDefault() ? null :
                new StringBuilder(packageName)
                        .append(DELIMETER)
                        .append(activityClassName)
                        .append(DELIMETER)
                        .append(label)
                        .toString();
    }

    private boolean isDefault() {
        return this == DEFAULT_APP;
    }

    private Intent getDefaultClockIntent(PackageManager packageManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        }

        Intent clockDetectIntent = tryToDetectClockApp(packageManager);
        return clockDetectIntent != null ? clockDetectIntent : new Intent(AlarmClock.ACTION_SET_ALARM);
    }

    @Nullable
    private Intent tryToDetectClockApp(PackageManager packageManager) {
        for (ClockApp clockApp : ClockApp.values()) {
            try {
                Intent intent = newLaunchIntent(packageManager, clockApp.packageName, clockApp.className);
                LOG.info("Clock app detected: {}", clockApp);
                return intent;
            } catch (PackageManager.NameNotFoundException e) {
                // do not log exception to prevent log flood
                LOG.error(clockApp + " not found");
            }
        }
        return null;
    }

    private Intent newLaunchIntent(PackageManager packageManager, String packageName, String activityClassName) throws NameNotFoundException {
        ComponentName componentName = new ComponentName(packageName, activityClassName);
        packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA);
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(componentName);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExternalApp{");
        sb.append("packageName='").append(packageName).append('\'');
        sb.append(", activityClassName='").append(activityClassName).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
