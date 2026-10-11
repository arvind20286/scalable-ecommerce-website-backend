package com.shopping_service_api.Authorization.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.shopping_service_api.Authorization.Interceptor.GatewayHeadersRequestInterceptor;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor gatewayHeadersRequestInterceptor() {
        return new GatewayHeadersRequestInterceptor();
    }
}
