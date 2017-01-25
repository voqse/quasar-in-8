package com.voqse.nixieclock.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.Utils;
import com.voqse.nixieclock.WidgetUpdater;
import com.voqse.nixieclock.timezone.TimeZoneInfo;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment;
import com.voqse.nixieclock.timezone.TimeZonePickerDialogFragment.OnTimeZoneSelectedListener;
import com.voqse.nixieclock.timezone.TimeZones;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment;
import com.voqse.nixieclock.widget.support.DateFormatDialogFragment.OnDateFormatSelectedListener;

public class ConfigurationActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, OnTimeZoneSelectedListener, OnDateFormatSelectedListener {

    private static final String EXTRA_WIDGET_ID = BuildConfig.APPLICATION_ID + ".EXTRA_WIDGET_ID";

    private WidgetsAdapter widgetsAdapter;
    private ViewPager widgetsViewPager;
    private Switch timeFormatSwitch;
    private TextView timeZoneTextView;
    private TextView dateFormatTextView;
    private Settings settings;
    private WidgetUpdater widgetUpdater;

    public static Intent newIntent(Context context, int widgetId) {
        return new Intent(context, ConfigurationActivity.class)
                .putExtra(EXTRA_WIDGET_ID, widgetId);
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_configuration);
        this.settings = new Settings(this);
        this.widgetUpdater = new WidgetUpdater(this);
        this.widgetsViewPager = (ViewPager) findViewById(R.id.widgetsViewPager);
        this.timeFormatSwitch = (Switch) findViewById(R.id.timeFormatSwitch);
        this.timeZoneTextView = (TextView) findViewById(R.id.timeZoneTextView);
        this.dateFormatTextView = (TextView) findViewById(R.id.dateFormatTextView);

        int[] widgetIds = getWidgetIds();
        timeFormatSwitch.setOnCheckedChangeListener(this);
        widgetsViewPager.addOnPageChangeListener(new WidgetSettingBinder(widgetIds));
        timeZoneTextView.setOnClickListener(this);
        dateFormatTextView.setOnClickListener(this);

        this.widgetsAdapter = new WidgetsAdapter(widgetIds, this, settings);
        widgetsViewPager.setAdapter(widgetsAdapter);
        int currentWidget = getCurrentWidget(widgetIds);
        widgetsViewPager.setCurrentItem(currentWidget);
        if (currentWidget == 0) {
            bindWidgetSettings(widgetIds[0]);
        }
    }

    private int[] getWidgetIds() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        return appWidgetManager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
    }

    public int getCurrentWidget(int[] widgetIds) {
        if (getIntent().hasExtra(EXTRA_WIDGET_ID)) {
            int widgetId = getIntent().getIntExtra(EXTRA_WIDGET_ID, -1);
            for (int i = 0; i < widgetIds.length; i++) {
                if (widgetIds[i] == widgetId) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void bindWidgetSettings(int widgetId) {
        boolean format24 = settings.is24TimeFormat(widgetId);
        timeFormatSwitch.setChecked(format24);

        String timeZoneId = settings.getTimeZone(widgetId);
        TimeZoneInfo timeZoneInfo = TimeZones.getTimeZoneInfo(this, timeZoneId);
        String formattedTimeZone = TimeZones.format(timeZoneInfo);
        timeZoneTextView.setText(formattedTimeZone);

        boolean monthFirst = settings.isMonthFirst(widgetId);
        String date = Utils.getCurrentDate(monthFirst, timeZoneId);
        dateFormatTextView.setText(getString(R.string.configuration_date_format) + "\n" + date);
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
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton timeFormatSwitch, boolean format24) {
        settings.setTimeFormat(getCurrentWidget(), format24);
        updatePreviewAndWidget(getCurrentWidgetId(), getCurrentWidget());
    }

    @Override
    public void onTimeZoneSelected(TimeZoneInfo timeZoneInfo) {
        int widgetId = getCurrentWidgetId();
        settings.setTimezone(widgetId, timeZoneInfo.id);
        timeZoneTextView.setText(TimeZones.format(timeZoneInfo));
        updatePreviewAndWidget(getCurrentWidget(), widgetId);
    }

    @Override
    public void onDateFormatSelected(boolean monthFirst) {
        int currentWidget = getCurrentWidget();
        int widgetId = widgetsAdapter.getWidgetId(currentWidget);
        settings.setMonthFirst(widgetId, monthFirst);
        String date = Utils.getCurrentDate(monthFirst, settings.getTimeZone(widgetId));
        dateFormatTextView.setText(getString(R.string.configuration_date_format) + "\n" + date);
        updatePreviewAndWidget(currentWidget, widgetId);
    }

    private int getCurrentWidget() {
        return widgetsViewPager.getCurrentItem();
    }

    private int getCurrentWidgetId() {
        int currentWidget = widgetsViewPager.getCurrentItem();
        return widgetsAdapter.getWidgetId(currentWidget);
    }

    private void updatePreviewAndWidget(int currentWidget, int widgetId) {
        widgetsAdapter.refresh(widgetsViewPager, currentWidget);
        widgetUpdater.updateImmediately(widgetId);
    }

    private static class WidgetsAdapter extends PagerAdapter {

        private final int[] widgetIds;
        private final LayoutInflater inflater;
        private final Settings settings;

        private WidgetsAdapter(int[] widgetIds, Context context, Settings settings) {
            this.widgetIds = widgetIds;
            this.inflater = LayoutInflater.from(context);
            this.settings = settings;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.widget, container, false);
            bind(view, position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return widgetIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        void refresh(ViewPager viewPager, int position) {
            View view = viewPager.findViewWithTag(position);
            if (view != null) {
                bind(view, position);
            }
        }

        void bind(View view, int position) {
            TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
            int widgetId = widgetIds[position];
            boolean format24 = settings.is24TimeFormat(widgetId);
            String timeZone = settings.getTimeZone(widgetId);
            timeTextView.setText(Utils.getCurrentTime(format24, timeZone));

            view.setTag(position);
        }

        int getWidgetId(int position) {
            return widgetIds[position];
        }
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
