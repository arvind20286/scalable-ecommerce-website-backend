package com.product_service_api.Service;

import com.product_service_api.DTO.ProductCategoryRequest;
import com.product_service_api.Entity.ProductCategory;

import java.util.List;

public interface CategoryService {
    ProductCategory addProductCategory(ProductCategoryRequest productCategoryRequest);
    ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategoryRequest);
    boolean deleteProductCategory(Long categoryId);
    List<ProductCategory> getAllProductCategories();
    List<ProductCategory> getParentProductCategories();
    ProductCategory getProductCategoryById(Long categoryId);
}
