package com.product_service_api.Controller;

import java.util.List;

import com.product_service_api.DTO.*;
import com.product_service_api.Entity.AttributeType;
import com.product_service_api.Exceptions.BadRequestException;
import com.product_service_api.Exceptions.ConflictException;
import com.product_service_api.Exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.product_service_api.Authorization.Client.AuthServiceClient;
import com.product_service_api.Entity.Product;
import com.product_service_api.Service.ProductService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthServiceClient authServiceClient;

    @GetMapping
    public ResponseEntity<?> findAllProducts() {
        if (authServiceClient.isUser()) {
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
        if (authServiceClient.isUser()) {
            try {
                return new ResponseEntity<>(productService.updateStockProduct(idProduct, Stock), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Your are not user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

//    @PostMapping("/save")
//    public ResponseEntity<?> saveProduct(@ModelAttribute Product product, @RequestParam("images-files") List<MultipartFile> images) {
//        if (authServiceClient.isAdmin()) {
//            try {
//                System.out.println("got save req");
//                return new ResponseEntity<>(productService.saveProduct(product, images), HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            return null;
//        }
//    }

    @PostMapping("/save")
    public ResponseEntity<?> saveProduct(@RequestBody ProductRequest productRequest) {
        if (authServiceClient.isAdmin()) {
            try {
                System.out.println(productRequest);
                return new ResponseEntity<>(productService.saveProduct(productRequest), HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
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

    @PostMapping("/register/brand")
    public ResponseEntity<?> registerBrand(@RequestBody BrandRequestDTO brandRequestDTO){
        return new ResponseEntity<>(productService.registerBrand(brandRequestDTO), HttpStatus.OK);
    }

    @GetMapping("/brands")
    public ResponseEntity<?> getAllBrands(){
        try {
            return new ResponseEntity<>(productService.getAllBrands(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/colour")
    public ResponseEntity<?> addColour(@RequestBody ColourRequest colourRequest){
        return new ResponseEntity<>(productService.addColour(colourRequest), HttpStatus.OK);
    }

    @GetMapping("/colours")
    public ResponseEntity<?> getAllColours(){
        try {
            return new ResponseEntity<>(productService.getAllColours(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/size-categories")
    public ResponseEntity<?> addSizeCategory(@RequestBody SizeCategoryRequest sizeCategoryRequest){
        return new ResponseEntity<>(productService.addSizeCategory(sizeCategoryRequest), HttpStatus.OK);
    }

    @GetMapping("/size-categories")
    public ResponseEntity<?> getAllSizeCategories(){
        try {
            return new ResponseEntity<>(productService.getAllSizeCategories(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/size-option")
    public ResponseEntity<?> addSizeOption(@RequestBody SizeOptionRequest sizeOptionRequest){
        return new ResponseEntity<>(productService.addSizeOption(sizeOptionRequest), HttpStatus.OK);
    }

    @GetMapping("/size-options")
    public ResponseEntity<?> getAllSizeOptions(){
        try {
            return new ResponseEntity<>(productService.getAllSizeOptions(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/attribute-type")
    public ResponseEntity<?> addAttributeType(@RequestBody AttributeTypeRequest attributeTypeRequest){
        return new ResponseEntity<>(productService.addAttributeType(attributeTypeRequest), HttpStatus.OK);
    }

    @GetMapping("/attribute-types")
    public ResponseEntity<?> getAllAttributeTypes(){
        try {
            return new ResponseEntity<>(productService.getAllAttributeTypes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/attribute-option")
    public ResponseEntity<?> addAttributeOption(@RequestBody AttributeOptionRequest attributeOptionRequest){
        return new ResponseEntity<>(productService.addAttributeOption(attributeOptionRequest), HttpStatus.OK);
    }

    @GetMapping("/attribute-options")
    public ResponseEntity<?> getAllAttributeOptions(){
        try {
            return new ResponseEntity<>(productService.getAllAttributeOptions(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product-images")
    public ResponseEntity<?> getAllProductImages(){
        try {
            return new ResponseEntity<>(productService.getAllProductImages(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product-categories")
    public ResponseEntity<?> getAllProductCategories(){
        try {
            return new ResponseEntity<>(productService.getAllProductCategories(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product-attributes")
    public ResponseEntity<?> getAllProductAttributes(){
        try {
            return new ResponseEntity<>(productService.getAllProductAttributes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product-items")
    public ResponseEntity<?> getAllProductItems(){
        try {
            return new ResponseEntity<>(productService.getAllProductItems(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product-variations")
    public ResponseEntity<?> getAllProductVariations(){
        try {
            return new ResponseEntity<>(productService.getAllProductVariations(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getAllReviews(){
        try {
            return new ResponseEntity<>(productService.getAllReviews(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{idProduct}")
    public ResponseEntity<?> findProductById(@PathVariable("idProduct") Long idProduct) {
        if (authServiceClient.isUser()) {
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
        if (authServiceClient.isClient()){
            try {

                return new ResponseEntity<>(productService.addProductReview(reviewRequestDTO), HttpStatus.OK);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("Invalid user", HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable("productId") Long productId, @RequestBody ProductRequest productRequest){
        if (authServiceClient.isAdmin()){
            try {
                return new ResponseEntity<>(productService.updateProduct(productId, productRequest), HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") Long productId){
        if (authServiceClient.isAdmin()){
            try {
                productService.deleteProduct(productId);
                return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/category/add")
    public ResponseEntity<?> addProductCategory(@RequestBody ProductCategoryRequest productCategoryRequest){
        if (authServiceClient.isAdmin()){
            try {
                return new ResponseEntity<>(productService.addProductCategory(productCategoryRequest), HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/category/{categoryId}")
    public ResponseEntity<?> updateProductCategory(@PathVariable("categoryId") Long categoryId, @RequestBody ProductCategoryRequest productCategoryRequest){
        if (authServiceClient.isAdmin()){
            try {
                return new ResponseEntity<>(productService.updateProductCategory(categoryId, productCategoryRequest), HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<?> deleteProductCategory(@PathVariable("categoryId") Long categoryId){
        if (authServiceClient.isAdmin()){
            try {
                productService.deleteProductCategory(categoryId);
                return new ResponseEntity<>("Product category deleted successfully", HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/product-item/{productItemId}")
    public ResponseEntity<?> updateProductItemStock(@PathVariable("productItemId") Long productItemId,
                                                     @RequestParam(required = false) Double originalPrice,
                                                     @RequestParam(required = false) Double salePrice){
        if (authServiceClient.isAdmin()){
            try {
                return new ResponseEntity<>(productService.updateProductItemStock(productItemId, originalPrice, salePrice), HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/product-item/{productItemId}")
    public ResponseEntity<?> deleteProductItem(@PathVariable("productItemId") Long productItemId){
        if (authServiceClient.isAdmin()){
            try {
                productService.deleteProductItem(productItemId);
                return new ResponseEntity<>("Product item deleted successfully", HttpStatus.OK);
            } catch (BadRequestException | ForbiddenException e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (Exception e){
                return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }else {
            return new ResponseEntity<>("User is not admin", HttpStatus.FORBIDDEN);
        }
    }
}
