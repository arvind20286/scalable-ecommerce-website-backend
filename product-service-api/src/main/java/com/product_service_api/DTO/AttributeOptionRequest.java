package com.product_service_api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOptionRequest {
    private Long attributeTypeId;
    private String attributeOptionName;
}
