package com.example.storagemanager.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoodEntity {
    private String name;
    private String group;
    private String description;
    private String producer;
    private Integer amount;
    private Integer price;
}
