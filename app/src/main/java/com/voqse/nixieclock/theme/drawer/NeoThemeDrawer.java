package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class NeoThemeDrawer implements ThemeDrawer {
    
    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(firstDigit, x2 ? 100 : 50, x2 ? 104 : 52, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(secondDigit, x2 ? 290 : 145, x2 ? 100 : 50, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(thirdDigit, x2 ? 550 : 275, x2 ? 100 : 50, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(fourthDigit, x2 ? 740 : 370, x2 ? 100 : 50, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint, boolean x2) {
        canvas.drawBitmap(dot, x2 ? 496 : 248, x2 ? 324 : 162, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint, boolean x2) {
        canvas.drawBitmap(front, x2 ? 113 : 56, x2 ? 40 : 20, paint);
    }
}
