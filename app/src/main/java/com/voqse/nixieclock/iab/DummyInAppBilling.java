package com.voqse.nixieclock.iab;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class DummyInAppBilling implements InAppBilling {

    private InAppBillingListener listener;

    public DummyInAppBilling(boolean pro, @NonNull InAppBillingListener listener) {
        this.listener = listener;
        this.listener.onProductsFetched(pro);
    }

    @Override
    public void release() {
        this.listener = null;
    }

    @Override
    public void purchase(Activity activity, int requestCode) {
        this.listener.onPurchased();
    }

    @Override
    public void processPurchase(int requestCode, int resultCode, Intent data) {

    }
}
