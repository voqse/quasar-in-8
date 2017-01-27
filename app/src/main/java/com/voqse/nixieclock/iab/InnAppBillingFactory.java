package com.voqse.nixieclock.iab;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class InnAppBillingFactory {

    public static InnAppBilling newInnAppBilling(@NonNull Context context, @NonNull InAppBillingListener listener) {
//        return new DummyInAppBilling(false, listener);
        return new GooglePlayInAppBilling(context, listener);
    }
}
