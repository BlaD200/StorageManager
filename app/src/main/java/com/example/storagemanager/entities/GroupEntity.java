package com.example.storagemanager.entities;

import com.example.storagemanager.exceptions.EntityException;

import lombok.Data;

@Data
public class GroupEntity {

    private String name;
    private String description;

    public GroupEntity(String name, String description) throws EntityException {
        if (name == null || name.isEmpty())
            throw new EntityException("Group must have a name");
        if (description == null || description.isEmpty())
            throw new EntityException("Group must have a description");

        this.name = name;
        this.description = description;
    }
}
