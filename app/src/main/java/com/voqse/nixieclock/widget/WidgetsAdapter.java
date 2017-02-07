package com.voqse.nixieclock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.voqse.nixieclock.R;
import com.voqse.nixieclock.theme.drawer.Drawer;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
class WidgetsAdapter extends PagerAdapter {

    private final Drawer drawer;
    private final int[] widgetIds;
    private final LayoutInflater inflater;
    private final WidgetOptionsProvider widgetOptionsProvider;

    WidgetsAdapter(@NonNull int[] widgetIds, @NonNull Context context, @NonNull WidgetOptionsProvider widgetOptionsProvider) {
        this.drawer = new Drawer(context);
        this.widgetIds = widgetIds;
        this.inflater = LayoutInflater.from(context);
        this.widgetOptionsProvider = widgetOptionsProvider;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.widget_preview, container, false);
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

    private void bind(View view, int position) {
        ImageView imageView = (ImageView) view.findViewById(R.id.widgetImageView);
        int widgetId = widgetIds[position];
        WidgetOptions widgetOptions = widgetOptionsProvider.provideWidgetOptions(widgetId);
        Bitmap bitmapToReuse = getImageBitmap(imageView);
        Bitmap bitmap = drawer.draw(widgetOptions, bitmapToReuse, TextMode.TIME, true);
        imageView.setImageBitmap(bitmap);
        view.setTag(position);
    }

    @Nullable
    private Bitmap getImageBitmap(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    int getWidgetId(int position) {
        return widgetIds[position];
    }

    public interface WidgetOptionsProvider {

        WidgetOptions provideWidgetOptions(int widgetId);
    }
}
