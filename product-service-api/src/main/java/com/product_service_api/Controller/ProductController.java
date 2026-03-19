package com.product_service_api.Controller;

import java.util.List;

import com.product_service_api.DTO.ReviewRequestDTO;
import com.product_service_api.Entity.ProductImages;
import com.product_service_api.Entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.Entity.Product;
import com.product_service_api.Service.ProductService;
import com.product_service_api.DTO.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthServiceClient authServiceClient;

    @GetMapping
    public ResponseEntity<?> findAllProducts() {
        if (authServiceClient.IsUser()) {
            try {
                System.out.println("Got Save request");
                return new ResponseEntity<>(productService.findAllProducts(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Your are not user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PutMapping("/update/stock/{idProduct}")
    public ResponseEntity<?> updateStockProduct(@PathVariable("idProduct") Long idProduct, @RequestBody Integer Stock) {
        if (authServiceClient.IsUser()) {
            try {
                return new ResponseEntity<>(productService.updateStockProduct(idProduct, Stock), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Your are not user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveProduct(@ModelAttribute Product product, @RequestParam("images-files") List<MultipartFile> images) {
        if (authServiceClient.isAdmin()) {
            try {
                System.out.println("got save req");
                return new ResponseEntity<>(productService.saveProduct(product, images), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
            }
        } else {
            return null;
        }
    }

    @PostMapping("/save-with-images")
    public ResponseEntity<?> saveWithImage(Long productId, @RequestParam("images") List<MultipartFile> images){
        if (authServiceClient.isAdmin()){
            try{
                return new ResponseEntity<>(productService.saveWithImage(productId, images), HttpStatus.OK);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @PostMapping("/save/list")
    public ResponseEntity<?> saveListProducts(@RequestBody List<Product> products) {
        if (authServiceClient.isAdmin()) {
            try {
                return new ResponseEntity<>(productService.saveListProducts(products), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
            }
        } else {
            return null;
        }
    }

    @GetMapping("/{idProduct}")
    public ResponseEntity<?> findProductById(@PathVariable("idProduct") Long idProduct) {
        if (authServiceClient.IsUser()) {
            try {
                return new ResponseEntity<>(productService.findProductById(idProduct), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Your are not user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/review/add")
    public ResponseEntity<?> addProductReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        if (authServiceClient.IsUser()){
            try {

                return new ResponseEntity<>(productService.addProductReview(reviewRequestDTO), HttpStatus.OK);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("Invalid user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }
}
