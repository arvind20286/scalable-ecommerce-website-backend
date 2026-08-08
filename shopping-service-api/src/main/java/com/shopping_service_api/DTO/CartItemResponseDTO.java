package com.shopping_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long productId;
    private Long variationId;

    // Enriched data from Product Service
    private String productName;
    private String colorName;
    private String sizeName;
    private String imageUrl;

    // Data from Shopping Service (Cart DB)
    private Integer quantity;
    private Double price;
    private Double itemTotal;
}
