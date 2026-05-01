package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryRequest {
    private String category;
    private String categoryDescription;
    private Long sizeCategoryId;
    private Long parentCategoryId;
}

