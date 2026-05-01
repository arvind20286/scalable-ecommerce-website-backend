package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {

    private Long id;
    private String sku;
    private Integer quantityInStock;
    private Double salePrice;
    private Double originalPrice;

}