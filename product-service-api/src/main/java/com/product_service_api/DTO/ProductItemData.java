package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemData {
    private Double originalPrice;
    private Double salePrice;
    private String productCode;
    private Long colourId;
    private List<VariationData> variationDataList;
}
