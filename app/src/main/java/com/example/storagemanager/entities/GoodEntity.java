package com.example.storagemanager.entities;

import com.example.storagemanager.backend.entity.Good;
import com.example.storagemanager.exceptions.EntityException;

import lombok.Data;

@Data
public class GoodEntity {

    private String name;
    private String group;
    private String description;
    private String producer;
    private Integer amount;
    private Integer price;

    public GoodEntity(String name, String group, String description,
                      String producer, Integer amount, Integer price) throws EntityException {
        if (name == null || name.isEmpty())
            throw new EntityException("Good must have a name");
        if (group == null || group.isEmpty())
            throw new EntityException("Good must have a group");
        if (description == null || description.isEmpty())
            throw new EntityException("Good must have a description");
        if (producer == null || producer.isEmpty())
            throw new EntityException("Good must have a producer");
        if (amount == null)
            throw new EntityException("Good must have initial amount");
        if (price == null)
            throw new EntityException("Good must have initial price");

        this.name = name;
        this.group = group;
        this.description = description;
        this.producer = producer;
        this.amount = amount;
        this.price = price;
    }

    public GoodEntity(Good good) throws EntityException {
        this(good.getName(), good.getGroupName(), good.getDescription(),
                good.getProducer(), good.getAmount(), good.getPrice().intValue());
    }
}
