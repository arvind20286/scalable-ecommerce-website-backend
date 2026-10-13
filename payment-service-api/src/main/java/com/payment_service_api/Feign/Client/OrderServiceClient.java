package com.payment_service_api.Feign.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.payment_service_api.dto.OrderDTO;
import com.payment_service_api.Feign.Config.FeignConfig;

@FeignClient(name = "order-service-api", configuration = FeignConfig.class)
public interface OrderServiceClient {
    @PostMapping("/api/orders/create/{idUser}")
    OrderDTO createOrder(@PathVariable Long idUser);

    @GetMapping("/api/orders/bring/{orderId}")
    OrderDTO bringOrder(@PathVariable Long orderId);

    @PostMapping("/api/orders/complete/{orderId}")
    OrderDTO completeOrder(@PathVariable Long orderId);
}
