package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TronThemeDrawer implements ThemeDrawer {

    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(firstDigit, x2 ? 97 : 48, x2 ? 89 : 44, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(secondDigit, x2 ? 289 : 144, x2 ? 89 : 44, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(thirdDigit, x2 ? 553 : 276, x2 ? 89 : 44, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(fourthDigit, x2 ? 744 : 372, x2 ? 89 : 44, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint, boolean x2) {
        canvas.drawBitmap(dot, x2 ? 496 : 248, x2 ? 319 : 159, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint, boolean x2) {
        canvas.drawBitmap(front, x2 ? 109 : 54, x2 ? 50 : 25, paint);
    }
}
