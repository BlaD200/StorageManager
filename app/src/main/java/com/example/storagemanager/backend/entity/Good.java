package com.example.storagemanager.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Good {

    @NonNull
    private String name;
    private String description;
    private Double price;
    private Integer amount;
    private String producer;

    private String groupName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Good good = (Good) o;
        return name.equals(good.name) &&
                producer.equals(good.producer) &&
                groupName.equals(good.groupName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, producer, groupName);
    }


}

