package com.product_service_api.Service;

import com.product_service_api.DTO.ProductItemResponse;
import com.product_service_api.Entity.ProductItem;

import java.util.List;

public interface ItemService {
    ProductItem updateProductItemStock(Long productItemId, Double originalPrice, Double salePrice);
    void deleteProductItem(Long productItemId);
    List<ProductItem> getAllProductItems();
    List<ProductItemResponse> getProductsPaginatedAndSortByPrice(int page, int size, Long productCategoryId, String orderDirection);
}
