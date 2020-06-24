package com.example.storagemanager.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Good {

    @NonNull
    private String name;
    private String description;
    private Double price;
    private Integer amount;
    private String producer;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Good that = (Good) o;
        return name.equals(that.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}

