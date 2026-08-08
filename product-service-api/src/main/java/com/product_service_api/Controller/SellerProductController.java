package com.product_service_api.Controller;

import com.product_service_api.DTO.BrandRequestDTO;
import com.product_service_api.DTO.ProductRequest;
import com.product_service_api.Entity.Product;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Service.ProductService;
import com.product_service_api.Service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class SellerProductController {

    private final ProductService productService;
    private final SellerProductService sellerProductService;

    @PostMapping
    public ResponseEntity<?> saveProduct(@RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.saveProduct(productRequest), HttpStatus.OK);
    }

//    @PostMapping("/save-with-images")
//    public ResponseEntity<?> saveWithImage(@RequestParam Long productId, @RequestParam("images") List<MultipartFile> images) throws IOException {
//        return new ResponseEntity<>(productService.saveWithImage(productId, images), HttpStatus.OK);
//    }

    @PostMapping("/save-with-images-json")
    public ResponseEntity<?> saveProductWithImagesJson(@RequestPart("product") String productRequest,
                                                       @RequestPart("images") List<MultipartFile> images) throws IOException {
        Map<String, MultipartFile> imageFiles = new HashMap<>();
        for (MultipartFile image : images) {
            imageFiles.put(image.getOriginalFilename(), image);
        }
        return new ResponseEntity<>(productService.saveProductWithImages(productRequest, imageFiles), HttpStatus.OK);
    }

    @PostMapping("/save/list")
    public ResponseEntity<?> saveListProducts(@RequestBody List<Product> products) {
        return new ResponseEntity<>(productService.saveListProducts(products), HttpStatus.OK);
    }

    @PostMapping("/register/brand")
    public ResponseEntity<?> registerBrand(@RequestBody BrandRequestDTO brandRequestDTO) {
        return new ResponseEntity<>(sellerProductService.registerBrand(brandRequestDTO), HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") Long productId, @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.updateProduct(productId, productRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }
}
