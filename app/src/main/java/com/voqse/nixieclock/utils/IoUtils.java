package com.voqse.nixieclock.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Содержит ряд полезных методов для работы с потоками.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class IoUtils {

    private static final String TAG = "IoUtils";
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * Closes stream and releases any system resources associated with it.
     * <p>
     * Hide {@link IOException} if it occurs during closing resource
     * </p>
     *
     * @param closeableSource Closeable source to be needed to close
     */
    public static void closeSilently(Closeable closeableSource) {
        try {
            if (closeableSource != null) {
                closeableSource.close();
            }
        } catch (IOException e) {
            // hide exception, close source "silently"
            Log.d(TAG, "closeSilently: Error closing closeable source", e);
        }
    }

    /**
     * Copy bytes from an {@code InputStream} to an {@code OutputStream} with listening progress of copying.
     * <p>
     * This method buffers the input internally, so there is no need to use a {@code BufferedInputStream}. Method doesn't close
     * streams after copying.
     * </p>
     *
     * @param input  the {@code InputStream} to read from, if {@code null} {@link IOException} will be thrown.
     * @param output the {@code OutputStream} to write to, if {@code null} {@link IOException} will be thrown.
     * @return the number of bytes copied
     * @throws IOException         if an I/O error occurs or if the input or output is {@code null}
     * @throws ArithmeticException if the byte count is too large
     */
    public static long copy(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
        InputStream bufferedInputStream = new BufferedInputStream(input);
        OutputStream bufferedOutputStream = new BufferedOutputStream(output);

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = bufferedInputStream.read(buffer))) {
            bufferedOutputStream.write(buffer, 0, n);
            count += n;
        }
        bufferedOutputStream.flush();
        return count;
    }

    /**
     * Reads content of file.
     *
     * @param sourceFile file to be used for reading data, must be not null.
     * @return content of file.
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readFile(@NonNull File sourceFile) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            byte[] data = new byte[(int) sourceFile.length()];
            inputStream.read(data);
            return data;
        } finally {
            closeSilently(inputStream);
        }
    }

    /**
     * Saves data to file and creates parent directories if needed.
     *
     * @param data       byte data to be saved, must be not null.
     * @param targetFile file to be used for saving data, must be not null.
     * @throws IOException if an I/O error occurs
     */
    public static void saveToFile(@NonNull byte[] data, @NonNull File targetFile) throws IOException {
        OutputStream outputStream = null;
        try {
            createDirectory(targetFile.getParentFile());
            outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
            outputStream.write(data);
        } finally {
            closeSilently(outputStream);
        }
    }

    /**
     * Creates a directory with specified path.
     * <p/>
     * Directory can exist, but in this case should be directory, not file.
     *
     * @param directory directory to be created, must be not {@code null} and must be not file if exists.
     * @throws IOException if file is exist and it is not a directory of if any error occurs.
     */
    public static void createDirectory(@NonNull File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("File is not directory!");
            }
        } else {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                String error = String.format("Directory %s can't be created", directory.getAbsolutePath());
                throw new IOException(error);
            }
        }
    }

}
