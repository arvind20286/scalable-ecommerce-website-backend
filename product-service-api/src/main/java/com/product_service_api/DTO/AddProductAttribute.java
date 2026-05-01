package com.product_service_api.DTO;

import lombok.Data;

@Data
public class AddProductAttribute {
    private Long productId;
    private ProductAttributeData productAttributeData;
}
