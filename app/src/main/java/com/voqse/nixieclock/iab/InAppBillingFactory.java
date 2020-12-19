package com.voqse.nixieclock.iab;

import android.content.Context;
import androidx.annotation.NonNull;

import com.voqse.nixieclock.BuildConfig;

/**
 * Фабрика для создания либо тестовой, либо реальной реализации {@link InAppBilling}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class InAppBillingFactory {

    public static InAppBilling newInnAppBilling(@NonNull Context context, @NonNull InAppBillingListener listener) {
        return BuildConfig.DEBUG ?
                new DummyInAppBilling(false, listener) :
                new GooglePlayInAppBilling(context, listener);
    }
}
