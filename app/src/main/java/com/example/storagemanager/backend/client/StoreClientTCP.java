package com.example.storagemanager.backend.client;

import android.util.Log;

import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.backend.entity.Message;
import com.example.storagemanager.backend.entity.Packet;
import com.example.storagemanager.backend.network.INetwork;
import com.example.storagemanager.backend.network.TCPNetwork;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class StoreClientTCP extends BaseClient {

    private final int userID = 2;
    private final byte appID;
    Long messageID = 0L;
    private TCPNetwork network;
    private boolean isConnected;


    public StoreClientTCP() {
        appID = (byte) (Math.random() * 100);
    }


    public synchronized Message conversation(CommandType command, String message) {
        Message m = new Message(command, userID, message);
        Packet packet = new Packet(appID, messageID++, m);

        Log.w("StoreClient", "Sending: " + m);

        if (!isConnected)
            setupConnection();

        if (network != null) {
            try {
                return sendPacketForReply(packet).getMessage();
            } catch (IOException e) {
                Log.e("StoreClient", "CONNECTION LOST. TRYING RECONECT.");
                setupConnection();
                if (network != null)
                    try {
                        return sendPacketForReply(packet).getMessage();
                    } catch (IOException e2) {
                        Log.e("StoreClient", "CONNECTION LOST. TRYING RECONECT.");
                        return null;
                    }
                return null;
            }
        }
        return null;
    }


    private Packet sendPacketForReply(Packet packet) throws IOException {
        Log.w("StoreClient", "Sending(" + packet.getMessageID() + ")... ");
        network.sendPacket(packet);
        Log.w("StoreClient", "Sent.");

        try {
            Packet reply;
            reply = network.receivePacket();
            Log.w("StoreClient", "Server replied: ");
            Log.w("StoreClient", reply.getMessage().getMessageText());
            return reply;
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (IOException e) {
            --messageID;
            throw e;
        }
    }


    private synchronized void setupConnection() {
        int connectionAttempts = 10;
        while (connectionAttempts >= 0) {
            try {
                if (connect()) {
                    this.isConnected = true;
                    return;
                }
            } catch (IOException e) {
                Log.e("StoreClient", "COULD NOT ESTABLISH CONNECTION(" + (10 - connectionAttempts) + ").");
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            --connectionAttempts;
        }
        Log.e("StoreClient", "Closing client.");
        network = null;
        isConnected = false;
    }


    @Override
    public boolean connect() throws IOException {
        SymmetricCryptography symmetricCryptography;
//        Socket socket = new Socket("10.0.2.2", INetwork.TCP_PORT);
        Socket socket = new Socket("192.168.1.186", INetwork.TCP_PORT);

        // TODO FIX INSECURE CONNECTION
//        symmetricCryptography = configureSecureConnection(socket);
        symmetricCryptography = null;
        if (symmetricCryptography == null) {
//            Log.w("StoreClient", "Couldn't create secure connection. Aborting connection...");
            Log.w("StoreClient", "Couldn't create secure connection. USED UNSAFE ONE.");
//            return false;
        }
        Log.w("StoreClient", "Connection established!");
        this.network = new TCPNetwork(socket, symmetricCryptography);
        return true;
    }


    @Override
    public void disconnect() {
        if (network != null) {
            network.close();
            network = null;
            isConnected = false;
        }
    }
}
