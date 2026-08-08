package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariationResponseDTO {
    private Long id;
    private Long stock;
    private Double price;
    private Long sizeOptionId;
}
