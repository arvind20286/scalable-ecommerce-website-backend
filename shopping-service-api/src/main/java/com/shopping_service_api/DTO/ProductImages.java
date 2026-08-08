package com.shopping_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImages {
    private Long id;
    private String url;
    private ProductDTO product;
    private int preference;
}
