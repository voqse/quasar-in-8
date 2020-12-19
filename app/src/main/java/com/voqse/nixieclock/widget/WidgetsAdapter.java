package com.voqse.nixieclock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.theme.drawer.Drawer;
import com.voqse.nixieclock.theme.drawer.DrawerNew;

import java.util.List;

/**
 * Адаптер для слайдера виджетов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class WidgetsAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<Integer> widgetIds;
    private final LayoutInflater inflater;
    private final WidgetOptionsProvider widgetOptionsProvider;

    WidgetsAdapter(@NonNull List<Integer> widgetIds, @NonNull Context context, @NonNull WidgetOptionsProvider widgetOptionsProvider) {
        this.mContext = context;
        this.widgetIds = widgetIds;
        this.inflater = LayoutInflater.from(context);
        this.widgetOptionsProvider = widgetOptionsProvider;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.widget_preview, container, false);
        bind(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return widgetIds.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    void refresh(ViewPager viewPager, int position) {
        View view = viewPager.findViewWithTag(position);
        if (view != null) {
            bind(view, position);
        }
    }

    private void bind(View view, int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.widgetImageView);
        int widgetId = widgetIds.get(position);
        WidgetOptions widgetOptions = widgetOptionsProvider.provideWidgetOptions(widgetId);

        Drawer drawer;
        if (widgetOptionsProvider.provideWidgetOptions(widgetId).theme.isItNew) {
            drawer = new DrawerNew(mContext);
        } else {
            drawer = new Drawer(mContext);
        }

        Bitmap bitmap = drawer.draw(widgetOptions, TextMode.TIME, true);
        imageView.setImageBitmap(bitmap);
        view.setTag(position);
    }

    int getWidgetId(int position) {
        return widgetIds.get(position);
    }

    public interface WidgetOptionsProvider {

        WidgetOptions provideWidgetOptions(int widgetId);
    }
}
