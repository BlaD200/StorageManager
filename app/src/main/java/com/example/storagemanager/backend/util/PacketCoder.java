package com.example.storagemanager.backend.util;

import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.backend.entity.Message;
import com.example.storagemanager.backend.entity.Packet;
import com.github.snksoft.crc.CRC;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import static com.example.storagemanager.backend.entity.Packet.MAGIC_BYTE;

public class PacketCoder {

    public static Packet decodePacket(byte[] encodedPacket, SymmetricCryptography symmetricCryptography)
            throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException,
            IllegalStateException, IllegalArgumentException {
        // start magic byte 1
        if (encodedPacket[0] != MAGIC_BYTE)
            throw new IllegalArgumentException("Wrong packet start byte.");
        // org.vsynytsyn.client app id 1
        byte appID = encodedPacket[1];
        // message id 8
        Long messageID = ByteBuffer.wrap(encodedPacket, 2, 8)
                .order(ByteOrder.BIG_ENDIAN)
                .getLong();
        // message length 4
        int messageLength = ByteBuffer.wrap(encodedPacket, 10, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        // CRC16 for first 14 bytes
        short CRC16_1 = ByteBuffer.wrap(encodedPacket, 14, 2)
                .order(ByteOrder.BIG_ENDIAN)
                .getShort();
        short crc1Evaluated = (short) CRC.calculateCRC(CRC.Parameters.CRC16, Arrays.copyOf(encodedPacket, 14));
        if (CRC16_1 != crc1Evaluated)
            throw new IllegalStateException("CRC1 expected: " + crc1Evaluated + ", but was: " + CRC16_1);

        // packed message
        final byte[] packedMessage = new byte[messageLength];
        System.arraycopy(encodedPacket, 16, packedMessage, 0, messageLength);

        // CRC16 for packed message
        short CRC16_2 = ByteBuffer.wrap(encodedPacket, 16 + messageLength, 2)
                .order(ByteOrder.BIG_ENDIAN)
                .getShort();
        short crc2Evaluated = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packedMessage);
        if (CRC16_2 != crc2Evaluated) {
            throw new IllegalStateException("CRC2 expected: " + crc2Evaluated + ", but was: " + CRC16_2);
        }

        // unpackedMessage
        Message message;
        if (symmetricCryptography != null)
            message = decodeMessage(symmetricCryptography.decryptByteArray(packedMessage));
        else
            message = decodeMessage(packedMessage);

        return new Packet(appID, messageID, message);
    }

    public static byte[] packetToByteArray(Packet packet, SymmetricCryptography symmetricCryptography)
            throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        // message itself
        byte[] messageBytes;
        if (symmetricCryptography != null)
            messageBytes = symmetricCryptography.encryptByteArray(messageToByteArray(packet.getMessage()));
        else
            messageBytes = messageToByteArray(packet.getMessage());
        int messageLength = messageBytes.length;

        int packetServicePartLength = Byte.BYTES + Byte.BYTES + Long.BYTES + Integer.BYTES;
        byte[] packetServicePart = ByteBuffer.allocate(packetServicePartLength)
                .order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC_BYTE)
                .put(packet.getAppID())
                .putLong(packet.getMessageID())
                .putInt(messageLength)
                .array();

        // CRC16 for first 14 bytes
        short CRC16_1 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, packetServicePart);

        // CRC16 for message
        short CRC16_2 = (short) CRC.calculateCRC(CRC.Parameters.CRC16, messageBytes);

        int packetLength = packetServicePartLength + Short.BYTES * 2 + messageBytes.length;
        return ByteBuffer.allocate(packetLength)
                .order(ByteOrder.BIG_ENDIAN)
                .put(packetServicePart)
                .putShort(CRC16_1)
                .put(messageBytes)
                .putShort(CRC16_2)
                .array();
    }

    public static Message decodeMessage(byte[] packedMessage) {
        // command code 4
        int commandCode = ByteBuffer.wrap(packedMessage, 0, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        // user id 4
        int userID = ByteBuffer.wrap(packedMessage, 4, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .getInt();
        // message text
        final byte[] messageBytes = new byte[packedMessage.length - 8];
        System.arraycopy(packedMessage, 8, messageBytes, 0, messageBytes.length);
        String message = new String(messageBytes);
        return new Message(CommandType.getCommandType(commandCode), userID, message);
    }

    public static byte[] messageToByteArray(Message message) {
        byte[] messageBytes = message.getMessageText().getBytes();

        byte[] packedMessage = new byte[8 + messageBytes.length];
        // command code 4
        for (int i = 0; i < 4; i++) {
            packedMessage[i] = (byte) ((message.getCommandCode().getCode() >> (3 - i) * 8) & 0xFF);
        }
        // userID 4
        for (int i = 0; i < 4; i++) {
            packedMessage[4 + i] = (byte) ((message.getUserID() >> (3 - i) * 8) & 0xFF);
        }
        // message itself
        System.arraycopy(messageBytes, 0, packedMessage, 8, messageBytes.length);

        return packedMessage;
    }
}
