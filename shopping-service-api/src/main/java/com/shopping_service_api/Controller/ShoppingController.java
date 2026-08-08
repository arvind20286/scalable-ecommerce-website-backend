package com.shopping_service_api.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shopping_service_api.entity.Cart;
import com.shopping_service_api.DTO.AddToCartRequest;
import com.shopping_service_api.Service.ShoppingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shopping")
public class ShoppingController {

    private final ShoppingService shoppingService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        Cart cart = shoppingService.addToCart(request.getIdUser(), request.getVariationId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> syncCartItem(@RequestBody AddToCartRequest request) {
        Cart cart = shoppingService.syncCartItem(request.getIdUser(), request.getVariationId(), request.getQuantity());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> removeFromCart(@RequestBody AddToCartRequest request) {
        Cart cart = shoppingService.removeFromCart(request.getIdUser(), request.getVariationId());
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<?> getCart(@PathVariable Long idUser) {
        Cart cart = shoppingService.getCart(idUser);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/clear/{idUser}")
    public ResponseEntity<?> clearCart(@PathVariable Long idUser) {
        Cart cart = shoppingService.clearCart(idUser);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
}
