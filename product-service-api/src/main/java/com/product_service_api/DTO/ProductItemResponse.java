package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponse {

    private Long id;
    private Long productId;
    private String productCode;
    private Double salePrice;
    private Double originalPrice;
    private Long colourId;
    private List<VariationData> variationDataList;
}