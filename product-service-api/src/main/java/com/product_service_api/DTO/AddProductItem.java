package com.product_service_api.DTO;

import lombok.Data;

@Data
public class AddProductItem {
    private Long productId;
    private ProductItemData productItemData;
}
