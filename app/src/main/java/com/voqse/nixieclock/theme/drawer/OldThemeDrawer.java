package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class OldThemeDrawer implements ThemeDrawer {

    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(firstDigit, x2 ? 113 : 56, x2 ? 107 : 53, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(secondDigit, x2 ? 306 : 153, x2 ? 107 : 53, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(thirdDigit, x2 ? 536 : 268, x2 ? 107 : 53, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(fourthDigit, x2 ? 728 : 364, x2 ? 107 : 53, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint, boolean x2) {
        canvas.drawBitmap(dot, x2 ? 481 : 240, x2 ? 105 : 52, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint, boolean x2) {
        canvas.drawBitmap(front, x2 ? 121 : 60, x2 ? 27 : 13, paint);
    }
}
