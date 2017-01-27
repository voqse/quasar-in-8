package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class TronThemeDrawer implements ThemeDrawer {

    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint) {
        canvas.drawBitmap(firstDigit, 97, 89, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint) {
        canvas.drawBitmap(secondDigit, 289, 89, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint) {
        canvas.drawBitmap(thirdDigit, 553, 89, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint) {
        canvas.drawBitmap(fourthDigit, 744, 89, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint) {
        canvas.drawBitmap(dot, 496, 319, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint) {
        canvas.drawBitmap(front, 109, 50, paint);
    }
}
