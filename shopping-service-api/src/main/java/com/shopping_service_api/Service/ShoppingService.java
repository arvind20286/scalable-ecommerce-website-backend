package com.shopping_service_api.Service;

import com.shopping_service_api.entity.Cart;

public interface ShoppingService {
    Cart addToCart(Long idUser, Long idProduct);

    Cart syncCartItem(Long idUser, Long variationId, Integer quantity);

    Cart removeFromCart(Long idUser, Long idProduct);

    Cart clearCart(Long idUser);

    Cart getCart(Long idUser);
}
