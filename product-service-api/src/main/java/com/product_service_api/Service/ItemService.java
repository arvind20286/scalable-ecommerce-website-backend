package com.product_service_api.Service;

import com.product_service_api.DTO.ProductResponse;
import com.product_service_api.DTO.AddProductItem;
import com.product_service_api.Entity.ProductItem;

import java.util.List;

public interface ItemService {
    ProductItem updateProductItemPrice(Long productItemId, Double originalPrice, Double salePrice);
    void deleteProductItem(Long productItemId);
    List<ProductItem> getAllProductItems();
    List<ProductResponse> getProductsPaginatedAndSortByPrice(int page, int size, Long productCategoryId, String orderDirection);
    ProductItem addProductItemToProduct(AddProductItem addProductItem);
}
