package com.voqse.nixieclock.theme.drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.voqse.nixieclock.theme.Theme;
import com.voqse.nixieclock.theme.ThemeResources;
import com.voqse.nixieclock.utils.NixieUtils;
import com.voqse.nixieclock.widget.TextMode;
import com.voqse.nixieclock.widget.WidgetOptions;


/**
 * Класс для отрисовки виджета на {@link Bitmap}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class Drawer {

    private static final String TAG = "Drawer";
    public static final int X2_MIN_WIDTH_PX = 600;
    public static final int X2_MIN_HEIGHT_PX = 300;

    private final Paint paint;
    private ThemeResources themeResources;

    public Drawer(Context context) {
        this.themeResources = new ThemeResources(context);
        this.paint = new Paint();
        paint.setAntiAlias(true);
    }

    public Bitmap draw(WidgetOptions widgetOptions, TextMode textMode, boolean x2) {
        Theme theme = widgetOptions.theme;
        Bitmap basement = themeResources.getBack(theme, x2);
        Canvas canvas = new Canvas(basement);
        String text = getTextToDraw(widgetOptions, textMode);
        Log.d(TAG, "draw: Draw text '" + text + "'. x2 quality? " + x2);
        ThemeDrawer themeDrawer = theme.newThemeDrawer();
        themeDrawer.drawFirstDigit(canvas, getDigit(theme, text, 0, x2), paint, x2);
        themeDrawer.drawSecondDigit(canvas, getDigit(theme, text, 1, x2), paint, x2);
        themeDrawer.drawThirdDigit(canvas, getDigit(theme, text, 2, x2), paint, x2);
        themeDrawer.drawFourthDigit(canvas, getDigit(theme, text, 3, x2), paint, x2);
        if (textMode != TextMode.YEAR && (widgetOptions.format24 || !NixieUtils.isAm(widgetOptions.timeZoneId))) {
            themeDrawer.drawDot(canvas, themeResources.getDot(theme, x2), paint, x2);
        }
        themeDrawer.drawFront(canvas, themeResources.getFront(theme, x2), paint, x2);
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

    private Bitmap getDigit(Theme theme, String text, int index, boolean x2) {
        int digit = text.charAt(index) - '0';
        return themeResources.getDigit(theme, digit, x2);
    }
}
