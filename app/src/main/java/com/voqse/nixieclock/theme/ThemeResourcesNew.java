package com.voqse.nixieclock.theme;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.voqse.nixieclock.utils.IoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Класс для извлечения необхдимых для отрисовки виджета ресурсов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ThemeResourcesNew {

    private static final Logger LOG = LoggerFactory.getLogger("ThemeResourcesNew");

    private final AssetManager assetManager;

    public ThemeResourcesNew(@NonNull Context context) {
        this.assetManager = context.getAssets();
    }

    public Bitmap getBack(@NonNull Theme theme, boolean x2) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        String back = theme.useBasement ? "base" : "nobase";
        return getBitmapFromAsset(theme, getName(back, x2), options);
    }

    public Bitmap getDigit(@NonNull Theme theme, int digit, int index, boolean x2) {
        String name = getName("00" + index + digit, x2);
        return getBitmapFromAsset(theme, name, null);

    }

    private String getName(String name, boolean x2) {
        return x2 ? name + "_x2" : name;
    }

    private Bitmap getBitmapFromAsset(Theme theme, String fileName, BitmapFactory.Options options) {
        String filePath = getAssetName(theme, fileName);
        InputStream istr = null;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr, null, options);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading assets " + filePath, e);
        } finally {
            IoUtils.closeSilently(istr);
        }
        return bitmap;
    }

    private String getAssetName(Theme theme, String fileName) {
        return new StringBuilder("themes/")
                .append(theme.resources)
                .append('/')
                .append(theme.resources)
                .append(fileName)
                .append(".png")
                .toString();
    }
}
