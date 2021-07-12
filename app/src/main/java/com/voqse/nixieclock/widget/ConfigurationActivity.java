package com.voqse.nixieclock.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.clock.ExternalApp;
import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.timezone.TimeZoneInfo;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment.OnTimeZoneSelectedListener;
import com.voqse.nixieclock.timezone.TimeZones;
import com.voqse.nixieclock.utils.NixieUtils;
import com.voqse.nixieclock.widget.support.AboutDialogFragment;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment.OnAppSelectedListener;
import com.voqse.nixieclock.widget.support.ApplySettingsDialogFragment;
import com.voqse.nixieclock.widget.support.ApplySettingsDialogFragment.OnApplySettingsDialogClickListener;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment.OnDateFormatSelectedListener;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment.OnThemeSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_CONFIGURE;
import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static com.voqse.nixieclock.utils.NixieUtils.formatTwoLineText;

/**
 * Конфигурация виджета. Может запускаться как системой при добавлении виджета, так и пользователем для изменения настроек виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ConfigurationActivity extends AppCompatActivity implements OnCheckedChangeListener, OnClickListener,
        OnTimeZoneSelectedListener, OnDateFormatSelectedListener, OnThemeSelectedListener, OnAppSelectedListener,
        OnApplySettingsDialogClickListener, WidgetsAdapter.WidgetOptionsProvider, PurchasesUpdatedListener, BillingClientStateListener {

    private static final String TAG = "ConfigurationActivity";
    private static final String STATE_CONFIGURING_WIDGET_ID = BuildConfig.APPLICATION_ID + ".STATE_CONFIGURING_WIDGET_ID";

    private WidgetsAdapter widgetsAdapter;
    private ViewPager widgetsViewPager;
    private CompoundButton systemSettingsSwitch;
    private CompoundButton timeFormatSwitch;
    private TextView timeZoneTextView;
    private TextView dateFormatTextView;
    private TextView themeTextView;
    private TextView appTextView;
    private CompoundButton hideIconSwitch;
    private Button applyWidgetButton;
    private Button upgradeButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private View noWidgetsView;
    private Toolbar toolbar;
    private Settings settings;
    private SparseArray<WidgetOptions> widgetsOptions;
    private int configuringWidgetId;

    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private static final String SKU_PRO_UPDATE = "unlock_all";
    private boolean hasPro = false;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        // Billing client start
        mBillingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();
        // Billing client end

        this.settings = new Settings(this);
        restoreConfiguringWidgetId(state);

        setContentView(R.layout.activity_configuration);
        findViews();
        List<Integer> widgetIds = getWidgetIds();
        this.widgetsOptions = getWidgetOptions(widgetIds);
        setupViews(widgetIds);
        setupUi(widgetIds);

        if (isNewlyCreatedWidget()) {
            setActivityResult(false);
        }
    }

    private void connectToBillingService() {
        if (!mBillingClient.isReady()) {
            mBillingClient.startConnection(this);
        }
    }

    private void disconnectToBillingService() {
        if (mBillingClient.isReady()) {
            mBillingClient.endConnection();
        }
    }

    // Получаем список совершенных покупок
    private void queryPurchases() {
        mBillingClient.queryPurchasesAsync(SkuType.INAPP, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        Log.d(TAG, "onQueryPurchasesResponse: There's purchase, handle it");
                        handlePurchase(purchase);
                    }

                    // Update buttons after fetching products
                    onProductsFetched();
                }
            }
        });
    }

    private void querySkuDetails() {
        List<String> skuList = new ArrayList<> ();
        skuList.add(SKU_PRO_UPDATE);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SkuType.INAPP);

        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                            }
                        }
                    }
                });
    }

    // Обрабатываем успешную покупку
    private void handlePurchase(Purchase purchase) {

        Log.d("DEBUG", "handlePurchase: Purchase state: " + purchase.getPurchaseState());
        if (purchase.getPurchaseState() == PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();

                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                            onPurchased();
                        }
                    }
                });
            } else {
                onPurchased();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        queryPurchases();

//        findViews();
        List<Integer> widgetIds = getWidgetIds();
        this.widgetsOptions = getWidgetOptions(widgetIds);
        setupViews(widgetIds);
        setupUi(widgetIds);
    }

    private void restoreConfiguringWidgetId(Bundle activityState) {
        if (activityState == null) {
            if (ACTION_APPWIDGET_CONFIGURE.equals(getIntent().getAction())) {
                configuringWidgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
            }
        } else {
            activityState.getInt(STATE_CONFIGURING_WIDGET_ID);
        }
    }

    private void findViews() {
        this.widgetsViewPager = (ViewPager) findViewById(R.id.widgetsViewPager);
        this.timeFormatSwitch = (CompoundButton) findViewById(R.id.timeFormatSwitch);
        this.systemSettingsSwitch = (CompoundButton) findViewById(R.id.systemSettingsSwitch);
        this.timeZoneTextView = (TextView) findViewById(R.id.timeZoneTextView);
        this.dateFormatTextView = (TextView) findViewById(R.id.dateFormatTextView);
        this.themeTextView = (TextView) findViewById(R.id.themeTextView);
        this.appTextView = (TextView) findViewById(R.id.appTextView);
        this.noWidgetsView = findViewById(R.id.noWidgetsView);
        this.hideIconSwitch = (CompoundButton) findViewById(R.id.hideIconSwitch);
        this.applyWidgetButton = (Button) findViewById(R.id.applyWidgetButton);
        this.upgradeButton = (Button) findViewById(R.id.upgradeButton);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.leftButton = (ImageButton) findViewById(R.id.leftButton);
        this.rightButton = (ImageButton) findViewById(R.id.rightButton);
    }

    private void setupViews(List<Integer> widgetIds) {
        setPreviewHeight();
        setSupportActionBar(toolbar);
        this.widgetsAdapter = new WidgetsAdapter(widgetIds, this, this);
        widgetsViewPager.setAdapter(widgetsAdapter);
        timeFormatSwitch.setOnCheckedChangeListener(this);
        hideIconSwitch.setOnCheckedChangeListener(this);
        systemSettingsSwitch.setOnCheckedChangeListener(this);
        widgetsViewPager.addOnPageChangeListener(new WidgetSettingBinder());
        timeZoneTextView.setOnClickListener(this);
        dateFormatTextView.setOnClickListener(this);
        themeTextView.setOnClickListener(this);
        applyWidgetButton.setOnClickListener(this);
        upgradeButton.setOnClickListener(this);
        appTextView.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        bindHideIcon(settings.isHideIcon());
        bindUseSystemSettings(settings.isUseSystemPreferences());
    }

    private void setPreviewHeight() {
        View previewContainer = findViewById(R.id.previewContainer);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int previewHeight = (int) (9f / 16f * displayMetrics.widthPixels);
        previewContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, previewHeight));
    }

    private void setupUi(List<Integer> widgetIds) {
        int arrowVisibility = isNewlyCreatedWidget() ? View.GONE : View.VISIBLE;
        leftButton.setVisibility(arrowVisibility);
        rightButton.setVisibility(arrowVisibility);
        updateArrowButtons(0);
        boolean hasWidgets = !widgetIds.isEmpty();
        noWidgetsView.setVisibility(hasWidgets ? View.GONE : View.VISIBLE);
        applyWidgetButton.setVisibility(hasWidgets ? View.VISIBLE : View.GONE);
        WidgetOptions currentWidgetOptions = hasWidgets ? getCurrentWidgetOptions() : WidgetOptions.getDefault(this);
        bindWidgetSettings(currentWidgetOptions);
        if (!hasWidgets) {
            setEnabled(false, systemSettingsSwitch, timeFormatSwitch, timeZoneTextView, dateFormatTextView, themeTextView, appTextView, hideIconSwitch);
        }
    }

    private SparseArray<WidgetOptions> getWidgetOptions(List<Integer> widgetIds) {
        SparseArray<WidgetOptions> widgetOptionsArrays = new SparseArray<>(widgetIds.size());
        for (int widgetId : widgetIds) {
            widgetOptionsArrays.put(widgetId, settings.getWidgetOptions(widgetId));
        }
        return widgetOptionsArrays;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CONFIGURING_WIDGET_ID, configuringWidgetId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AboutDialogFragment().show(getSupportFragmentManager(), "AboutDialog");
        return true;
    }

    private List<Integer> getWidgetIds() {
        if (isNewlyCreatedWidget()) {
            return Arrays.asList(configuringWidgetId);
        }

        Set<Integer> liveWidgets = settings.getLiveWidgets();
        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        List<Integer> result = new ArrayList<>();
        for (int appWidgetId : appWidgetIds) {  // filter phantom widgets
            if (liveWidgets.contains(appWidgetId)) {
                result.add(appWidgetId);
            }
        }

        return result;
    }

    private void bindWidgetSettings(WidgetOptions widgetOptions) {
        bindTimeFormat(widgetOptions.format24);
        bindTimeZone(widgetOptions.timeZoneId);
        bindDateFormat(widgetOptions.monthFirst);
        bindAppToLaunch(widgetOptions.appToLaunch);
        bindTheme(widgetOptions.theme);
    }

    private void bindTimeFormat(boolean format24) {
        String label = getString(R.string.format_24);
        String time = getString(format24 ? R.string.format_24_yes : R.string.format_24_no);
        timeFormatSwitch.setChecked(format24);
        timeFormatSwitch.setText(formatTwoLineText(label, time));
    }

    private void bindTimeZone(String timeZoneId) {
        String label = getString(R.string.timezone);
        TimeZoneInfo timeZoneInfo = TimeZones.getTimeZoneInfo(this, timeZoneId);
        String timeZone = timeZoneInfo.getPrettyOffset() + " " + timeZoneInfo.city;
        timeZoneTextView.setText(formatTwoLineText(label, timeZone));
    }

    private void bindDateFormat(boolean monthFirst) {
        String label = getString(R.string.date_format);
        String date = NixieUtils.getNewYerDate(monthFirst);
        dateFormatTextView.setText(formatTwoLineText(label, date));
    }

    private void bindTheme(Theme theme) {
        String label = getString(R.string.theme);
        themeTextView.setText(formatTwoLineText(label, getString(theme.nameId)));
    }

    private void bindAppToLaunch(ExternalApp app) {
        String label = getString(R.string.app_to_launch);
        String appName = app.getName(this);
        appTextView.setText(formatTwoLineText(label, appName));
    }

    private void bindHideIcon(boolean hideIcon) {
        String label = getString(R.string.hide_icon);
        String explanation = getString(hideIcon ? R.string.icon_hidden : R.string.icon_shown);
        hideIconSwitch.setChecked(hideIcon);
        hideIconSwitch.setText(formatTwoLineText(label, explanation));
    }

    private void bindUseSystemSettings(boolean useSystemPreferences) {
        systemSettingsSwitch.setChecked(useSystemPreferences);
        setEnabled(!useSystemPreferences, dateFormatTextView, timeFormatSwitch, timeZoneTextView);
        if (useSystemPreferences) {
            WidgetOptions defaultOptions = WidgetOptions.getDefault(this);
            bindTimeFormat(defaultOptions.format24);
            bindTimeZone(defaultOptions.timeZoneId);
            bindDateFormat(defaultOptions.monthFirst);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.timeZoneTextView) {
            new TimeZonePickerDialogFragment().show(getSupportFragmentManager(), "TimeZonePicker");
        } else if (id == R.id.dateFormatTextView) {
            new DateFormatDialogFragment().show(getSupportFragmentManager(), "DateFormatPicker");
        } else if (id == R.id.themeTextView) {
            new ThemePickerDialogFragment().show(getSupportFragmentManager(), "ThemePicker");
        } else if (id == R.id.appTextView) {
            new AppPickerDialogFragment().show(getSupportFragmentManager(), "AppPicker");
        } else if (id == R.id.applyWidgetButton) {
            applySettings();
        } else if (id == R.id.upgradeButton) {
            upgradeToPro();
        } else if (id == R.id.leftButton) {
            swipeSlider(true);
        } else if (id == R.id.rightButton) {
            swipeSlider(false);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        int id = button.getId();
        if (id == R.id.timeFormatSwitch) {
            onTimeFormatChanged(checked);
        } else if (id == R.id.hideIconSwitch) {
            onHideIconValueChanged(checked);
        } else if (id == R.id.systemSettingsSwitch) {
            onUseSystemPreferencesValueChanged(checked);
        } else {
            throw new IllegalStateException();
        }
    }

    private void onTimeFormatChanged(boolean format24) {
        if (widgetsAdapter.getCount() > 0) {
            WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeFormat24(format24);
            changeCurrentWidgetOptions(newWidgetOptions);
            bindTimeFormat(format24);
            updatePreviewAndButton();
        }
//        WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeFormat24(format24);
//        changeCurrentWidgetOptions(newWidgetOptions);
//        bindTimeFormat(format24);
//        updatePreviewAndButton();
    }

    @Override
    public void onTimeZoneSelected(TimeZoneInfo timeZoneInfo) {
        WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeTimeZoneId(timeZoneInfo.id);
        changeCurrentWidgetOptions(newWidgetOptions);
        bindTimeZone(timeZoneInfo.id);
        updatePreviewAndButton();
    }

    @Override
    public void onDateFormatSelected(boolean monthFirst) {
        WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeMonthFirst(monthFirst);
        changeCurrentWidgetOptions(newWidgetOptions);
        bindDateFormat(monthFirst);
        updatePreviewAndButton();
    }

    @Override
    public void onAppSelected(ExternalApp app) {
        WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeAppToLaunch(app);
        changeCurrentWidgetOptions(newWidgetOptions);
        bindAppToLaunch(app);
        updateButtons();
    }

    @Override
    public void onThemeSelected(Theme theme) {
        WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeTheme(theme);
        changeCurrentWidgetOptions(newWidgetOptions);
        bindTheme(theme);
        updatePreviewAndButton();
    }

    private void onHideIconValueChanged(boolean checked) {
        updateButtons();
        bindHideIcon(checked);
    }

    private void onUseSystemPreferencesValueChanged(boolean useSystemPreferences) {
        WidgetOptions defaultOptions = WidgetOptions.getDefault(this);
        WidgetOptions currentOptions = getCurrentWidgetOptions();

        WidgetOptions newWidgetOptions = new WidgetOptions(defaultOptions.format24, defaultOptions.timeZoneId, defaultOptions.monthFirst, currentOptions.appToLaunch, currentOptions.theme, useSystemPreferences);
        changeCurrentWidgetOptions(newWidgetOptions);

        bindUseSystemSettings(useSystemPreferences);
        updatePreviewAndButton();
    }

    private int getCurrentWidget() {
        return widgetsViewPager.getCurrentItem();
    }

    private int getCurrentWidgetId() {
        int currentWidget = widgetsViewPager.getCurrentItem();
        return widgetsAdapter.getWidgetId(currentWidget);
    }

    private WidgetOptions getCurrentWidgetOptions() {
        int widgetId = getCurrentWidgetId();
        return widgetsOptions.get(widgetId);
    }

    private void changeCurrentWidgetOptions(WidgetOptions newWidgetOptions) {
        widgetsOptions.put(getCurrentWidgetId(), newWidgetOptions);
    }

    private void updatePreviewAndButton() {
        widgetsAdapter.refresh(widgetsViewPager, getCurrentWidget());
        updateButtons();
    }

    private void updateButtons() {
        boolean newWidget = isNewlyCreatedWidget();
        boolean settingsChanged = isCurrentWidgetSettingsChanged();

        boolean applyButtonActive = newWidget && (hasPro || !settingsChanged) || (!newWidget && hasPro && settingsChanged);
        boolean applyButtonDisabled = !newWidget && !settingsChanged;
        int applyButtonTextColorId = applyButtonActive || settingsChanged ? android.R.color.white : R.color.text_white_disabled;

        applyWidgetButton.setBackgroundResource(applyButtonActive ? R.drawable.btn_blue : R.drawable.btn_dark);
        applyWidgetButton.setEnabled(!applyButtonDisabled);
        applyWidgetButton.setTextColor(getResources().getColor(applyButtonTextColorId));
    }

    private void updateArrowButtons(int position) {
        boolean leftActive = position > 0;
        boolean rightActive = position < widgetsAdapter.getCount() - 1;
        leftButton.setEnabled(leftActive);
        rightButton.setEnabled(rightActive);
        leftButton.setAlpha(leftActive ? 1f : .3f);
        rightButton.setAlpha(rightActive ? 1f : .3f);
    }

    private boolean isCurrentWidgetSettingsChanged() {
        if (widgetsAdapter.getCount() == 0) {
            return false;
        }
        int currentWidgetId = getCurrentWidgetId();
        WidgetOptions currentWidgetOptions = getCurrentWidgetOptions();
        return hideIconSwitch.isChecked() != settings.isHideIcon() ||
//                systemSettingsSwitch.isChecked() != settings.isUseSystemPreferences() ||
                !settings.getWidgetOptions(currentWidgetId).equals(currentWidgetOptions);
    }

    private void applySettings() {
        if ((mBillingClient == null || !hasPro) && isCurrentWidgetSettingsChanged()) {
            new ApplySettingsDialogFragment().show(getSupportFragmentManager(), "ApplySettingsConfirm");
        } else {
            boolean hideIcon = hideIconSwitch.isChecked();
            if (settings.isHideIcon() != hideIcon) {
                NixieUtils.setLauncherIconVisibility(this, !hideIcon);
                settings.setHideIcon(hideIcon);
//                settings.setUseSystemPreferences(systemSettingsSwitch.isChecked());
            }

            WidgetOptions currentWidgetOptions = getCurrentWidgetOptions();
            int widgetId = getCurrentWidgetId();
            settings.setWidgetOptions(widgetId, currentWidgetOptions);

            new WidgetUpdater(this).updateImmediately();
            updatePreviewAndButton();

            if (isNewlyCreatedWidget()) {
                setActivityResult(true);
                finish();
            }
        }
    }

    private void setActivityResult(boolean success) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, configuringWidgetId);
        int result = success ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
        setResult(result, resultValue);
    }

    private void upgradeToPro() {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(SKU_PRO_UPDATE))
                .build();

        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void setEnabled(boolean enabled, View... views) {
        for (View view : views) {
            view.setEnabled(enabled);
            view.setAlpha(enabled ? 1 : 0.5f);
        }
    }

    private boolean isNewlyCreatedWidget() {
        return configuringWidgetId > 0;
    }

    public void onPurchasingError() {
        Toast.makeText(this, R.string.purchase_error, Toast.LENGTH_LONG).show();
    }

    public void onProductsFetched() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: SKU fetched, update UI");
                upgradeButton.setVisibility(hasPro ? View.GONE : View.VISIBLE);
            }
        });
    }

    public void onPurchased() {
        hasPro = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: SKU purchased, update UI");
                upgradeButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectToBillingService();
    }

    @Override
    public void onApplySettingsClick(boolean buyClicked, boolean noClicked, boolean yesClicked) {
        if (buyClicked) {
            upgradeToPro();
        } else if (yesClicked) {
            WidgetOptions defaultOptions = WidgetOptions.getDefault(this);
            changeCurrentWidgetOptions(defaultOptions);
            bindWidgetSettings(defaultOptions);
            hideIconSwitch.setChecked(false);
            systemSettingsSwitch.setChecked(true);
            updatePreviewAndButton();
        }
    }

    private void swipeSlider(boolean left) {
        int currentWidgetIndex = widgetsViewPager.getCurrentItem();
        int newWidgetIndex = left ? --currentWidgetIndex : ++currentWidgetIndex;
        widgetsViewPager.setCurrentItem(newWidgetIndex);
    }


    @Override
    public WidgetOptions provideWidgetOptions(int widgetId) {
        return widgetsOptions.get(widgetId);
    }

    // Billing service start
    @Override
    public void onBillingServiceDisconnected() {
        connectToBillingService();
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() ==  BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            querySkuDetails();
            // Проверяем уже совершенные покупки
            queryPurchases();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                // Обрабатываем покупку
                Log.d(TAG, "onPurchasesUpdated: Handle purchasing item");
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Обновляем список уже совершенных покупок
            Log.d(TAG, "onPurchasesUpdated: Item already owned");
            queryPurchases();
            // Handle an error caused by a user cancelling the purchase flow.
        } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            // Handle any other error codes.
            onPurchasingError();
        }
    }
    // Billing service end

    private class WidgetSettingBinder implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            WidgetOptions widgetOptions = getCurrentWidgetOptions();
            bindWidgetSettings(widgetOptions);
            updateArrowButtons(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
