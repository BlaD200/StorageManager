package com.example.storagemanager.backend.util;


import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.backend.entity.Packet;
import com.github.snksoft.crc.CRC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import lombok.Getter;

import static com.example.storagemanager.backend.entity.Packet.MAGIC_BYTE;

public class PacketExtractor {

    private final InputStream in;
    private final SymmetricCryptography symmetricCryptography;
    private final byte[] oneByte = new byte[1];

    @Getter
    private Packet lastPacket;
    @Getter
    private long lastMessageID = -1;
    @Getter
    private boolean isReceived;

    private final ByteArrayOutputStream packetBytes;
    private int messageLength;
    private byte[] messageBytes;
    private boolean packetIncomplete;
    private int state;
    private ByteBuffer byteBuffer;
    private ByteBuffer byteBufferForRead;


    public PacketExtractor(InputStream in, SymmetricCryptography symmetricCryptography) {
        this.in = in;
        this.symmetricCryptography = symmetricCryptography;
        packetBytes = new ByteArrayOutputStream();

        reset();
    }


    /**
     * The method tries to extract a packet from the InputStream.
     * If a packet was extracted successfully returns true, else false.
     * To handle the situation, when one packet was inside/before a part of another one,
     * the method could be called second times to try to extract the packet
     * using previously obtain data from InputStream.
     * Also could be used in a loop with <code>isReceived()</code> method to wait while packet will be received.
     *
     * @return true if, and only if a packet was successfully obtained and decrypted, and false otherwise.
     *
     * @see PacketExtractor#reset()
     */
    public boolean tryExtractNextPacket() throws IOException {
        /*
        magicByte       1   byte
        appID           1   byte
        messageID       8   long
        message length  4   int
        CRC16_1         2   short
         */
        try {
            byteBuffer = ByteBuffer.allocate(Long.BYTES);
            byteBufferForRead = ByteBuffer.allocate(0);
            state = -1;

            if (packetBytes.size() != 0) {
                do {
                    findMagicByte();
                    if (readNextBytes())
                        break;
                } while (byteBufferForRead.array().length != 0);
            } else if (!readNextBytes() || packetIncomplete)
                return false;

            if (packetIncomplete)
                return false;
            byte[] fullPacket = packetBytes.toByteArray();
            Packet packet;
            try {
                packet = PacketCoder.decodePacket(fullPacket, symmetricCryptography);
                lastPacket = packet;
                lastMessageID = packet.getMessageID();
                reset();
            } catch (IllegalArgumentException | IllegalStateException e) {
                isReceived = false;
                return false;
            }
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return isReceived;
    }


    /**
     * If it doesn't need to try to extract packet using cached data from a previous try call this method.
     *
     * @see PacketExtractor#tryExtractNextPacket()
     */
    public void reset() {
        if (packetBytes != null)
            packetBytes.reset();
        packetIncomplete = true;
        messageLength = -1;
    }


    private boolean checkCRC16Fail(byte[] calculate, short CRC16Received) {
        short crc1Evaluated = (short) CRC.calculateCRC(CRC.Parameters.CRC16, calculate);
        return CRC16Received != crc1Evaluated;
    }


    private void findMagicByte() {
        byte[] arr;
        if (byteBufferForRead.array().length != 0) {
            int length = byteBufferForRead.array().length;
            length -= packetBytes.size();
            if (length > 0) {
                arr = new byte[length];
                System.arraycopy(byteBufferForRead.array(), packetBytes.size(), arr, 0, length);
            } else
                arr = packetBytes.toByteArray();
        } else {
            arr = packetBytes.toByteArray();
        }
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == MAGIC_BYTE) {
                byte[] temp = new byte[arr.length - i];
                System.arraycopy(arr, i, temp, 0, arr.length - i);
                packetBytes.reset();
                byteBufferForRead = ByteBuffer.wrap(temp);
                return;
            }
        }
        packetBytes.reset();
        byteBufferForRead = ByteBuffer.allocate(0);
    }


    private boolean readNextBytes() throws IOException {
        if (byteBufferForRead.array().length > 0) {
            for (byte b : byteBufferForRead.array()) {
                if (isNextByteProcessed(b))
                    return false;
            }
        }
        while (packetIncomplete && (in.read(oneByte)) != -1) {
            if (isNextByteProcessed(oneByte[0]))
                return false;
        }
        return true;
    }


    private boolean isNextByteProcessed(byte nextByte) {
        if (MAGIC_BYTE.equals(nextByte) && packetBytes.size() == 0) {
            state = 0;
            byteBuffer = ByteBuffer.allocate(Byte.BYTES);
            packetBytes.write(nextByte);
        } else if (state != -1) {
            byteBuffer.put(nextByte);
            packetBytes.write(nextByte);
            switch (state) {
                // org.vsynytsyn.client messageID
                case 0:
                    if (!byteBuffer.hasRemaining()) {
                        // allocate for next part of Packet
                        byteBuffer = ByteBuffer.allocate(Long.BYTES);
                        state = 1;
                    }
                    break;
                // messageID -> messageLength
                case 1:
                    if (!byteBuffer.hasRemaining()) {
                        byteBuffer = ByteBuffer.allocate(Integer.BYTES);
                        state = 2;
                    }
                    break;
                // messageLength -> CRC16_1
                case 2:
                    if (!byteBuffer.hasRemaining()) {
                        messageLength = byteBuffer.getInt(0);
                        byteBuffer = ByteBuffer.allocate(Short.BYTES);
                        state = 3;
                    }
                    break;
                // CRC16_1 -> message
                case 3:
                    if (!byteBuffer.hasRemaining()) {
                        if (checkCRC16Fail(Arrays.copyOf(packetBytes.toByteArray(), 14),
                                byteBuffer.getShort(0))) {
                            return true;
                        }
                        byteBuffer = ByteBuffer.allocate(messageLength);
                        state = 4;
                    }
                    break;
                // message -> CRC16_2
                case 4:
                    if (!byteBuffer.hasRemaining()) {
                        messageBytes = byteBuffer.array();
                        byteBuffer = ByteBuffer.allocate(Short.BYTES);
                        state = 5;
                    }
                    break;
                case 5:
                    if (!byteBuffer.hasRemaining()) {
                        if (checkCRC16Fail(messageBytes, byteBuffer.getShort(0))) {
                            return true;
                        }
                        packetIncomplete = false;
                        isReceived = true;
                    }
                    break;
            }
        }
        return false;
    }
}
