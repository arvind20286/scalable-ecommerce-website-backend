package com.product_service_api.Service;

import com.product_service_api.DTO.*;
import com.product_service_api.Entity.AttributeOption;
import com.product_service_api.Entity.AttributeType;
import com.product_service_api.Entity.Colour;
import com.product_service_api.Entity.SizeCategory;
import com.product_service_api.Entity.SizeOption;

import java.util.List;

public interface AttributeService {
    Colour addColour(ColourRequest colourRequest);
    List<Colour> getAllColours();
    SizeCategory addSizeCategory(SizeCategoryRequest sizeCategoryRequest);
    List<SizeCategory> getAllSizeCategories();
    SizeOption addSizeOption(SizeOptionRequest sizeOptionRequest);
    List<SizeOption> getAllSizeOptions();
    AttributeType addAttributeType(AttributeTypeRequest attributeTypeRequest);
    List<AttributeType> getAllAttributeTypes();
    AttributeOption addAttributeOption(AttributeOptionRequest attributeOptionRequest);
    List<AttributeOption> getAllAttributeOptions();
}
