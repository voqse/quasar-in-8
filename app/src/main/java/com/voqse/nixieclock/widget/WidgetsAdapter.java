package com.voqse.nixieclock.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.Utils;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
class WidgetsAdapter extends PagerAdapter {

    private final int[] widgetIds;
    private final LayoutInflater inflater;
    private final Settings settings;

    WidgetsAdapter(int[] widgetIds, Context context, Settings settings) {
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
