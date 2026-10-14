package com.payment_service_api.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.payment_service_api.Feign.Client.OrderServiceClient;
import com.payment_service_api.dto.CheckoutResponse;
import com.payment_service_api.dto.OrderDTO;
import com.payment_service_api.dto.PaymentVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private static final String CURRENCY = "INR";
    private final RestClient razorpayRestClient;
    private final OrderServiceClient orderServiceClient;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public CheckoutResponse startCheckout(Long userId) {
        OrderDTO order = orderServiceClient.createOrder(userId);
        if (order.getOrderId() == null || order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new IllegalStateException("Order service returned an invalid order for payment");
        }

        long amountInPaise = BigDecimal.valueOf(order.getTotalAmount())
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();
        String receipt = "order-" + order.getOrderId();
        JsonNode razorpayOrder = razorpayRestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "amount", amountInPaise,
                        "currency", CURRENCY,
                        "receipt", receipt,
                        "notes", Map.of("internalOrderId", order.getOrderId().toString())
                ))
                .retrieve()
                .body(JsonNode.class);

        if (razorpayOrder == null || !razorpayOrder.hasNonNull("id")) {
            throw new IllegalStateException("Razorpay did not return a payment order ID");
        }
        return new CheckoutResponse(
                order.getOrderId(),
                razorpayOrder.get("id").asText(),
                razorpayKeyId,
                amountInPaise,
                CURRENCY
        );
    }

    public OrderDTO verifyPayment(Long userId, PaymentVerificationRequest request) {
        verifySignature(request);

        JsonNode razorpayOrder = razorpayRestClient.get()
                .uri("/orders/{orderId}", request.getRazorpayOrderId())
                .retrieve()
                .body(JsonNode.class);
        if (razorpayOrder == null
                || !("order-" + request.getOrderId()).equals(textValue(razorpayOrder, "receipt"))
                || !request.getOrderId().toString().equals(
                        textValue(razorpayOrder.path("notes"), "internalOrderId"))) {
            throw new IllegalArgumentException("Razorpay order does not match the requested order");
        }

        OrderDTO order = orderServiceClient.bringOrder(request.getOrderId());
        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to the authenticated user");
        }
        long expectedAmount = BigDecimal.valueOf(order.getTotalAmount())
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();
        if (!CURRENCY.equals(textValue(razorpayOrder, "currency"))
                || razorpayOrder.path("amount").asLong(-1) != expectedAmount) {
            throw new IllegalArgumentException("Razorpay order amount or currency does not match");
        }

        JsonNode payment = razorpayRestClient.get()
                .uri("/payments/{paymentId}", request.getRazorpayPaymentId())
                .retrieve()
                .body(JsonNode.class);
        if (payment == null
                || !request.getRazorpayOrderId().equals(textValue(payment, "order_id"))
                || payment.path("amount").asLong(-1) != expectedAmount
                || !CURRENCY.equals(textValue(payment, "currency"))) {
            throw new IllegalArgumentException("Razorpay payment does not match the order");
        }

        String paymentStatus = textValue(payment, "status");
        if ("authorized".equals(paymentStatus)) {
            payment = razorpayRestClient.post()
                    .uri("/payments/{paymentId}/capture", request.getRazorpayPaymentId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("amount", expectedAmount, "currency", CURRENCY))
                    .retrieve()
                    .body(JsonNode.class);
            paymentStatus = textValue(payment, "status");
        }
        if (!"captured".equals(paymentStatus)) {
            throw new IllegalStateException("Razorpay payment has not been captured");
        }
        return orderServiceClient.completeOrder(request.getOrderId());
    }

    private void verifySignature(PaymentVerificationRequest request) {
        if (request.getOrderId() == null
                || isBlank(request.getRazorpayOrderId())
                || isBlank(request.getRazorpayPaymentId())
                || isBlank(request.getRazorpaySignature())) {
            throw new IllegalArgumentException("Payment verification details are required");
        }

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = hmac.doFinal(
                    (request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId())
                            .getBytes(StandardCharsets.UTF_8)
            );
            byte[] expected = HexFormat.of().formatHex(digest).getBytes(StandardCharsets.US_ASCII);
            byte[] supplied = request.getRazorpaySignature().getBytes(StandardCharsets.US_ASCII);
            if (!MessageDigest.isEqual(expected, supplied)) {
                throw new IllegalArgumentException("Invalid Razorpay payment signature");
            }
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Could not verify Razorpay payment signature", exception);
        }
    }

    private static String textValue(JsonNode node, String fieldName) {
        return node == null || !node.hasNonNull(fieldName) ? null : node.get(fieldName).asText();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
