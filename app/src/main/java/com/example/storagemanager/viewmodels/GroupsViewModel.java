package com.example.storagemanager.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.storagemanager.entities.GroupEntity;

import java.util.LinkedList;
import java.util.List;

import lombok.SneakyThrows;

public class GroupsViewModel extends ViewModel {

    @SneakyThrows
    public List<GroupEntity> getGroups(String query) {
        // TODO get groups
        List<GroupEntity> groupEntities = new LinkedList<>();

        groupEntities.add(new GroupEntity("First Ever Created Group",
                "And its awesome description!"));
        groupEntities.add(new GroupEntity("Second Ever Created Group",
                "And its awesome description"));
        groupEntities.add(new GroupEntity("Third Ever Created Group",
                "And its awesome description.."));
        groupEntities.add(new GroupEntity("Another created group",
                "And yet again another description"));
        groupEntities.add(new GroupEntity("Group Im getting tired of it",
                "Really tired description"));

        return groupEntities;
    }

    public void createGroup(GroupEntity groupEntity) {
        // TODO create group
    }

    public void updateGroup(GroupEntity groupEntity) {
        // TODO update group
    }

    public void deleteGroup(GroupEntity groupEntity) {
        // TODO delete group
    }
}
