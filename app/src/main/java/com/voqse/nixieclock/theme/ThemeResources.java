package com.voqse.nixieclock.theme;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;

import com.voqse.nixieclock.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс для извлечения необхдимых для отрисовки виджета ресурсов.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ThemeResources {

    private static final Logger LOG = LoggerFactory.getLogger("ThemeResources");

    private final ResourceCipher resourceCipher;
    private final AssetManager assets;

    public ThemeResources(@NonNull Context context) {
        this.assets = context.getAssets();
        this.resourceCipher = new ResourceCipher();
    }

    public Bitmap getBack(@NonNull Theme theme, boolean x2) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        String back = theme.useBasement ? "basement_back" : "no_basement_back";
        return decodeBitmap(theme, getName(back, x2), options);
    }

    public Bitmap getFront(@NonNull Theme theme, boolean x2) {
        return decodeBitmap(theme, getName("basement_front", x2), null);
    }

    public Bitmap getDot(@NonNull Theme theme, boolean x2) {
        return decodeBitmap(theme, getName("binary", x2), null);
    }

    public Bitmap getDigit(@NonNull Theme theme, int digit, boolean x2) {
        return decodeBitmap(theme, getName("n" + digit, x2), null);
    }

    private String getName(String name, boolean x2) {
        return x2 ? name + "_x2" : name;
    }

    private Bitmap decodeBitmap(@NonNull Theme theme, String fileName, BitmapFactory.Options options) {
        String assetsName = getAssetsName(theme, fileName);
        InputStream in = null;
        try {
            in = openBitmapStream(assetsName);
            return BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading assets " + assetsName, e);
        } finally {
            IoUtils.closeSilently(in);
        }
    }

    private InputStream openBitmapStream(String assetsName) throws IOException {
        try {
            byte[] assetsContent = readAssetsContent(assetsName);
            byte[] decryptedData = resourceCipher.decrypt(assetsContent);
            return new ByteArrayInputStream(decryptedData);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Error decrypting " + assetsName);
        }
    }

    private byte[] readAssetsContent(String assetsName) throws IOException {
        InputStream in = null;
        try {
            in = assets.open(assetsName);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IoUtils.copy(in, out);
            return out.toByteArray();
        } finally {
            IoUtils.closeSilently(in);
        }
    }

    private String getAssetsName(Theme theme, String fileName) {
        return new StringBuilder("themes/")
                .append(theme.resources)
                .append('/')
                .append(theme.resources)
                .append('_')
                .append(fileName)
                .append(".png")
                .toString();
    }
}
