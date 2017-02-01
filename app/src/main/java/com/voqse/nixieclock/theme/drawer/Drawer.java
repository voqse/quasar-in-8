package com.voqse.nixieclock.theme.drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;

import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.theme.ThemeResources;
import com.voqse.nixieclock.utils.NixieUtils;
import com.voqse.nixieclock.widget.TextMode;
import com.voqse.nixieclock.widget.WidgetOptions;

import hugo.weaving.DebugLog;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */

public class Drawer {

    private final Paint paint;
    private ThemeResources themeResources;

    public Drawer(Context context) {
        this.themeResources = new ThemeResources(context);
        this.paint = new Paint();
        paint.setAntiAlias(true);
    }

    @DebugLog
    public Bitmap draw(WidgetOptions widgetOptions, @Nullable Bitmap bitmapToReuse, TextMode textMode) {
        Theme theme = widgetOptions.theme;
        Bitmap basement = themeResources.getBasement(theme, bitmapToReuse);
        Canvas canvas = new Canvas(basement);
        String text = getTextToDraw(widgetOptions, textMode);
        ThemeDrawer themeDrawer = theme.newThemeDrawer();
        themeDrawer.drawFirstDigit(canvas, getDigit(theme, text, 0), paint);
        themeDrawer.drawSecondDigit(canvas, getDigit(theme, text, 1), paint);
        themeDrawer.drawThirdDigit(canvas, getDigit(theme, text, 2), paint);
        themeDrawer.drawFourthDigit(canvas, getDigit(theme, text, 3), paint);
        if (textMode != TextMode.YEAR) {
            themeDrawer.drawDot(canvas, themeResources.getDot(theme), paint);
        }
        themeDrawer.drawFront(canvas, themeResources.getFront(theme), paint);
        return basement;
    }

    private String getTextToDraw(WidgetOptions widgetOptions, TextMode textMode) {
        String text;
        switch (textMode) {
            case TIME:
                text = NixieUtils.getCurrentTime(widgetOptions.format24, widgetOptions.timeZoneId);
                break;
            case DATE:
                text = NixieUtils.getCurrentDate(widgetOptions.monthFirst, widgetOptions.timeZoneId);
                break;
            case YEAR:
                text = NixieUtils.getCurrentYear(widgetOptions.timeZoneId);
                break;
            default:
                throw new IllegalStateException();
        }
        return text.replace(".", "").replace(":", "");
    }

    private Bitmap getDigit(Theme theme, String text, int index) {
        int digit = text.charAt(index) - '0';
        return themeResources.getDigit(theme, digit);
    }
}
