package com.example.storagemanager.backend.client;

import java.io.IOException;

public interface IClient {

    boolean connect() throws IOException;

    void disconnect();
}
