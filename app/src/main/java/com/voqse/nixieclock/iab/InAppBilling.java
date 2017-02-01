package com.voqse.nixieclock.iab;

import android.app.Activity;
import android.content.Intent;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface InAppBilling {

    void release();

    void purchase(Activity activity, int requestCode);

    void processPurchase(int requestCode, int resultCode, Intent data);

    boolean hasPro();

}
