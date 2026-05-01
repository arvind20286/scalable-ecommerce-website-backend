package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariationData {
    private Long stock;
    private Long sizeOptionId;
}
