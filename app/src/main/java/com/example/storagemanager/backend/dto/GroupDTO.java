package com.example.storagemanager.backend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private String name;
    private String description;
}
