package com.shopping_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationDTO {
    private Long id;
    private Long stock;
    private Double price;
    private Long sizeOptionId;
}
