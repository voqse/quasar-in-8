package com.voqse.nixieclock.iab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.danikula.iab.IabBroadcastReceiver;
import com.danikula.iab.IabBroadcastReceiver.IabBroadcastListener;
import com.danikula.iab.IabException;
import com.danikula.iab.IabHelper;
import com.danikula.iab.IabResult;
import com.danikula.iab.Inventory;
import com.danikula.iab.Purchase;
import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.log.NonFatalError;
import com.voqse.nixieclock.utils.NixieUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class GooglePlayInAppBilling implements InAppBilling {

    private static final Logger LOG = LoggerFactory.getLogger("GooglePlayInAppBilling");
    private static final String SKU_PRO_UPDATE = "pro.update";

    private final Context context;
    private IabHelper iabHelper;
    private InAppBillingListener listener;
    private BroadcastReceiver iabBroadcastReceiver;
    private boolean hasPro;

    public GooglePlayInAppBilling(@NonNull Context context, @NonNull InAppBillingListener listener) {
        this.context = context.getApplicationContext();
        this.iabHelper = new IabHelper(this.context, NixieUtils.getAppPublicKey(context));
        this.listener = listener;
        this.iabHelper.enableDebugLogging(BuildConfig.DEBUG);
        this.iabHelper.startSetup(new SetupFinishedListener());
    }

    @Override
    public void release() {
        if (!isReleased()) {
            if (iabBroadcastReceiver != null) {
                context.unregisterReceiver(iabBroadcastReceiver);
            }
            iabHelper.disposeWhenFinished();
            iabHelper = null;
            listener = null;
        }
    }

    @Override
    public void purchase(Activity activity, int requestCode) {
        try {
            String payload = UUID.randomUUID().toString();
            iabHelper.launchPurchaseFlow(activity, SKU_PRO_UPDATE, IabHelper.ITEM_TYPE_INAPP, null, requestCode, new PurchasingListener(payload), payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            LOG.error("Error launching purchase flow. Another async operation in progress.", e);
        }
    }

    @Override
    public void processPurchase(int requestCode, int resultCode, Intent data) {
        if (!isReleased()) {
            iabHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean hasPro() {
        return hasPro;
    }

    private boolean isReleased() {
        return iabHelper == null;
    }

    private void onFailureResult(IabResult result, String errorMessage) {
        boolean canceledByUser = result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED;
        Exception exceptionToLog = canceledByUser ? new PurchaseCanceledException(errorMessage) : new IabException(result);
        LOG.error(errorMessage, exceptionToLog);
        if (!canceledByUser) {
            listener.onPurchasingError();
        }
    }

    private void queryInventoryAsync() {
        if (isReleased()) {
            LOG.info("Skip querying inventory: iab has already been released");
            return;
        }

        try {
            iabHelper.queryInventoryAsync(new InventoryFetchListener());
        } catch (IabHelper.IabAsyncInProgressException e) {
            String message = "Error querying inventory. Another async operation in progress.";
            LOG.error(message, new NonFatalError(message));
        }
    }

    private void onIabSetupFinished(IabResult result) {
        LOG.info("In app billing setup finished. Result: {}", result);

        if (result.isFailure()) {
            onFailureResult(result, "Error setting up in-app billing: " + result);
            return;
        }

        if (!isReleased()) {
            this.iabBroadcastReceiver = new IabBroadcastReceiver(new PurchaseUpdatedListener());
            context.registerReceiver(iabBroadcastReceiver, new IntentFilter(IabBroadcastReceiver.ACTION));

            queryInventoryAsync();
        }
    }

    private void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        LOG.info("In app inventory is fetched. Result: {}, inventory: {}", result, inventory);

        if (result.isFailure()) {
            onFailureResult(result, "Failed to query inventory: " + result);
            return;
        }

        hasPro = inventory.hasPurchase(SKU_PRO_UPDATE);
        LOG.info("Is PRO user?: {}", hasPro);

        listener.onProductsFetched(hasPro);

        // uncomment to delete test purchase
        // cancelTestPurchase(inventory.getPurchase(SKU_PRO_UPDATE));
    }

    private void onIabPurchaseFinished(IabResult result, Purchase purchase, String payload) {
        LOG.info("In app purchase is finished. Result: {}, purchase: {}", result, purchase);

        if (result.isFailure()) {
            onFailureResult(result, "Failed to start purchasing: " + result);
            return;
        }

        boolean payloadsEquals = TextUtils.equals(payload, purchase.getDeveloperPayload());
        if (!payloadsEquals) {
            String error = String.format("Error purchasing. Authenticity verification failed. Original: %s, actual: %s", payload, purchase.getDeveloperPayload());
            onFailureResult(result, error);
            return;
        }

        hasPro = true;
        listener.onPurchased();
    }

    private void cancelTestPurchase(Purchase purchase) {
        try {
            iabHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {

                @Override
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    LOG.info("Purchase canceled with result " + result);
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            LOG.error("Error canceling purchase", e);
        }
    }

    private class InventoryFetchListener implements IabHelper.QueryInventoryFinishedListener {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            GooglePlayInAppBilling.this.onQueryInventoryFinished(result, inventory);
        }

    }

    private class SetupFinishedListener implements IabHelper.OnIabSetupFinishedListener {

        @Override
        public void onIabSetupFinished(IabResult result) {
            GooglePlayInAppBilling.this.onIabSetupFinished(result);
        }

    }

    private class PurchaseUpdatedListener implements IabBroadcastListener {

        @Override
        public void receivedBroadcast() {
            queryInventoryAsync();
        }

    }

    private class PurchasingListener implements IabHelper.OnIabPurchaseFinishedListener {

        private final String payload;

        private PurchasingListener(String payload) {
            this.payload = payload;
        }

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            GooglePlayInAppBilling.this.onIabPurchaseFinished(result, purchase, payload);
        }
    }
}
