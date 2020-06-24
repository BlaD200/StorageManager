package com.example.storagemanager.backend.network;

import com.example.storagemanager.backend.entity.Packet;
import com.example.storagemanager.backend.util.PacketCoder;

import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;

public class UDPNetwork implements INetwork {

    private final DatagramSocket socket;
    private final boolean isServer;
    @Getter
    private int incomingPort = -1;


    public UDPNetwork(DatagramSocket socket, boolean isServer) {
        this.socket = socket;
        this.isServer = isServer;
    }


    public UDPNetwork(int replyPort) throws SocketException {
        socket = new DatagramSocket();
        isServer = true;
        incomingPort = replyPort;
    }


    public void setTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }


    @Override
    public Packet receivePacket() throws IOException {
        while (true) {
            byte[] maxPacketBuffer = new byte[Short.MAX_VALUE];

            DatagramPacket datagramPacket = new DatagramPacket(maxPacketBuffer, maxPacketBuffer.length);
            socket.receive(datagramPacket);

            ByteBuffer byteBuffer = ByteBuffer.wrap(maxPacketBuffer);
            int wLen = byteBuffer.getInt(10);

            byte[] fullPacket = new byte[16 + wLen + 2];
            byteBuffer.get(fullPacket, 0, fullPacket.length);

            incomingPort = datagramPacket.getPort();
            try {
                return PacketCoder.decodePacket(fullPacket, null);
            } catch (BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean sendPacket(Packet packet) throws IOException {
        if (isServer && incomingPort == -1)
            throw new IOException("Cannot send reply before receiving data.");
        try {
            InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
            byte[] packetBytes = PacketCoder.packetToByteArray(packet, null);

            DatagramPacket datagramPacket;
            if (isServer)
                datagramPacket = new DatagramPacket(packetBytes, packetBytes.length, inetAddress, incomingPort);
            else
                datagramPacket = new DatagramPacket(packetBytes, packetBytes.length, inetAddress, UDP_PORT);

            socket.send(datagramPacket);

        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            return false;
        }
        return true;
    }


    @Override
    public void close() {
        socket.close();
    }
}
