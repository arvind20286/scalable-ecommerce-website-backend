package com.product_service_api.Service;

import java.io.IOException;
import java.util.List;

import com.product_service_api.DTO.*;
import com.product_service_api.Entity.*;
import com.product_service_api.Repository.AttributeTypeRepository;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    // Product methods
    List<Product> findAllProducts();
    Product updateStockProduct(Long idProduct, Integer Stock);
    Product findProductById(Long idProduct);
    Product saveProduct(ProductRequest productRequest);
    Product updateProduct(Long productId, ProductRequest productRequest);
    void deleteProduct(Long productId);
    List<Product> saveListProducts(List<Product> products);
    
    // Product Image methods
    List<ProductImages> saveWithImage(Long productId, List<MultipartFile> images) throws IOException;
    List<ProductImages> getAllProductImages();
    
    // Brand methods
    Brand registerBrand(BrandRequestDTO brandRequestDTO);
    List<Brand> getAllBrands();
    
    // Colour methods
    Colour addColour(ColourRequest colourRequest);
    List<Colour> getAllColours();
    
    // Size Category methods
    SizeCategory addSizeCategory(SizeCategoryRequest sizeCategoryRequest);
    List<SizeCategory> getAllSizeCategories();
    
    // Size Option methods
    SizeOption addSizeOption(SizeOptionRequest sizeOptionRequest);
    List<SizeOption> getAllSizeOptions();
    
    // Attribute Type methods
    AttributeType addAttributeType(AttributeTypeRequest attributeTypeRequest);
    List<AttributeType> getAllAttributeTypes();
    
    // Attribute Option methods
    AttributeOption addAttributeOption(AttributeOptionRequest attributeOptionRequest);
    List<AttributeOption> getAllAttributeOptions();
    
    // Review methods
    Review addProductReview(ReviewRequestDTO reviewRequestDTO);
    List<Review> getAllReviews();
    
    ProductCategory addProductCategory(ProductCategoryRequest productCategoryRequest);
    ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategoryRequest);
    void deleteProductCategory(Long categoryId);
    List<ProductCategory> getAllProductCategories();
    
    // Product Attribute methods
    List<ProductAttribute> getAllProductAttributes();
    
    ProductItem updateProductItemStock(Long productItemId, Double originalPrice, Double salePrice);
    void deleteProductItem(Long productItemId);
    List<ProductItem> getAllProductItems();
    
    // Product Variation methods
    List<ProductVariation> getAllProductVariations();
}
