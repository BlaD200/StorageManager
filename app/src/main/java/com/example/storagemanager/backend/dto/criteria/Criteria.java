package com.example.storagemanager.backend.dto.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {
    String name;
    String producerName;

    Integer amountFrom;
    Integer amountTo;

    Integer priceFrom;
    Integer priceTo;

    String groupName;
}
