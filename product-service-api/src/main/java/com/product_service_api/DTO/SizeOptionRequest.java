package com.product_service_api.DTO;

import com.product_service_api.Entity.SizeCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeOptionRequest {
    private String sizeName;
    private Integer sizeOrder;
    private Long sizeCategoryId;
}
