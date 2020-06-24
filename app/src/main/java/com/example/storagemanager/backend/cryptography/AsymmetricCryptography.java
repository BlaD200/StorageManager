package com.example.storagemanager.backend.cryptography;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class AsymmetricCryptography {

    public static final int KEY_LENGTH_256 = 256;
    public static final int KEY_LENGTH_512 = 512;
    public static final int KEY_LENGTH_1024 = 1024;
    public static final int KEY_LENGTH_2048 = 2048;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final Cipher cipher;


    public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        this(KEY_LENGTH_2048);
    }

    public AsymmetricCryptography(int keyLength) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeySpecException {
        this.cipher = Cipher.getInstance("RSA");
        KeysGenerator keysGenerator = new KeysGenerator(keyLength);

        PKCS8EncodedKeySpec specPrivate = new PKCS8EncodedKeySpec(keysGenerator.getPrivateKey().getEncoded());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = kf.generatePrivate(specPrivate);

        X509EncodedKeySpec specPublic = new X509EncodedKeySpec(keysGenerator.getPublicKey().getEncoded());
        this.publicKey = kf.generatePublic(specPublic);
    }


    public AsymmetricCryptography(PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
        this.privateKey = key;
        this.publicKey = null;
    }


    public AsymmetricCryptography(byte[] publicKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.cipher = Cipher.getInstance("RSA");
        this.publicKey = kf.generatePublic(spec);
        this.privateKey = null;
    }


    public AsymmetricCryptography(PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
        this.publicKey = key;
        this.privateKey = null;
    }


    public AsymmetricCryptography(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException,
            NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }


    public String encryptText(String msg)
            throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        if (this.publicKey == null)
            throw new NullPointerException("No public key was provided or generated.");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }


    public String decryptText(String msg)
            throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        if (this.privateKey == null)
            throw new NullPointerException("No private key was provided or generated.");
        this.cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
    }


    public byte[] encryptByteArray(byte[] msg)
            throws InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (this.publicKey == null)
            throw new NullPointerException("No public key was provided or generated.");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        return Base64.encodeBase64(this.cipher.doFinal(msg));
    }


    public byte[] decryptByteArray(byte[] msg)
            throws InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (this.privateKey == null)
            throw new NullPointerException("No private key was provided or generated.");
        this.cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        return cipher.doFinal(Base64.decodeBase64(msg));
    }
}
