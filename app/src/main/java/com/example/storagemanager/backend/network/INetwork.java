package com.example.storagemanager.backend.network;

import com.example.storagemanager.backend.entity.Packet;

import java.io.IOException;

public interface INetwork {

    int TCP_PORT = 58901;
    int UDP_PORT = 2305;
    int timeout = 5_000;

    Packet receivePacket() throws IOException;

    boolean sendPacket(Packet packet) throws IOException;

    void close();
}
