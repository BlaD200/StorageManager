package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.entities.GoodEntity;

import lombok.SneakyThrows;

public class GoodViewModel extends ViewModel {

    @SneakyThrows
    public GoodEntity getGoodByName(String name) {
        // TODO get good by name
        return new GoodEntity("name", "group", "description",
                "producer", 100, 100);
    }
}
