package com.example.storagemanager.backend.entity;

import lombok.Data;

@Data
public class Message {

    private final CommandType commandCode;
    private final int userID;
    private final String messageText;


    @Override
    public String toString() {
        return "Message" +
                "{commandCode=" + commandCode +
                ", userID=" + userID +
                ", message='" + messageText + '\'' +
                '}';
    }
}
