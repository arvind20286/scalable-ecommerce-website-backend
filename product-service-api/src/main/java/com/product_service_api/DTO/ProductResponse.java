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
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String brandName;
    private String categoryName;
    private List<ProductItemResponse> productItems;
    private List<ProductAttributeResponse> attributes;
    private List<ReviewResponse> reviews;
    private List<String> images;

}