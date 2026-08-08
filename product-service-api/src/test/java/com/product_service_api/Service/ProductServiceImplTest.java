package com.product_service_api.Service;

import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.DTO.ProductRequest;
import com.product_service_api.Entity.Brand;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductCategory;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Repository.*;
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
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Brand testBrand;
    private ProductCategory testCategory;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.setId(1L);
        testBrand.setBrandName("TestBrand");

        testCategory = new ProductCategory();
        testCategory.setId(1L);
        testCategory.setCategory("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setBrand(testBrand);
        testProduct.setProductCategory(testCategory);
    }

    @Test
    @DisplayName("Should retrieve all products successfully")
    void testFindAllProducts_Success() {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.findAllProducts();

        assertThat(result).isNotNull().hasSize(1).containsExactly(testProduct);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return null when exception occurs in findAllProducts")
    void testFindAllProducts_Exception() {
        when(productRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        List<Product> result = productService.findAllProducts();

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should retrieve product by ID successfully")
    void testFindProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.findProductById(1L);

        assertThat(result).isNotNull().isEqualTo(testProduct);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when product not found")
    void testFindProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Product result = productService.findProductById(999L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should save product successfully with valid data")
    void testSaveProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("New Product Description");
        request.setBrandId(1L);
        request.setProductCategoryId(1L);
        request.setProductItemList(new ArrayList<>());
        request.setProductAttributeDataList(new ArrayList<>());

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(brandRepository.existsById(1L)).thenReturn(true);
        when(productCategoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(brandRepository.getReferenceById(1L)).thenReturn(testBrand);
        when(productCategoryRepository.getReferenceById(1L)).thenReturn(testCategory);

        Product result = productService.saveProduct(request);

        assertThat(result).isNotNull().isEqualTo(testProduct);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is not admin")
    void testSaveProduct_NotAdmin() {
        ProductRequest request = new ProductRequest();

        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> productService.saveProduct(request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User not authorised to perform the action");
    }

    @Test
    @DisplayName("Should throw BadRequestException when brand does not exist")
    void testSaveProduct_BrandNotFound() {
        ProductRequest request = new ProductRequest();
        request.setBrandId(999L);
        request.setProductCategoryId(1L);

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(brandRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> productService.saveProduct(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Either Brand or Product Category does not exist");
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setBrandId(1L);
        request.setProductCategoryId(1L);
        request.setProductItemList(new ArrayList<>());
        request.setProductAttributeDataList(new ArrayList<>());

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(brandRepository.existsById(1L)).thenReturn(true);
        when(productCategoryRepository.existsById(1L)).thenReturn(true);
        when(brandRepository.getReferenceById(1L)).thenReturn(testBrand);
        when(productCategoryRepository.getReferenceById(1L)).thenReturn(testCategory);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(1L, request);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when updating non-existent product")
    void testUpdateProduct_NotFound() {
        ProductRequest request = new ProductRequest();

        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(999L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not exist");
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct_Success() {
        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    @DisplayName("Should throw BadRequestException when deleting non-existent product")
    void testDeleteProduct_NotFound() {
        when(authServiceClient.isAdmin()).thenReturn(true);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to update product")
    void testUpdateProduct_NotAdmin() {
        ProductRequest request = new ProductRequest();

        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> productService.updateProduct(1L, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when non-admin tries to delete product")
    void testDeleteProduct_NotAdmin() {
        when(authServiceClient.isAdmin()).thenReturn(false);

        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(ForbiddenException.class);
    }
}
