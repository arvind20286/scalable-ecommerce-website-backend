package com.shopping_service_api.Service;

import java.util.ArrayList;

import com.shopping_service_api.DTO.ProductVariationDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.shopping_service_api.Client.ProductServiceClient;
import com.shopping_service_api.Client.UserServiceClient;
import com.shopping_service_api.DTO.UserDTO;
import com.shopping_service_api.entity.Cart;
import com.shopping_service_api.entity.CartItem;
import com.shopping_service_api.Repository.CartRepository;

@Service
@RequiredArgsConstructor
public class ShoppingServiceImpl implements ShoppingService {

    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final CartRepository cartRepository;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingServiceImpl.class);

    @Override
    public Cart addToCart(Long idUser, Long variationId) {
        logger.debug("AddToCart called: userId={}, variationId={}", idUser, variationId);
        Integer quantity = 1;
        if (idUser == null || variationId == null || quantity == null || quantity <= 0) {
            logger.error("Invalid input to AddToCart: userId={}, productId={}, quantity={}", idUser, variationId, quantity);
            throw new IllegalArgumentException("Invalid input: userId, productId and quantity must be provided and quantity must be > 0");
        }

        UserDTO user = userServiceClient.getUserById(idUser);
        if (user == null) {
            logger.warn("User not found: userId={}", idUser);
            throw new IllegalArgumentException("User does not exist: " + idUser);
        }

        ProductVariationDTO productVariationDTO = productServiceClient.findProductVariationsById(variationId);
        if (productVariationDTO == null) {
            logger.warn("Product not found: productId={}", variationId);
            throw new IllegalArgumentException("Product does not exist: " + variationId);
        }

        Cart cart = cartRepository.findByIdUser(idUser);

        Long stock = productVariationDTO.getStock();
        if (stock == null || stock < quantity) {
            logger.warn("Insufficient stock for productId={} requested={} available={}", variationId, quantity, stock);
            throw new IllegalArgumentException("Stock insufficient for product: " + variationId);
        }



        if (cart == null) {
            cart = Cart.builder()
                    .idUser(user.getId())
                    .email(user.getEmail())
                    .total(0.0)
                    .cartItems(new ArrayList<>())
                    .build();
            logger.debug("Created new cart for userId={}", idUser);
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getVariationId().equals(variationId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            logger.debug("Increased quantity for productId={} in cart userId={} newQuantity={}", variationId, idUser, cartItem.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .variationId(variationId)
                    .price(productVariationDTO.getPrice())
                    .quantity(quantity)
                    .cart(cart)
                    .build();
            cart.getCartItems().add(cartItem);
            logger.debug("Added new cartItem productId={} quantity={} for userId={}", variationId, quantity, idUser);
        }

        Double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotal(total);
        Cart saved = cartRepository.save(cart);
        logger.debug("Cart saved for userId={} total={}", idUser, total);
        return saved;
    }

    @Override
    public Cart syncCartItem(Long idUser, Long variationId, Integer quantity) {
        Cart cart = cartRepository.findByIdUser(idUser);
        if (cart == null) {
            throw new IllegalArgumentException("Error no Product added to cart");
        }
        CartItem itemToUpdate = cart.getCartItems().stream()
                .filter(item -> item.getVariationId().equals(variationId))
                .findFirst()
                .orElse(null);
        if (itemToUpdate == null) {
            throw new IllegalArgumentException("Error no Such Product in The Cart");
        }
        itemToUpdate.setQuantity(quantity);

        Double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotal(total);

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeFromCart(Long idUser, Long variationId) {
        Cart cart = cartRepository.findByIdUser(idUser);
        if (cart == null) {
            throw new IllegalArgumentException("Error no Product added to cart");
        }
        CartItem itemToDelete = cart.getCartItems().stream()
                .filter(item -> item.getVariationId().equals(variationId))
                .findFirst()
                .orElse(null);
        if (itemToDelete == null) {
            throw new IllegalArgumentException("Error no Such Product in The Cart");
        }
        cart.getCartItems().remove(itemToDelete);

        return cartRepository.save(cart);
    }

    @Override
    public Cart clearCart(Long idUser) {
        Cart cart = cartRepository.findByIdUser(idUser);
        if (cart == null) {
            throw new IllegalArgumentException("Error no Product added to cart");
        }
        cart.getCartItems().clear();
        cart.setTotal(0.0);
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCart(Long idUser) {
        Cart cartToSend = cartRepository.findByIdUser(idUser);
        if (cartToSend == null) {
            throw new IllegalArgumentException("El carrito del usuario no existe.");
        }
        return cartToSend;
    }

}

