package com.example.storagemanager.backend.client;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.storagemanager.backend.cryptography.SymmetricCryptography;
import com.example.storagemanager.backend.dto.GoodDTO;
import com.example.storagemanager.backend.entity.CommandType;
import com.example.storagemanager.backend.entity.Message;
import com.example.storagemanager.backend.entity.Packet;
import com.example.storagemanager.backend.network.TCPNetwork;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.example.storagemanager.backend.network.INetwork.TCP_PORT;

public class StoreClientTCP extends BaseClient {

    private final int userID = 2;
    private final byte appID;
    private final ObjectMapper mapper;
    Long messageID = 0L;
    private TCPNetwork network;


    public StoreClientTCP() {
        mapper = new ObjectMapper();
        appID = (byte) (Math.random() * 100);
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void main(String[] args) {
//        StoreClientTCP storeClientTCP = new StoreClientTCP();
//        int connectionAttempts = 10;
//        while (connectionAttempts >= 0) {
//            try {
//                if (storeClientTCP.connect())
//                    try {
//                        connectionAttempts = 10;
//                        storeClientTCP.conversation();
//                    } catch (IOException e) {
//                        System.err.println("CONNECTION LOST. TRYING RECONECT.");
//                    }
//            } catch (IOException e) {
//                System.err.println("COULD NOT ESTABLISH CONNECTION(" + (10 - connectionAttempts) + ").");
//            } finally {
//                storeClientTCP.disconnect();
//            }
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException ignored) {
//            }
//            --connectionAttempts;
//        }
//        System.err.println("Closing org.vsynytsyn.client.");
//    }


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
            } catch (SocketTimeoutException e){
                throw  e;
            } catch (IOException e) {
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean connect() throws IOException {
        SymmetricCryptography symmetricCryptography;
        Socket socket = new Socket("192.168.1.186", 8080);

        symmetricCryptography = configureSecureConnection(socket);
        if (symmetricCryptography == null) {
            System.out.println("Couldn't create secure connection. Aborting connection...");
            return false;
        }
        System.out.println("Connection established!");
        network = new TCPNetwork(socket, symmetricCryptography);
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
