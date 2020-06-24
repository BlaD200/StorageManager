package com.example.storagemanager.backend.client;

import com.example.storagemanager.backend.dto.GoodDTO;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.backend.entity.Message;
import com.example.storagemanager.backend.entity.Packet;
import com.example.storagemanager.backend.network.UDPNetwork;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class StoreClientUDP extends BaseClient {

    private final int userID = 2;
    private final byte appID;
    private final ObjectMapper mapper;
    Long messageID = 0L;
    private UDPNetwork network;


    public StoreClientUDP() {
        mapper = new ObjectMapper();
        appID = (byte) (Math.random() * 100);
    }


    public static void main(String[] args) {
        StoreClientUDP storeClientTCP = new StoreClientUDP();
        int connectionAttempts = 10;
        while (connectionAttempts >= 0) {
            try {
                storeClientTCP.connect();
                try {
                    connectionAttempts = 10;
                    storeClientTCP.conversation();
                    break;
                } catch (SocketTimeoutException e){
                    --connectionAttempts;
                    System.err.println("TIMEOUT EXCEPTION. TRYING RECONNECT(" + (10 - connectionAttempts) + ").");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.err.println("COULD NOT ESTABLISH CONNECTION.");
            } finally {
                storeClientTCP.disconnect();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
            --connectionAttempts;
        }
        System.err.println("Closing org.vsynytsyn.client.");
    }


    public void conversation() throws IOException {
        int packedToSent = 200;
        while (packedToSent > 0) {
            GoodDTO goodDTO = GoodDTO.builder().name(String.valueOf(appID) + messageID).amount(100).price(40.0).build();
            Message message = new Message(CommandType.CREATE_GOOD, userID, mapper.writeValueAsString(goodDTO));
            Packet packet = new Packet(appID, messageID++, message);

            System.out.print(ANSI_BLUE + "Sending(" + packet.getMessageID() + ")... " + ANSI_RESET);
            network.sendPacket(packet);
            System.out.println(ANSI_BRIGHT_CYAN + "Sent." + ANSI_RESET);

            try {
                Packet reply;
                reply = network.receivePacket();
                System.out.print("Server replied: ");
                System.out.println(ANSI_YELLOW + reply.getMessage().getMessageText() + ANSI_RESET);
                --packedToSent;
            } catch (IOException e){
                --messageID;
                throw e;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        try {
            Message message = new Message(CommandType.NONE, userID, "CLOSE");
            Packet packet = new Packet(appID, messageID++, message);
            System.out.print(ANSI_BLUE + "Sending... " + packet.getMessageID() + ANSI_RESET);
            network.sendPacket(packet);
            System.out.println(ANSI_BRIGHT_CYAN + "Sent." + ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public boolean connect() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        network = new UDPNetwork(socket, false);
        network.setTimeout(2000);
        return true;
    }


    @Override
    public void disconnect() {
        if (network != null) {
            network.close();
            network = null;
        }
    }
}
