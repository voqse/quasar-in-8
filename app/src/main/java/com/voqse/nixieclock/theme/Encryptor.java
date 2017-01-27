package com.voqse.nixieclock.theme;

import com.voqse.nixieclock.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class Encryptor {

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Folder with files to encrypt is not set in args!");
        }

        Encryptor encryptor = new Encryptor(args[0]);
        encryptor.encrypt();
    }

    private final File root;

    private Encryptor(String rootPath) {
        this.root = new File(rootPath);
        validate(root);
    }

    private void encrypt() throws IOException, GeneralSecurityException {
        ResourceCipher cipher = new ResourceCipher();
        File outDirectory = new File(root, "out");
        IoUtils.createDirectory(outDirectory);
        File[] files = root.listFiles();
        for (File file : files) {
            if (!file.equals(outDirectory)) {
                byte[] fileContent = IoUtils.readFile(file);
                byte[] encryptedFile = cipher.encrypt(fileContent);
                IoUtils.saveToFile(encryptedFile, new File(outDirectory, file.getName()));
            }
        }
    }

    private void validate(File root) {
        if (!root.exists()) {
            throw new IllegalArgumentException("Folder with files to encrypt '" + root + "' is not exist");
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException("Folder with files to encrypt '" + root + "' is not directory!");
        }
    }
}
