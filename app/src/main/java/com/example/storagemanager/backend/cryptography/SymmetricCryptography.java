package com.example.storagemanager.backend.cryptography;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SymmetricCryptography {
    private final SecretKeySpec secretKey;
    private final Cipher cipher;

    public SymmetricCryptography(int length, String algorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(length);
        SecretKey secretKey = keyGen.generateKey();
        this.secretKey = new SecretKeySpec(secretKey.getEncoded(), algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }

    public SymmetricCryptography(SecretKeySpec secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = secretKey;
        this.cipher = Cipher.getInstance(secretKey.getAlgorithm());
    }

    public SymmetricCryptography(byte[] secretKey, String algorithm)
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = new SecretKeySpec(secretKey, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
    }


    public SecretKeySpec getSecretKey() {
        return secretKey;
    }

    public byte[] encryptByteArray(byte[] msg)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        return Base64.encodeBase64(cipher.doFinal(msg));
    }

    public byte[] decryptByteArray(byte[] msg)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return cipher.doFinal(Base64.decodeBase64(msg));
    }

    public String encryptText(String msg)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    public String decryptText(String msg)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
    }

    public byte[] encryptFile(File f)
            throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        return this.cipher.doFinal(getBytesFromFile(f));
    }


    public byte[] decryptFile(File f)
            throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return this.cipher.doFinal(getBytesFromFile(f));
    }


    private byte[] getBytesFromFile(File f) throws IOException {
        try(FileInputStream in = new FileInputStream(f)) {
            byte[] input = new byte[(int) f.length()];
            if (in.read(input) == -1 || in.read(input) == 0)
                throw new IllegalArgumentException("The giving file '" + f.getPath() + "' is empty");
            in.close();
            return input;
        }
    }

    public byte[] decryptFile(byte[] f)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        return this.cipher.doFinal(f);
    }
}
