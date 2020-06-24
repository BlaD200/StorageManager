package com.example.storagemanager.backend.client;

import com.example.storagemanager.backend.cryptography.AsymmetricCryptography;
import com.example.storagemanager.backend.cryptography.SymmetricCryptography;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public abstract class BaseClient implements IClient {

    public static final String ANSI_RESET  = "\u001B[0m";

    public static final String ANSI_BLACK  = "\u001B[30m";
    public static final String ANSI_RED    = "\u001B[31m";
    public static final String ANSI_GREEN  = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE   = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN   = "\u001B[36m";
    public static final String ANSI_WHITE  = "\u001B[37m";

    public static final String ANSI_BRIGHT_BLACK  = "\u001B[90m";
    public static final String ANSI_BRIGHT_RED    = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN  = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE   = "\u001B[94m";
    public static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
    public static final String ANSI_BRIGHT_CYAN   = "\u001B[96m";
    public static final String ANSI_BRIGHT_WHITE  = "\u001B[97m";

    public static final String[] FOREGROUNDS = {
            ANSI_BLACK, ANSI_RED, ANSI_GREEN, ANSI_YELLOW,
            ANSI_BLUE, ANSI_PURPLE, ANSI_CYAN, ANSI_WHITE,
            ANSI_BRIGHT_BLACK, ANSI_BRIGHT_RED, ANSI_BRIGHT_GREEN, ANSI_BRIGHT_YELLOW,
            ANSI_BRIGHT_BLUE, ANSI_BRIGHT_PURPLE, ANSI_BRIGHT_CYAN, ANSI_BRIGHT_WHITE
    };

    protected static SymmetricCryptography configureSecureConnection(Socket socket) throws IOException {
//        socket.setSoTimeout(INetwork.timeout);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();

        try {
            System.out.println("Receiving public key...");
            byte[] publicKey = new byte[2048];
            int actualLength;
            byte[] actualPublicKey;
            LocalTime time = LocalTime.now();
            while (true)
                if (in.available() > 0) {
                    actualLength = socket.getInputStream().read(publicKey);
                    actualPublicKey = new byte[actualLength];
                    System.arraycopy(publicKey, 0, actualPublicKey, 0, actualLength);
                    break;
                } else if (Duration.between(time, LocalTime.now()).getSeconds() > 1)
                    return null;
            System.out.println("Public key received.");

            System.out.println("Creating session key...");
            AsymmetricCryptography asymmetricCryptography = new AsymmetricCryptography(actualPublicKey);
            SymmetricCryptography symmetricCryptography = new SymmetricCryptography(256, "AES");
            System.out.println("Sending encrypted session key...");
            out.write(asymmetricCryptography.encryptByteArray(symmetricCryptography.getSecretKey().getEncoded()));
            out.flush();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
            System.out.println("Sending encrypted encryption algorithm...");
            out.write(asymmetricCryptography.encryptByteArray("AES".getBytes()));
            out.flush();

            // check if the server closed the connection.
            if (in.read() == -1)
                return null;
            else
                return symmetricCryptography;

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException |
                BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }

    }
}
