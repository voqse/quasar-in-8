package com.voqse.nixieclock.theme.drawer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/** Класс для отрисовки отдельных элеметов видежета на {@link Canvas}.
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface ThemeDrawer {

    void drawFirstDigit(Canvas canvas, Bitmap firstDigit, Paint paint, boolean x2);

    void drawSecondDigit(Canvas canvas, Bitmap secondDigit, Paint paint, boolean x2);

    void drawThirdDigit(Canvas canvas, Bitmap thirdDigit, Paint paint, boolean x2);

    void drawFourthDigit(Canvas canvas, Bitmap fourthDigit, Paint paint, boolean x2);

    void drawDot(Canvas canvas, Bitmap dot, Paint paint, boolean x2);

    void drawFront(Canvas canvas, Bitmap front, Paint paint, boolean x2);
}
