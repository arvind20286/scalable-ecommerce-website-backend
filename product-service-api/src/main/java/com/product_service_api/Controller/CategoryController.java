package com.product_service_api.Controller;

import com.product_service_api.DTO.ProductCategoryRequest;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ForbiddenException;
import com.product_service_api.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> addProductCategory(@RequestBody ProductCategoryRequest productCategoryRequest) {
        return new ResponseEntity<>(categoryService.addProductCategory(productCategoryRequest), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProductCategories() {
        return new ResponseEntity<>(categoryService.getAllProductCategories(), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getProductCategoryById(@PathVariable("categoryId") Long categoryId) {
        return new ResponseEntity<>(categoryService.getProductCategoryById(categoryId), HttpStatus.OK);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateProductCategory(@PathVariable("categoryId") Long categoryId, @RequestBody ProductCategoryRequest productCategoryRequest) {
        return new ResponseEntity<>(categoryService.updateProductCategory(categoryId, productCategoryRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteProductCategory(@PathVariable("categoryId") Long categoryId) {
        if(categoryService.deleteProductCategory(categoryId)) {
            return new ResponseEntity<>("Product category deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Product category not found", HttpStatus.NOT_FOUND);
    }
}
