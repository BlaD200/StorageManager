package com.example.storagemanager.backend.entity;

import androidx.annotation.NonNull;

import lombok.Data;

@Data
public class Packet {

    public final static Byte MAGIC_BYTE = 0x13;
    private final Byte appID;
    private final Long messageID;
    private final Message message;


    @NonNull
    @Override
    public String toString() {
        return "Packet{" +
                "appID=" + appID +
                ", messageID=" + messageID +
                ", message=" + message +
                '}';
    }
}
