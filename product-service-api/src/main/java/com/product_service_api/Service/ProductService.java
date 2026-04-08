package com.product_service_api.Service;

import java.io.IOException;
import java.util.List;

import com.product_service_api.DTO.BrandRequestDTO;
import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Entity.Brand;
import com.product_service_api.Entity.Product;
import com.product_service_api.Entity.ProductImages;
import com.product_service_api.Entity.Review;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    List<Product> findAllProducts();
    Product updateStockProduct(Long idProduct, Integer Stock);
    Product findProductById(Long idProduct);
    Product saveProduct(Product product, List<MultipartFile> images);
    List<Product> saveListProducts(List<Product> products);
    List<ProductImages> saveWithImage(Long productId, List<MultipartFile> images) throws IOException;
    Brand registerBrand(BrandRequestDTO brandRequestDTO);
    Review addProductReview(ReviewRequestDTO reviewRequestDTO);
}
