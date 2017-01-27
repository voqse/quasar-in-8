package com.voqse.nixieclock.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.voqse.nixieclock.BuildConfig;
import com.voqse.nixieclock.R;
import com.voqse.nixieclock.Utils;

import hugo.weaving.DebugLog;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class Drawer {

    private final Resources resources;
    private final Paint paint;

    public Drawer(Context context) {
        this.resources = context.getResources();
        this.paint = new Paint();
        paint.setAntiAlias(true);
    }

    @DebugLog
    public Bitmap draw(WidgetOptions widgetOptions, @Nullable Bitmap bitmapToReuse) {
        Bitmap basement = decodeBasement(bitmapToReuse);
        Canvas canvas = new Canvas(basement);
        String text = Utils.getCurrentTime(widgetOptions.format24, widgetOptions.timeZoneId);
        drawDigit(canvas, text.charAt(0), 100, 100);
        drawDigit(canvas, text.charAt(1), 290, 100);
        drawDigit(canvas, text.charAt(3), 550, 100);
        drawDigit(canvas, text.charAt(4), 740, 100);
        drawDot(canvas);
        drawFront(canvas);
        return basement;
    }

    private void drawDot(Canvas canvas) {
        Bitmap front = decode(R.drawable.neo_binary);
        canvas.drawBitmap(front, 483, 103, paint);
    }

    private void drawFront(Canvas canvas) {
        Bitmap front = decode(R.drawable.neo_basement_front);
        canvas.drawBitmap(front, 114, 38, paint);
    }

    private void drawDigit(Canvas canvas, char digitChar, int left, int top) {
        int digit = digitChar - '0';
        Bitmap firstDigit = decodeDigit(digit);
        canvas.drawBitmap(firstDigit, left, top, paint);
    }

    private Bitmap decodeBasement(@Nullable Bitmap bitmapToReuse) {
        cleanBitmap(bitmapToReuse);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inBitmap = bitmapToReuse;
        return BitmapFactory.decodeResource(resources, R.drawable.neo_basement_back, options);
    }

    private void cleanBitmap(@Nullable Bitmap bitmapToReuse) {
        if (bitmapToReuse != null) {
            bitmapToReuse.eraseColor(Color.TRANSPARENT);
        }
    }

    private Bitmap decodeDigit(int digit) {
        String resourceName = "neo_n" + digit;
        int resourceId = resources.getIdentifier(resourceName, "drawable", BuildConfig.APPLICATION_ID);
        return decode(resourceId);
    }

    private Bitmap decode(int id) {
        return BitmapFactory.decodeResource(resources, id);
    }
}
