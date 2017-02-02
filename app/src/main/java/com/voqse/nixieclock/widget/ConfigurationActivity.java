package com.voqse.nixieclock.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.voqse.nixieclock.widget.support.AboutDialogFragment;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment;
import com.voqse.nixieclock.widget.support.AppPickerDialogFragment.OnAppSelectedListener;
import com.voqse.nixieclock.widget.support.ApplySettingsDialogFragment;
import com.voqse.nixieclock.widget.support.ApplySettingsDialogFragment.OnApplySettingsDialogClickListener;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment.OnDateFormatSelectedListener;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment;
import com.voqse.nixieclock.widget.support.ThemePickerDialogFragment.OnThemeSelectedListener;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_CONFIGURE;
import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;
import static com.voqse.nixieclock.utils.NixieUtils.formatTwoLineText;

public class ConfigurationActivity extends AppCompatActivity implements OnCheckedChangeListener, OnClickListener,
        OnTimeZoneSelectedListener, OnDateFormatSelectedListener, OnThemeSelectedListener,
        InAppBillingListener, OnAppSelectedListener, OnApplySettingsDialogClickListener, WidgetsAdapter.WidgetOptionsProvider {

    private static final int REQUEST_CODE_PURCHASE = 42;

    private WidgetsAdapter widgetsAdapter;
    private ViewPager widgetsViewPager;
    private Switch timeFormatSwitch;
    private TextView timeZoneTextView;
    private TextView dateFormatTextView;
    private TextView themeTextView;
    private TextView appTextView;
    private Switch hideIconSwitch;
    private Button applyWidgetButton;
    private Button upgradeButton;
    private View noWidgetsView;
    private Toolbar toolbar;
    private Settings settings;
    private InAppBilling inAppBilling;
    private SparseArray<WidgetOptions> widgetsOptions;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        this.settings = new Settings(this);

        setContentView(R.layout.activity_configuration);
        findViews();
        int[] widgetIds = getWidgetIds();
        this.widgetsOptions = getWidgetOptions(widgetIds);
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
        this.applyWidgetButton = (Button) findViewById(R.id.applyWidgetButton);
        this.upgradeButton = (Button) findViewById(R.id.upgradeButton);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setupViews(int[] widgetIds) {
        setPreviewHeight();
        setSupportActionBar(toolbar);
        this.widgetsAdapter = new WidgetsAdapter(widgetIds, this, this);
        widgetsViewPager.setAdapter(widgetsAdapter);
        timeFormatSwitch.setOnCheckedChangeListener(this);
        hideIconSwitch.setOnCheckedChangeListener(this);
        widgetsViewPager.addOnPageChangeListener(new WidgetSettingBinder());
        timeZoneTextView.setOnClickListener(this);
        dateFormatTextView.setOnClickListener(this);
        themeTextView.setOnClickListener(this);
        applyWidgetButton.setOnClickListener(this);
        upgradeButton.setOnClickListener(this);
        appTextView.setOnClickListener(this);
        bindHideIcon(settings.isHideIcon());
    }

    private void setPreviewHeight() {
        View previewContainer = findViewById(R.id.previewContainer);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int previewHeight = (int) (9f / 16f * displayMetrics.widthPixels);
        previewContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, previewHeight));
    }

    private void setupUi(int[] widgetIds) {
        int currentWidget = getCurrentWidget(widgetIds);
        widgetsViewPager.setCurrentItem(currentWidget);
        boolean hasWidgets = widgetIds.length > 0;
        noWidgetsView.setVisibility(hasWidgets ? View.GONE : View.VISIBLE);
        applyWidgetButton.setVisibility(hasWidgets ? View.VISIBLE : View.GONE);
        WidgetOptions currentWidgetOptions = hasWidgets ? getCurrentWidgetOptions() : WidgetOptions.DEFAULT;
        bindWidgetSettings(currentWidgetOptions);
        if (!hasWidgets) {
            disableSettings();
        }
    }

    private SparseArray<WidgetOptions> getWidgetOptions(int[] widgetIds) {
        SparseArray<WidgetOptions> widgetOptionsArrays = new SparseArray<>(widgetIds.length);
        for (int widgetId : widgetIds) {
            widgetOptionsArrays.put(widgetId, settings.getWidgetOptions(widgetId));
        }
        return widgetOptionsArrays;
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

    private int[] getWidgetIds() {
        return isNewlyCreatedWidget() ?
                new int[]{getWidgetIdFromExtras()} :
                AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
    }

    public int getCurrentWidget(int[] widgetIds) {
        if (getIntent().hasExtra(EXTRA_APPWIDGET_ID)) {
            int widgetId = getWidgetIdFromExtras();
            for (int i = 0; i < widgetIds.length; i++) {
                if (widgetIds[i] == widgetId) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getWidgetIdFromExtras() {
        return getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
    }

    private void bindWidgetSettings(WidgetOptions widgetOptions) {
        bindTimeFormat(widgetOptions.format24, widgetOptions.timeZoneId);
        bindTimeZone(widgetOptions.timeZoneId);
        bindDateFormat(widgetOptions.monthFirst, widgetOptions.timeZoneId);
        bindAppToLaunch(widgetOptions.appToLaunch);
        bindTheme(widgetOptions.theme);
    }

    private void bindTimeFormat(boolean format24, String timeZoneId) {
        String label = getString(R.string.format_24);
        String time = NixieUtils.getCurrentTime(format24, timeZoneId, true);
        timeFormatSwitch.setChecked(format24);
        timeFormatSwitch.setText(formatTwoLineText(label, time));
    }

    private void bindTimeZone(String timeZoneId) {
        String label = getString(R.string.timezone);
        TimeZoneInfo timeZoneInfo = TimeZones.getTimeZoneInfo(this, timeZoneId);
        String timeZone = timeZoneInfo.getOffset() + " " + timeZoneInfo.city;
        timeZoneTextView.setText(formatTwoLineText(label, timeZone));
    }

    private void bindDateFormat(boolean monthFirst, String timeZoneId) {
        String label = getString(R.string.date_format);
        String date = NixieUtils.getCurrentDate(monthFirst, timeZoneId);
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
            case R.id.applyWidgetButton:
                applySettings();
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
            WidgetOptions newWidgetOptions = getCurrentWidgetOptions().changeFormat24(format24);
            changeCurrentWidgetOptions(newWidgetOptions);
            bindTimeFormat(format24, newWidgetOptions.timeZoneId);
            updatePreviewAndButton();
        }
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
        bindDateFormat(monthFirst, newWidgetOptions.timeZoneId);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE) {
            inAppBilling.processPurchase(requestCode, resultCode, data);
        }
    }

    private void onHideIconValueChanged(boolean checked) {
        updateButtons();
        bindHideIcon(checked);
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
        boolean hasPro = inAppBilling != null && inAppBilling.hasPro();
        boolean newWidget = isNewlyCreatedWidget();
        boolean settingsChanged = isCurrentWidgetSettingsChanged();

        boolean applyButtonActive = (newWidget && (hasPro || !settingsChanged)) || (!newWidget && hasPro && settingsChanged);
        boolean applyButtonDisabled = !newWidget && !settingsChanged;
        int applyButtonTextColorId = applyButtonActive ? android.R.color.white : R.color.text_white_disabled;

        applyWidgetButton.setBackgroundResource(applyButtonActive ? R.drawable.btn_blue : R.drawable.btn_dark);
        applyWidgetButton.setEnabled(!applyButtonDisabled);
        applyWidgetButton.setTextColor(getResources().getColor(applyButtonTextColorId));
        upgradeButton.setBackgroundResource(settingsChanged ? R.drawable.btn_blue : R.drawable.btn_dark);
    }

    private boolean isCurrentWidgetSettingsChanged() {
        if (widgetsAdapter.getCount() == 0) {
            return false;
        }
        int currentWidgetId = getCurrentWidgetId();
        WidgetOptions currentWidgetOptions = getCurrentWidgetOptions();
        return hideIconSwitch.isChecked() != settings.isHideIcon() ||
                !settings.getWidgetOptions(currentWidgetId).equals(currentWidgetOptions);
    }

    private void applySettings() {
        if ((inAppBilling == null || !inAppBilling.hasPro()) && isCurrentWidgetSettingsChanged()) {
            new ApplySettingsDialogFragment().show(getSupportFragmentManager(), "ApplySettingsConfirm");
        } else {
            boolean hideIcon = hideIconSwitch.isChecked();
            if (settings.isHideIcon() != hideIcon) {
                NixieUtils.setLauncherIconVisibility(this, !hideIcon);
                settings.setHideIcon(hideIcon);
            }

            WidgetOptions currentWidgetOptions = getCurrentWidgetOptions();
            int widgetId = getCurrentWidgetId();
            settings.setWidgetOptions(widgetId, currentWidgetOptions);

            new WidgetUpdater(this).updateImmediately();

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    }

    private void upgradeToPro() {
        inAppBilling.purchase(this, REQUEST_CODE_PURCHASE);
    }

    private void disableSettings() {
        for (View view : new View[]{timeFormatSwitch, timeZoneTextView, dateFormatTextView, themeTextView, appTextView, hideIconSwitch}) {
            view.setEnabled(false);
            view.setAlpha(0.5f);
        }
    }

    private boolean isNewlyCreatedWidget() {
        return TextUtils.equals(getIntent().getAction(), ACTION_APPWIDGET_CONFIGURE);
    }

    @Override
    public void onPurchasingError() {
        Toast.makeText(this, R.string.purchase_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProductsFetched(boolean hasPro) {
        upgradeButton.setVisibility(hasPro ? View.GONE : View.VISIBLE);
        updateButtons();
    }

    @Override
    public void onPurchased() {
        upgradeButton.setVisibility(View.GONE);
        updateButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppBilling.release();
    }

    @Override
    public void onApplySettingsClick(boolean buyClicked, boolean noClicked, boolean yesClicked) {
        if (buyClicked) {
            upgradeToPro();
        } else if (yesClicked) {
            changeCurrentWidgetOptions(WidgetOptions.DEFAULT);
            bindWidgetSettings(WidgetOptions.DEFAULT);
            hideIconSwitch.setChecked(false);
            updatePreviewAndButton();
        }
    }

    @Override
    public WidgetOptions provideWidgetOptions(int widgetId) {
        return widgetsOptions.get(widgetId);
    }

    private class WidgetSettingBinder implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            WidgetOptions widgetOptions = getCurrentWidgetOptions();
            bindWidgetSettings(widgetOptions);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
