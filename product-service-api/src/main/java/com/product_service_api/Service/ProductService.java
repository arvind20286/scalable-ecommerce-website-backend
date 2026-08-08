package com.product_service_api.Service;

import com.product_service_api.DTO.ProductRequest;
import com.product_service_api.DTO.VariationResponseDTO;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductImages;
import com.product_service_api.Entity.ProductVariation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> findAllProducts();
    Product findProductById(Long idProduct);
    Product saveProduct(ProductRequest productRequest);
    Product saveProductWithImages(String productRequest, Map<String, MultipartFile> imageFiles) throws IOException;
    Product updateProduct(Long productId, ProductRequest productRequest);
    void deleteProduct(Long productId);
    List<Product> saveListProducts(List<Product> products);
//    List<ProductImages> saveWithImage(Long productId, List<MultipartFile> images) throws IOException;
    Product updateStockProduct(Long idProduct, Integer sale);
    List<ProductImages> getAllProductImages();
    List<ProductVariation> getAllProductVariations();

    VariationResponseDTO findProductVariationById(Long variationId);
}
