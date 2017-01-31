package com.voqse.nixieclock.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.clock.ExternalApp;
import com.voqse.nixieclock.iab.InAppBilling;
import com.voqse.nixieclock.iab.InAppBillingFactory;
import com.voqse.nixieclock.iab.InAppBillingListener;
import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.timezone.TimeZoneInfo;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment.OnTimeZoneSelectedListener;
import com.voqse.nixieclock.timezone.TimeZones;
import com.voqse.nixieclock.utils.NixieUtils;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment.OnAppSelectedListener;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment.OnDateFormatSelectedListener;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment.OnThemeSelectedListener;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_CONFIGURE;
import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

public class ConfigurationActivity extends AppCompatActivity implements OnCheckedChangeListener, OnClickListener,
        OnTimeZoneSelectedListener, OnDateFormatSelectedListener, OnThemeSelectedListener,
        InAppBillingListener, OnAppSelectedListener {

    private static final int REQUEST_CODE_PURCHASE = 42;

    private WidgetsAdapter widgetsAdapter;
    private ViewPager widgetsViewPager;
    private Switch timeFormatSwitch;
    private TextView timeZoneTextView;
    private TextView dateFormatTextView;
    private TextView themeTextView;
    private TextView appTextView;
    private Switch hideIconSwitch;
    private Button addWidgetButton;
    private Button upgradeButton;
    private View noWidgetsView;
    private Settings settings;
    private WidgetUpdater widgetUpdater;
    private InAppBilling inAppBilling;

    public static Intent newIntent(Context context, int widgetId) {
        return new Intent(context, ConfigurationActivity.class)
                .putExtra(EXTRA_APPWIDGET_ID, widgetId);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        this.settings = new Settings(this);
        this.widgetUpdater = new WidgetUpdater(this);

        setContentView(R.layout.activity_configuration);
        findViews();
        int[] widgetIds = getWidgetIds();
        setupViews(widgetIds);
        setupUi(widgetIds);
        this.inAppBilling = InAppBillingFactory.newInnAppBilling(this, this);
    }

    private void findViews() {
        this.widgetsViewPager = (ViewPager) findViewById(R.id.widgetsViewPager);
        this.timeFormatSwitch = (Switch) findViewById(R.id.timeFormatSwitch);
        this.timeZoneTextView = (TextView) findViewById(R.id.timeZoneTextView);
        this.dateFormatTextView = (TextView) findViewById(R.id.dateFormatTextView);
        this.themeTextView = (TextView) findViewById(R.id.themeTextView);
        this.appTextView = (TextView) findViewById(R.id.appTextView);
        this.noWidgetsView = findViewById(R.id.noWidgetsView);
        this.hideIconSwitch = (Switch) findViewById(R.id.hideIconSwitch);
        this.addWidgetButton = (Button) findViewById(R.id.addWidgetButton);
        this.upgradeButton = (Button) findViewById(R.id.upgradeButton);
    }

    private void setupViews(int[] widgetIds) {
        timeFormatSwitch.setOnCheckedChangeListener(this);
        hideIconSwitch.setOnCheckedChangeListener(this);
        widgetsViewPager.addOnPageChangeListener(new WidgetSettingBinder(widgetIds));
        timeZoneTextView.setOnClickListener(this);
        dateFormatTextView.setOnClickListener(this);
        themeTextView.setOnClickListener(this);
        addWidgetButton.setOnClickListener(this);
        upgradeButton.setOnClickListener(this);
        appTextView.setOnClickListener(this);
        hideIconSwitch.setChecked(settings.isHideIcon());
        this.widgetsAdapter = new WidgetsAdapter(widgetIds, this, settings);
        widgetsViewPager.setAdapter(widgetsAdapter);
    }

    private void setupUi(int[] widgetIds) {
        int currentWidget = getCurrentWidget(widgetIds);
        widgetsViewPager.setCurrentItem(currentWidget);
        boolean hasWidgets = currentWidget >= 0;
//        noWidgetsView.setVisibility(hasWidgets ? View.GONE : View.VISIBLE);
        addWidgetButton.setVisibility(isNewlyCreatedWidget() ? View.VISIBLE : View.GONE);

        if (hasWidgets) {
            bindWidgetSettings(widgetIds[0]);
        } else {
            bindWidgetSettings(-1);
            setSettingsEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        widgetUpdater.updateImmediately();
    }

    private int[] getWidgetIds() {
        return isNewlyCreatedWidget() ?
                new int[]{getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)} :
                AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
    }

    public int getCurrentWidget(int[] widgetIds) {
        if (getIntent().hasExtra(EXTRA_APPWIDGET_ID)) {
            int widgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, -1);
            for (int i = 0; i < widgetIds.length; i++) {
                if (widgetIds[i] == widgetId) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void bindWidgetSettings(int widgetId) {
        WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);

        timeFormatSwitch.setChecked(widgetOptions.format24);

        String timeZoneId = widgetOptions.timeZoneId;
        TimeZoneInfo timeZoneInfo = TimeZones.getTimeZoneInfo(this, timeZoneId);
        String formattedTimeZone = TimeZones.format(timeZoneInfo);
        timeZoneTextView.setText(formattedTimeZone);

        String date = NixieUtils.getCurrentDate(widgetOptions.monthFirst, timeZoneId);
        dateFormatTextView.setText(getString(R.string.date_format, date));

        bindAppToLaunch(widgetOptions.appToLaunch);

        Theme theme = widgetOptions.theme;
        themeTextView.setText(getString(R.string.theme, getString(theme.nameId)));
    }

    private void bindAppToLaunch(ExternalApp app) {
        appTextView.setText(getString(R.string.app_to_launch, app.getName(this)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timeZoneTextView:
                new TimeZonePickerDialogFragment().show(getSupportFragmentManager(), "TimeZonePicker");
                break;
            case R.id.dateFormatTextView:
                DateFormatDialogFragment.show(getSupportFragmentManager(), getCurrentWidgetId());
                break;
            case R.id.themeTextView:
                new ThemePickerDialogFragment().show(getSupportFragmentManager(), "ThemePicker");
                break;
            case R.id.appTextView:
                new AppPickerDialogFragment().show(getSupportFragmentManager(), "AppPicker");
                break;
            case R.id.addWidgetButton:
                applyNewWidget();
                break;
            case R.id.upgradeButton:
                upgradeToPro();
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        switch (button.getId()) {
            case R.id.timeFormatSwitch:
                onTimeFormatChanged(checked);
                break;
            case R.id.hideIconSwitch:
                onHideIconValueChanged(checked);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void onTimeFormatChanged(boolean format24) {
        if (widgetsAdapter.getCount() > 0) {
            settings.setTimeFormat(getCurrentWidgetId(), format24);
            updatePreview();
        }
    }

    @Override
    public void onTimeZoneSelected(TimeZoneInfo timeZoneInfo) {
        settings.setTimezone(getCurrentWidgetId(), timeZoneInfo.id);
        timeZoneTextView.setText(TimeZones.format(timeZoneInfo));
        updatePreview();
    }

    @Override
    public void onDateFormatSelected(boolean monthFirst) {
        int widgetId = getCurrentWidgetId();
        settings.setMonthFirst(widgetId, monthFirst);
        WidgetOptions widgetOptions = settings.getWidgetOptions(widgetId);
        String date = NixieUtils.getCurrentDate(monthFirst, widgetOptions.timeZoneId);
        dateFormatTextView.setText(getString(R.string.date_format, date));
        updatePreview();
    }

    @Override
    public void onAppSelected(ExternalApp app) {
        settings.setAppToLaunch(getCurrentWidgetId(), app);
        bindAppToLaunch(app);
    }

    @Override
    public void onThemeSelected(Theme theme) {
        settings.setTheme(getCurrentWidgetId(), theme);
        themeTextView.setText(getString(R.string.theme, getString(theme.nameId)));
        updatePreview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE) {
            inAppBilling.processPurchase(requestCode, resultCode, data);
        }
    }

    private void onHideIconValueChanged(boolean hideIcon) {
        if (settings.isHideIcon() != hideIcon) {
            NixieUtils.setLauncherIconVisibility(this, !hideIcon);
            settings.setHideIcon(hideIcon);
        }
    }

    private int getCurrentWidget() {
        return widgetsViewPager.getCurrentItem();
    }

    private int getCurrentWidgetId() {
        int currentWidget = widgetsViewPager.getCurrentItem();
        return widgetsAdapter.getWidgetId(currentWidget);
    }

    private void updatePreview() {
        widgetsAdapter.refresh(widgetsViewPager, getCurrentWidget());
    }

    private void applyNewWidget() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getCurrentWidgetId());
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void upgradeToPro() {
        inAppBilling.purchase(this, REQUEST_CODE_PURCHASE);
    }

    private void setSettingsEnabled(boolean enabled) {
        for (View view : new View[]{timeFormatSwitch, timeZoneTextView, dateFormatTextView, themeTextView, appTextView, hideIconSwitch}) {
            view.setEnabled(enabled);
        }
    }

    private boolean isNewlyCreatedWidget() {
        return TextUtils.equals(getIntent().getAction(), ACTION_APPWIDGET_CONFIGURE);
    }

    @Override
    public void onPurchasingError() {
        // TODO:
    }

    @Override
    public void onProductsFetched(boolean hasPro) {
        setSettingsEnabled(hasPro && widgetsAdapter.getCount() > 0);
        upgradeButton.setVisibility(hasPro ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onPurchased() {
        setSettingsEnabled(true);
        upgradeButton.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppBilling.release();
    }

    private class WidgetSettingBinder implements ViewPager.OnPageChangeListener {

        private final int[] widgetIds;

        WidgetSettingBinder(int[] widgetIds) {
            this.widgetIds = widgetIds;
        }

        @Override
        public void onPageSelected(int position) {
            int widgetId = widgetIds[position];
            bindWidgetSettings(widgetId);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
