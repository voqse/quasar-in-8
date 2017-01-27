package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class NeoThemeDrawer implements ThemeDrawer {

    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint) {
        canvas.drawBitmap(firstDigit, 100, 104, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint) {
        canvas.drawBitmap(secondDigit, 290, 100, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint) {
        canvas.drawBitmap(thirdDigit, 550, 100, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint) {
        canvas.drawBitmap(fourthDigit, 740, 100, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint) {
        canvas.drawBitmap(dot, 496, 324, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint) {
        canvas.drawBitmap(front, 113, 40, paint);
    }
}
