package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class NewThemeDrawer implements ThemeDrawer {
    
    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(firstDigit, 0, 0, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(secondDigit, 0, 0, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(thirdDigit, 0, 0, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint, boolean x2) {
        canvas.drawBitmap(fourthDigit, 0, 0, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint, boolean x2) {
        canvas.drawBitmap(dot, 0, 0, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint, boolean x2) {
        canvas.drawBitmap(front, 0, 0, paint);
    }
}
