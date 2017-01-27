package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class OldThemeDrawer implements ThemeDrawer {

    @Override
    public void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint) {
        canvas.drawBitmap(firstDigit, 113, 107, paint);
    }

    @Override
    public void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint) {
        canvas.drawBitmap(secondDigit, 306, 107, paint);
    }

    @Override
    public void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint) {
        canvas.drawBitmap(thirdDigit, 536, 107, paint);
    }

    @Override
    public void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint) {
        canvas.drawBitmap(fourthDigit, 728, 107, paint);
    }

    @Override
    public void drawDot(Canvas canvas, Bitmap dot, Paint paint) {
        canvas.drawBitmap(dot, 481, 105, paint);
    }

    @Override
    public void drawFront(Canvas canvas, Bitmap front, Paint paint) {
        canvas.drawBitmap(front, 121, 27, paint);
    }
}
