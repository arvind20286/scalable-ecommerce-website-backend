package com.product_service_api.DTO;

import lombok.Data;

@Data
public class ProductImageResponse {
    private Long id;
    private String url;
    private int preference;
}
