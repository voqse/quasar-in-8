package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface ThemeDrawer {

    void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint);

    void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint);

    void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint);

    void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint);

    void drawDot(Canvas canvas, Bitmap dot, Paint paint);

    void drawFront(Canvas canvas, Bitmap front, Paint paint);
}
