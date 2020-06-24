package com.example.storagemanager.backend.dto;

import com.example.storagemanager.backend.dto.criteria.Criteria;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingDTO {

    private Integer page;
    @NonNull
    private Integer size;

    private Criteria criteria;
}
