package com.voqse.nixieclock.iab;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface InAppBillingListener {

    void onPurchasingError();

    void onProductsFetched(boolean hasPro);

    void onPurchased();

}
