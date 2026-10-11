package com.shopping_service_api.Service;

import com.shopping_service_api.entity.Cart;

public interface ShoppingService {
    Cart addToCart(Long variationId);

    Cart syncCartItem(Long variationId, Integer quantity);

    Cart removeFromCart(Long variationId);

    Cart clearCart();

    Cart getCart();
}
