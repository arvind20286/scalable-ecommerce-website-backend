package com.product_service_api.Controller;

import com.product_service_api.Service.CategoryService;
import com.product_service_api.Service.ItemService;
import com.product_service_api.Service.ProductService;
import com.product_service_api.Service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final SellerProductService sellerProductService;

    @GetMapping
    public ResponseEntity<?> findAllProducts() {
        return new ResponseEntity<>(productService.findAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{idProduct}")
    public ResponseEntity<?> findProductById(@PathVariable("idProduct") Long idProduct) {
        return new ResponseEntity<>(productService.findProductById(idProduct), HttpStatus.OK);
    }

    @GetMapping("/variations/{variationId}")
    public ResponseEntity<?> findProductVariationById(@PathVariable("variationId") Long variationId) {
        return new ResponseEntity<>(productService.findProductVariationById(variationId), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getProductsPaginatedAndSortByPrice(@RequestParam int page,
                                                                @RequestParam int size,
                                                                @RequestParam Long category,
                                                                @RequestParam(required = false) String sort) {
        return new ResponseEntity<>(itemService.getProductsPaginatedAndSortByPrice(page, size, category, sort), HttpStatus.OK);

    }

    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands() {
        return new ResponseEntity<>(sellerProductService.getAllBrands(), HttpStatus.OK);

    }

    @GetMapping("/product-images")
    public ResponseEntity<?> getAllProductImages() {
        return new ResponseEntity<>(productService.getAllProductImages(), HttpStatus.OK);

    }

    @GetMapping("/product-variations")
    public ResponseEntity<?> getAllProductVariations() {
        return new ResponseEntity<>(productService.getAllProductVariations(), HttpStatus.OK);

    }
}
