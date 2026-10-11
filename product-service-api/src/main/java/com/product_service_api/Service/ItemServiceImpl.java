package com.product_service_api.Service;

import com.product_service_api.Authorization.GatewayIdentity;
import com.product_service_api.DTO.ProductAttributeResponse;
import com.product_service_api.DTO.ProductImageResponse;
import com.product_service_api.DTO.ProductItemResponse;
import com.product_service_api.DTO.ProductResponse;
import com.product_service_api.DTO.ReviewResponse;
import com.product_service_api.DTO.VariationData;
import com.product_service_api.DTO.AddProductItem;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Entity.ProductItem;
import com.product_service_api.Entity.ProductVariation;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductItemRepository;
import com.product_service_api.Repository.ProductCategoryRepository;
import com.product_service_api.Repository.ProductRepository;
import com.product_service_api.Repository.ColourRepository;
import com.product_service_api.Repository.SizeOptionRepository;
import com.product_service_api.Repository.ProductVariationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ColourRepository colourRepository;
    private final SizeOptionRepository sizeOptionRepository;
    private final ProductVariationRepository productVariationRepository;
    private final GatewayIdentity gatewayIdentity;

    private void checkOwnership(Product product) {
        boolean isAdmin = gatewayIdentity.isAdmin();
        if (!isAdmin && !gatewayIdentity.isSeller()) {
            throw new ForbiddenException("User is not a seller");
        }
        Long currentUserId = gatewayIdentity.getUserId();
        if (!isAdmin && !Objects.equals(product.getSellerId(), currentUserId)) {
            throw new ForbiddenException("User not authorised to perform the action");
        }
    }

    @Override
    @Transactional
    public ProductItem updateProductItemPrice(Long productItemId, Double originalPrice, Double salePrice) {
        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));

        checkOwnership(productItem.getProduct());

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
        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new BadRequestException("Product Item with id " + productItemId + " does not exist"));

        checkOwnership(productItem.getProduct());

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
    @Transactional
    public List<ProductResponse> getProductsPaginatedAndSortByPrice(int page, int size, Long productCategoryId, String orderDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(orderDirection)
                .orElse(Sort.Direction.ASC);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;
        if (productCategoryId == null) {
            products = direction.isAscending()
                    ? productRepository.findPageSortedByLowestSalePriceAsc(pageable)
                    : productRepository.findPageSortedByLowestSalePriceDesc(pageable);
        } else {
            List<Long> categoryIds = getCategoryTreeIds(productCategoryId);
            products = direction.isAscending()
                    ? productRepository.findPageByCategoryIdsSortedByLowestSalePriceAsc(categoryIds, pageable)
                    : productRepository.findPageByCategoryIdsSortedByLowestSalePriceDesc(categoryIds, pageable);
        }
        return products.getContent().stream()
                .map(this::toProductResponse)
                .toList();
    }

    private List<Long> getCategoryTreeIds(Long rootCategoryId) {
        List<ProductCategory> categories = productCategoryRepository.findAllByParentCategoryIsNotNull();
        Map<Long, List<Long>> childrenByParentId = new HashMap<>();
        for (ProductCategory category : categories) {
            Long parentId = category.getParentCategory().getId();
            childrenByParentId.computeIfAbsent(parentId, ignored -> new ArrayList<>()).add(category.getId());
        }

        Set<Long> categoryIds = new LinkedHashSet<>();
        List<Long> pendingCategoryIds = new ArrayList<>();
        pendingCategoryIds.add(rootCategoryId);
        for (int index = 0; index < pendingCategoryIds.size(); index++) {
            Long categoryId = pendingCategoryIds.get(index);
            if (categoryIds.add(categoryId)) {
                pendingCategoryIds.addAll(childrenByParentId.getOrDefault(categoryId, List.of()));
            }
        }
        return new ArrayList<>(categoryIds);
    }

    private ProductResponse toProductResponse(Product product) {
        List<ProductItemResponse> items = product.getProductItems().stream()
                .map(this::toProductItemResponse)
                .toList();
        List<ProductAttributeResponse> attributes = product.getProductAttributeList().stream()
                .map(productAttribute -> ProductAttributeResponse.builder()
                        .attributeName(productAttribute.getAttributeOption().getAttributeType().getAttributeName())
                        .value(productAttribute.getAttributeOption().getName())
                        .build())
                .toList();
        List<ReviewResponse> reviews = product.getReviews().stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .build())
                .toList();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .brandName(product.getBrand() == null ? null : product.getBrand().getBrandName())
                .categoryName(product.getProductCategory() == null ? null : product.getProductCategory().getCategory())
                .productItems(items)
                .attributes(attributes)
                .reviews(reviews)
                .build();
    }

    private ProductItemResponse toProductItemResponse(ProductItem productItem) {
        List<ProductImageResponse> images = productItem.getImages().stream()
                .map(image -> {
                    ProductImageResponse imageResponse = new ProductImageResponse();
                    imageResponse.setId(image.getId());
                    imageResponse.setUrl(image.getUrl());
                    imageResponse.setPreference(image.getPreference());
                    return imageResponse;
                })
                .toList();
        List<VariationData> variationDataList = productItem.getProductVariationList().stream()
                .map(productVariation -> new VariationData(
                        productVariation.getStock(),
                        productVariation.getSizeOption().getId()
                ))
                .toList();

        return ProductItemResponse.builder()
                .id(productItem.getId())
                .productId(productItem.getProduct().getId())
                .productCode(productItem.getProductCode())
                .salePrice(productItem.getSalePrice())
                .originalPrice(productItem.getOriginalPrice())
                .colourId(productItem.getColour().getId())
                .productImageResponseList(images)
                .variationDataList(variationDataList)
                .build();
    }

    @Override
    @Transactional
    public ProductItem addProductItemToProduct(AddProductItem addProductItem) {
        Long productId = addProductItem.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product with id " + productId + " does not exist"));

        checkOwnership(product);

        var itemData = addProductItem.getProductItemData();

        if (!colourRepository.existsById(itemData.getColourId())) {
            throw new BadRequestException("Colour with id " + itemData.getColourId() + " does not exist");
        }

        ProductItem productItem = new ProductItem();
        productItem.setOriginalPrice(itemData.getOriginalPrice());
        productItem.setSalePrice(itemData.getSalePrice());
        productItem.setProductCode(itemData.getProductCode());
        productItem.setProduct(product);
        productItem.setColour(colourRepository.getReferenceById(itemData.getColourId()));
        productItem = productItemRepository.save(productItem);

        List<ProductVariation> variations = new ArrayList<>();
        for (VariationData variationData : itemData.getVariationDataList()) {
            if (!sizeOptionRepository.existsById(variationData.getSizeOptionId())) {
                throw new BadRequestException("Size Option with id " + variationData.getSizeOptionId() + " does not exist");
            }

            ProductVariation productVariation = new ProductVariation();
            productVariation.setStock(variationData.getStock());
            productVariation.setProductItem(productItem);
            productVariation.setSizeOption(sizeOptionRepository.getReferenceById(variationData.getSizeOptionId()));
            variations.add(productVariationRepository.save(productVariation));
        }

        productItem.setProductVariationList(variations);
        return productItemRepository.save(productItem);
    }
}
