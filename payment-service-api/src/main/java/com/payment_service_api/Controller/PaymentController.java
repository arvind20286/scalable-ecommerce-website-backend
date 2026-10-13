package com.payment_service_api.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.payment_service_api.Feign.Client.NotificationClient;
import com.payment_service_api.dto.OrderDTO;
import com.payment_service_api.dto.CheckoutRequest;
import com.payment_service_api.dto.CheckoutResponse;
import com.payment_service_api.dto.PayDTO;
import com.payment_service_api.Service.PaymentService;
import com.paypal.base.rest.PayPalRESTException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    private final NotificationClient notificationClient;

    @Value("${payment.public-base-url}")
    private String paymentPublicBaseUrl;

    @PostMapping("/api/payment/checkout")
    @ResponseBody
    public CheckoutResponse startCheckout(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody(required = false) CheckoutRequest request) throws PayPalRESTException {
        String currency = request == null || request.getCurrency() == null || request.getCurrency().isBlank()
                ? "INR"
                : request.getCurrency().toUpperCase();
        if (!java.util.List.of("INR").contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency: " + currency);
        }
        String successUrl = paymentPublicBaseUrl + "/payment/success/{orderId}";
        String cancelUrl = paymentPublicBaseUrl + "/payment/cancel";
        CheckoutResponse response = paymentService.startCheckout(userId, currency, successUrl, cancelUrl);
        return response;
    }

    @GetMapping("/payment/cancel")
    private String paymentCancelled() {
        return "paymentFailed";
    }

    @GetMapping("/payment/success/{orderId}")
    private String ViewSuccess(@PathVariable Long orderId, Model model,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        try {
            OrderDTO orderDTO = paymentService.completeOrderAfterApprovedPayment(orderId, paymentId, payerId);
            model.addAttribute("order", orderDTO);
            PayDTO informationToPay = PayDTO.builder().orderId(orderId)
                    .userName(orderDTO.getName())
                    .userEmail(orderDTO.getEmail())
                    .userAddress(orderDTO.getAddress())
                    .userPhone(orderDTO.getPhone())
                    .orderStatus(orderDTO.getOrderStatus())
                    .orderDate(orderDTO.getOrderDate())
                    .totalAmount(orderDTO.getTotalAmount())
                    .build();
            try {
                notificationClient.sendEmail(informationToPay);
            } catch (RuntimeException notificationError) {
                log.error("Could not send payment confirmation notification for order {}", orderId, notificationError);
            }
            return "paymentSuccess";
        } catch (PayPalRESTException e) {
            log.error("Error occurred:: ", e);
        } catch (RuntimeException e) {
            log.error("Payment was not confirmed for order {}", orderId, e);
        }
        return "paymentFailed";
    }

}
