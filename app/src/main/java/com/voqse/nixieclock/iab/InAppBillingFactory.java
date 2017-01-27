package com.voqse.nixieclock.iab;

import android.content.Context;
import android.support.annotation.NonNull;

import com.voqse.nixieclock.BuildConfig;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class InAppBillingFactory {

    public static InAppBilling newInnAppBilling(@NonNull Context context, @NonNull InAppBillingListener listener) {
        return BuildConfig.DEBUG ?
                new DummyInAppBilling(false, listener) :
                new GooglePlayInAppBilling(context, listener);
    }
}
