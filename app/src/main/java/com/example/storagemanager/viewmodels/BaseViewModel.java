package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.backend.client.StoreClientTCP;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

public class BaseViewModel extends ViewModel {

    protected StoreClientTCP clientConnection = new StoreClientTCP();
    @Getter
    protected ObjectMapper mapper = new ObjectMapper();
}
