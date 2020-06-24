package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.entities.GroupEntity;

import lombok.SneakyThrows;

public class GroupViewModel extends ViewModel {

    @SneakyThrows
    public GroupEntity getGroupByName(String name) {
        // TODO get group by name
        return new GroupEntity("group", "description");
    }

    public int getGroupTotalPrice(GroupEntity groupEntity) {
        // TODO get group total price
        return 1000;
    }
}
