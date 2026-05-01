package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private Long brandId;
    private Long productCategoryId;
    private List<ProductItemData> productItemList;
    private List<ProductAttributeData> productAttributeDataList;
}
