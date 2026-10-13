package com.payment_service_api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.payment_service_api.Feign.Client.OrderServiceClient;
import com.payment_service_api.dto.CheckoutResponse;
import com.payment_service_api.dto.OrderDTO;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final APIContext apiContext;
    private final OrderServiceClient orderServiceClient;

    public OrderDTO viewOrderDetails(Long idOrder) {
        return orderServiceClient.bringOrder(idOrder);
    }

    public CheckoutResponse startCheckout(Long userId, String currency, String successUrl, String cancelUrl)
            throws PayPalRESTException {
        OrderDTO order = orderServiceClient.createOrder(userId);
        if (order.getOrderId() == null || order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new IllegalStateException("Order service returned an invalid order for payment");
        }

        Payment payment = createPayment(
                order.getOrderId(),
                order.getTotalAmount(),
                currency,
                "paypal",
                "sale",
                "Order " + order.getOrderId(),
                cancelUrl,
                successUrl.replace("{orderId}", order.getOrderId().toString())
        );
        String approvalUrl = payment.getLinks().stream()
                .filter(link -> "approval_url".equals(link.getRel()))
                .map(Links::getHref)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Payment provider did not return an approval URL"));
        return new CheckoutResponse(order.getOrderId(), approvalUrl);
    }

    public Payment createPayment(
            Long orderId,
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US, "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setCustom(String.valueOf(orderId));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

    public OrderDTO completeOrderAfterApprovedPayment(Long orderId, String paymentId, String payerId)
            throws PayPalRESTException {
        OrderDTO order = orderServiceClient.bringOrder(orderId);
        Payment executedPayment = executePayment(paymentId, payerId);
        if (!"approved".equalsIgnoreCase(executedPayment.getState())
                || executedPayment.getTransactions() == null
                || executedPayment.getTransactions().isEmpty()) {
            throw new IllegalStateException("Payment was not approved");
        }

        Transaction transaction = executedPayment.getTransactions().get(0);
        Amount amount = transaction.getAmount();
        if (amount == null || !"INR".equalsIgnoreCase(amount.getCurrency())) {
            throw new IllegalStateException("Approved payment currency does not match the order currency");
        }
        BigDecimal paidAmount = new BigDecimal(amount.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal orderAmount = BigDecimal.valueOf(order.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);
        if (!Objects.equals(transaction.getCustom(), String.valueOf(orderId))
                || paidAmount.compareTo(orderAmount) != 0) {
            throw new IllegalStateException("Approved payment does not match the order");
        }
        return orderServiceClient.completeOrder(orderId);
    }

}
