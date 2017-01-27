package com.voqse.nixieclock.theme;

import android.support.annotation.NonNull;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ResourceCipher {

    private static final byte[] KEY_BYTES = new byte[]{-14, -39, -20, 56, 4, 56, -53, -42};
    private static final String ALGORITHM = "DES";

    private final Key key;
    private final Cipher cipher;

    public ResourceCipher() {
        try {
            this.key = new SecretKeySpec(KEY_BYTES, 0, KEY_BYTES.length, ALGORITHM);
            this.cipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException("Error creating cipher", e);
        }
    }

    public byte[] encrypt(@NonNull byte[] source) throws GeneralSecurityException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(source);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new GeneralSecurityException("Error encoding ", e);
        }
    }

    public byte[] decrypt(@NonNull byte[] source) throws GeneralSecurityException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(source);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new GeneralSecurityException("Error decoding ", e);
        }
    }
}
