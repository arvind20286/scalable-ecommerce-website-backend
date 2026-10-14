package com.payment_service_api.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.payment_service_api.Feign.Client.NotificationClient;
import com.payment_service_api.dto.OrderDTO;
import com.payment_service_api.dto.CheckoutResponse;
import com.payment_service_api.dto.PaymentVerificationRequest;
import com.payment_service_api.dto.PayDTO;
import com.payment_service_api.Service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    private final NotificationClient notificationClient;

    @PostMapping("/api/payment/checkout")
    public CheckoutResponse startCheckout(@RequestHeader("X-User-Id") Long userId) {
        return paymentService.startCheckout(userId);
    }

    @PostMapping("/api/payment/verify")
    public ResponseEntity<OrderDTO> verifyPayment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PaymentVerificationRequest request) {
        OrderDTO completedOrder = paymentService.verifyPayment(userId, request);
        PayDTO notification = PayDTO.builder()
                .orderId(completedOrder.getOrderId())
                .userName(completedOrder.getName())
                .userEmail(completedOrder.getEmail())
                .userAddress(completedOrder.getAddress())
                .userPhone(completedOrder.getPhone())
                .orderStatus(completedOrder.getOrderStatus())
                .orderDate(completedOrder.getOrderDate())
                .totalAmount(completedOrder.getTotalAmount())
                .build();
        try {
            notificationClient.sendEmail(notification);
        } catch (RuntimeException notificationError) {
            log.error("Could not send payment confirmation notification for order {}",
                    completedOrder.getOrderId(), notificationError);
        }
        return ResponseEntity.ok(completedOrder);
    }

}
