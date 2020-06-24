package com.example.storagemanager.backend.network;


import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.backend.entity.Packet;
import com.example.storagemanager.backend.util.PacketCoder;
import com.example.storagemanager.backend.util.PacketExtractor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;

public class TCPNetwork implements INetwork {

    private final Socket socket;
    private final OutputStream outputStream;

    private final SymmetricCryptography cryptography;
    private final PacketExtractor packetExtractor;


    public TCPNetwork(Socket socket, SymmetricCryptography cryptography) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(timeout);
        this.outputStream = socket.getOutputStream();

        this.cryptography = cryptography;
        InputStream inputStream = socket.getInputStream();
        packetExtractor = new PacketExtractor(inputStream, cryptography);
    }


    @Override
    public Packet receivePacket() throws IOException {
        do {
            if (packetExtractor.tryExtractNextPacket())
                return packetExtractor.getLastPacket();
        } while (!packetExtractor.isReceived());
        return packetExtractor.getLastPacket();
    }


    @Override
    public boolean sendPacket(Packet packet) throws IOException {
        try {
            outputStream.write(PacketCoder.packetToByteArray(packet, cryptography));
            outputStream.flush();
        } catch (BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            return false;
        }
        return true;
    }


    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closed: " + socket);
    }
}
