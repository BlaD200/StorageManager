package com.example.storagemanager.backend.entity;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {

    UNKNOWN(-1),
    NONE(0),

    LOGIN(1),

    CREATE_GOOD(2),
    DELETE_GOOD(3),
    GET_GOOD(4),
    GET_GOODS(5),

    GET_PRODUCERS(16),

    GET_GOOD_AMOUNT(6),
    RETIRE_GOOD_AMOUNT(7),
    PUT_GOOD_AMOUNT(8),
    SET_GOOD_PRICE(9),

    CREATE_GROUP(10),
    DELETE_GROUP(11),
    GET_GROUP(12),
    GET_GROUPS(13),

    GET_GROUP_TOTAL(14),
    ADD_GOOD_TO_GROUP(15);


    private static final Map<Integer, CommandType> intToTypeMap = new HashMap<>();


    static {
        for (CommandType type : CommandType.values()) {
            intToTypeMap.put(type.code, type);
        }
    }


    private final int code;


    CommandType(int code) {
        this.code = code;
    }


    public static CommandType getCommandType(int i) {
        CommandType type = intToTypeMap.get(i);
        if (type == null)
            return CommandType.UNKNOWN;
        return type;
    }


    public int getCode() {
        return code;
    }
}
