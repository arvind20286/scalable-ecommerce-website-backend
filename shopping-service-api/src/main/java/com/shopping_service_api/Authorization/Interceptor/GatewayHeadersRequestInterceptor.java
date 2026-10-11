package com.shopping_service_api.Authorization.Interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class GatewayHeadersRequestInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public void apply(RequestTemplate template) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HttpServletRequest request = attributes.getRequest();
            copyHeader(request, template, HttpHeaders.AUTHORIZATION);
            copyHeader(request, template, USER_ID_HEADER);
            copyHeader(request, template, USER_ROLE_HEADER);
        }
    }

    private void copyHeader(HttpServletRequest request, RequestTemplate template, String headerName) {
        String headerValue = request.getHeader(headerName);
        if (headerValue != null && !headerValue.isBlank()) {
            template.header(headerName, headerValue);
        }
    }
}
