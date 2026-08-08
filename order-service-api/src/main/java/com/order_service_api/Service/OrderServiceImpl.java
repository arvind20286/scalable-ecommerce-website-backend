package com.order_service_api.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.order_service_api.Exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.order_service_api.Client.ShopServiceClient;
import com.order_service_api.Client.UserServiceClient;
import com.order_service_api.Model.Entities.Order;
import com.order_service_api.Model.Entities.OrderItem;
import com.order_service_api.Model.Entities.Order.StatusOrder;
import com.order_service_api.Model.dto.CartDTO;
import com.order_service_api.Model.dto.ResponseOrder;
import com.order_service_api.Model.dto.UserDTO;
import com.order_service_api.Repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ShopServiceClient shopServiceClient;

    @Override
    public Order createOrder(Long idUser) {
        CartDTO cart = shopServiceClient.getCart(idUser);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty or not found for user with id: " + idUser);
        }
        UserDTO user = userServiceClient.getUserById(idUser);
        List<OrderItem> OrderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .idProduct(cartItem.getVariationId())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getPrice())
                        .totalPrice(Math.round((cartItem.getPrice() * cartItem.getQuantity()) * 100.0) / 100.0)
                        .build())
                .collect(Collectors.toList());
        Order order = Order.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .address(user.getAddress())
                .phone(user.getPhone())
                .items(OrderItems)
                .totalAmount(Math.round(cart.getTotal() * 100.0) / 100.0)
                .orderStatus(StatusOrder.PENDING)
                .build();
            shopServiceClient.cleanCart(idUser);
        return orderRepository.save(order);
    }

    @Override
    public Order bringOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (order != null) {
            order.setOrderStatus(StatusOrder.PROCESSING);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with given order id: " + orderId);
        }
    }

    @Override
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (order != null) {
            order.setOrderStatus(StatusOrder.COMPLETED);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with given order id: " + orderId);
        }
    }

    @Override
    public List<ResponseOrder> getOrdersByUser(String email) {
        if (userServiceClient.isAdmin()) {
            List<Order> orders = orderRepository.findByEmail(email);
            List<ResponseOrder> responseOrders = orders.stream()
                    .map(order -> ResponseOrder.builder()
                            .orderId(order.getOrderId())
                            .email(order.getEmail())
                            .totalAmount(order.getTotalAmount())
                            .orderStatus(order.getOrderStatus())
                            .orderDate(order.getOrderDate())
                            .build())
                    .collect(Collectors.toList());
            return responseOrders;
        } else {
            return null;
        }

    }

    @Override
    public List<ResponseOrder> getAllOrders() {
        if (userServiceClient.isAdmin()) {
            List<Order> orders = orderRepository.findAll();
            List<ResponseOrder> responseOrders = orders.stream()
                    .map(order -> ResponseOrder.builder()
                            .orderId(order.getOrderId())
                            .email(order.getEmail())
                            .totalAmount(order.getTotalAmount())
                            .orderStatus(order.getOrderStatus())
                            .orderDate(order.getOrderDate())
                            .build())
                    .collect(Collectors.toList());
            return responseOrders;
        } else {
            return null;
        }

    }

}
