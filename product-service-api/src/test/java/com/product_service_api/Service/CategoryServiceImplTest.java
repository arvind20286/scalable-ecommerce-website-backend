package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ProductCategoryRequest;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.ProductCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Unit Tests")
class CategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private ProductCategory testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new ProductCategory();
        testCategory.setId(1L);
        testCategory.setCategory("Electronics");
    }

    @Test
    @DisplayName("Should retrieve all product categories successfully")
    void testGetAllProductCategories_Success() {
        List<ProductCategory> categories = new ArrayList<>();
        categories.add(testCategory);

        when(productCategoryRepository.findAll()).thenReturn(categories);

        List<ProductCategory> result = categoryService.getAllProductCategories();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testCategory);
    }

    @Test
    @DisplayName("Should add product category successfully")
    void testAddProductCategory_Success() {
        ProductCategoryRequest request = new ProductCategoryRequest();
        request.setCategory("NewCategory");
        request.setCategoryDescription("Category Description");
        request.setSizeCategoryId(0L);
        request.setParentCategoryId(0L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productCategoryRepository.existsByCategory("NewCategory")).thenReturn(false);
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(testCategory);

        ProductCategory result = categoryService.addProductCategory(request);

        assertThat(result).isNotNull();
        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when category already exists")
    void testAddProductCategory_DuplicateCategory() {
        ProductCategoryRequest request = new ProductCategoryRequest();
        request.setCategory("Electronics");

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productCategoryRepository.existsByCategory("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.addProductCategory(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should update product category successfully")
    void testUpdateProductCategory_Success() {
        ProductCategoryRequest request = new ProductCategoryRequest();
        request.setCategory("UpdatedCategory");
        request.setCategoryDescription("Updated Description");
        request.setSizeCategoryId(0L);
        request.setParentCategoryId(0L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(testCategory);

        ProductCategory result = categoryService.updateProductCategory(1L, request);

        assertThat(result).isNotNull();
        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @Test
    @DisplayName("Should delete product category successfully")
    void testDeleteProductCategory_Success() {
        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        categoryService.deleteProductCategory(1L);

        verify(productCategoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to update product category")
    void testUpdateProductCategory_NotAdmin() {
        ProductCategoryRequest request = new ProductCategoryRequest();

        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> categoryService.updateProductCategory(1L, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to delete product category")
    void testDeleteProductCategory_NotAdmin() {
        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteProductCategory(1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating category with invalid parent")
    void testUpdateProductCategory_InvalidParent() {
        ProductCategoryRequest request = new ProductCategoryRequest();
        request.setCategory("UpdatedCategory");
        request.setParentCategoryId(999L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productCategoryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.updateProductCategory(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Parent Category");
    }
}
