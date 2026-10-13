package com.product_service_api.Service;

import com.product_service_api.Authorization.GatewayIdentity;
import com.product_service_api.DTO.ProductResponse;
import com.product_service_api.Entity.Brand;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Entity.ProductItem;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductCategoryRepository;
import com.product_service_api.Repository.ProductItemRepository;
import com.product_service_api.Repository.ProductRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemServiceImpl Unit Tests")
class ItemServiceImplTest {

    @Mock
    private ProductItemRepository productItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private GatewayIdentity gatewayIdentity;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("Should update product item stock successfully")
    void testUpdateProductItemPrice_Success() {
        ProductItem item = new ProductItem();
        item.setId(1L);
        item.setOriginalPrice(100.0);
        item.setSalePrice(80.0);

        when(gatewayIdentity.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(productItemRepository.save(any(ProductItem.class))).thenReturn(item);

        ProductItem result = itemService.updateProductItemPrice(1L, 120.0, 90.0);

        assertThat(result).isNotNull();
        assertThat(result.getOriginalPrice()).isEqualTo(120.0);
        assertThat(result.getSalePrice()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("Should delete product item successfully")
    void testDeleteProductItem_Success() {
        ProductItem item = new ProductItem();
        item.setId(1L);

        when(gatewayIdentity.isAdmin()).thenReturn(true);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteProductItem(1L);

        verify(productItemRepository, times(1)).delete(item);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to update product item")
    void testUpdateProductItemPrice_NotAdmin() {
        Product product = new Product();
        product.setSellerId(2L);
        ProductItem item = new ProductItem();
        item.setProduct(product);

        when(gatewayIdentity.isAdmin()).thenReturn(false);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateProductItemPrice(1L, 100.0, 80.0))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to delete product item")
    void testDeleteProductItem_NotAdmin() {
        Product product = new Product();
        product.setSellerId(2L);
        ProductItem item = new ProductItem();
        item.setProduct(product);

        when(gatewayIdentity.isAdmin()).thenReturn(false);
        when(productItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.deleteProductItem(1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating non-existent product item")
    void testUpdateProductItemPrice_NotFound() {
        when(productItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateProductItemPrice(999L, 100.0, 80.0))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when deleting non-existent product item")
    void testDeleteProductItem_NotFound() {
        when(productItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteProductItem(999L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should page products directly from the repository and map them to ProductResponse")
    void testGetProductsPaginatedAndSortByPrice() {
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Description")
                .brand(new Brand(2L, "Brand", null))
                .productCategory(new ProductCategory(3L, "Electronics", null, null, null, null))
                .productItems(List.of())
                .productAttributeList(List.of())
                .reviews(List.of())
                .build();
        PageRequest pageable = PageRequest.of(0, 5);
        when(productRepository.findPageByCategoryIdsSortedByLowestSalePriceAsc(List.of(3L), pageable))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        List<ProductResponse> result = itemService.getProductsPaginatedAndSortByPrice(0, 5, 3L, "ASC");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(0).getBrandName()).isEqualTo("Brand");
        assertThat(result.get(0).getCategory().getId()).isEqualTo(3L);
        assertThat(result.get(0).getCategory().getName()).isEqualTo("Electronics");
        assertThat(result.get(0).getProductItems()).isEmpty();
        verify(productRepository).findPageByCategoryIdsSortedByLowestSalePriceAsc(List.of(3L), pageable);
        verify(productItemRepository, never()).findAllByProductIdIn(anyList(), any());
    }

    @Test
    @DisplayName("Should include descendant category IDs when filtering products")
    void testPaginationIncludesDescendantCategoryIds() {
        ProductCategory childCategory = new ProductCategory();
        childCategory.setId(4L);
        ProductCategory parentCategory = new ProductCategory();
        parentCategory.setId(3L);
        childCategory.setParentCategory(parentCategory);
        ProductCategory grandchildCategory = new ProductCategory();
        grandchildCategory.setId(5L);
        grandchildCategory.setParentCategory(childCategory);
        when(productCategoryRepository.findAllByParentCategoryIsNotNull())
                .thenReturn(List.of(childCategory, grandchildCategory));
        PageRequest pageable = PageRequest.of(0, 5);
        when(productRepository.findPageByCategoryIdsSortedByLowestSalePriceAsc(List.of(3L, 4L, 5L), pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        itemService.getProductsPaginatedAndSortByPrice(0, 5, 3L, "ASC");

        verify(productRepository).findPageByCategoryIdsSortedByLowestSalePriceAsc(List.of(3L, 4L, 5L), pageable);
    }
}
