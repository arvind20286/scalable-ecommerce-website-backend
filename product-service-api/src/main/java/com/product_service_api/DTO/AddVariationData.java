package com.product_service_api.DTO;

import lombok.Data;

@Data
public class AddVariationData{
    Long productItemId;
    VariationData variationData;
}
