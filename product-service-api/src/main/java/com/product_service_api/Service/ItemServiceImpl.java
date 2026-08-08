package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ProductItemResponse;
import com.product_service_api.DTO.VariationData;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductItem;
import com.product_service_api.Entity.ProductVariation;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductItemRepository;
import com.product_service_api.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public ProductItem updateProductItemStock(Long productItemId, Double originalPrice, Double salePrice) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));

        if (originalPrice != null) {
            productItem.setOriginalPrice(originalPrice);
        }
        if (salePrice != null) {
            productItem.setSalePrice(salePrice);
        }

        return productItemRepository.save(productItem);
    }

    @Override
    @Transactional
    public void deleteProductItem(Long productItemId) {
        if (!authServiceClient.isAdmin()) {
            throw new ForbiddenException("User not authorised to perform the action");
        }

        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));

        productItemRepository.delete(productItem);
    }

    @Override
    public List<ProductItem> getAllProductItems() {
        try {
            return productItemRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ProductItemResponse> getProductsPaginatedAndSortByPrice(int page, int size, Long productCategoryId, String orderDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(orderDirection)
                .orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "salePrice"));
        List<Product> products = productRepository.findAllByProductCategoryId(productCategoryId);
        List<ProductItem> productItemList = productItemRepository.findAllByProductIdIn(products.stream().map(Product::getId).toList(), pageable);
        List<ProductItemResponse> productItemResponseList = new ArrayList<>();
        for (ProductItem productItem : productItemList) {
            List<VariationData> variationDataList = new ArrayList<>();
            for (ProductVariation productVariation : productItem.getProductVariationList()) {
                VariationData variationData = new VariationData();
                variationData.setStock(productVariation.getStock());
                variationData.setSizeOptionId(productVariation.getSizeOption().getId());
                variationDataList.add(variationData);
            }
            productItemResponseList.add(new ProductItemResponse(productItem.getId(), productItem.getProduct().getId(), productItem.getProductCode(), productItem.getSalePrice(), productItem.getOriginalPrice(), productItem.getColour().getId(), variationDataList));
        }
        return productItemResponseList;
    }
}
