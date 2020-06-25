package com.example.storagemanager.backend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodDTO {
    private String name;
    private Double price;
    private Integer amount;
    private String description;
    private String producer;

    private String group;
}
