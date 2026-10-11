package com.product_service_api.Controller;

import com.product_service_api.DTO.AddProductItem;
import com.product_service_api.Service.ItemService;
import com.product_service_api.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product/item")
public class ItemController {

    private final ItemService itemService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> addProductItem(@RequestBody AddProductItem addProductItem) {
        return new ResponseEntity<>(itemService.addProductItemToProduct(addProductItem), HttpStatus.OK);
    }

    @PutMapping("/{productItemId}")
    public ResponseEntity<?> updateProductItemPrice(@PathVariable("productItemId") Long productItemId,
                                                    @RequestParam(required = false) Double originalPrice,
                                                    @RequestParam(required = false) Double salePrice) {
        return new ResponseEntity<>(itemService.updateProductItemPrice(productItemId, originalPrice, salePrice), HttpStatus.OK);

    }

    @DeleteMapping("/{productItemId}")
    public ResponseEntity<?> deleteProductItem(@PathVariable("productItemId") Long productItemId) {
        itemService.deleteProductItem(productItemId);
        return new ResponseEntity<>("Product item deleted successfully", HttpStatus.OK);
    }
}
