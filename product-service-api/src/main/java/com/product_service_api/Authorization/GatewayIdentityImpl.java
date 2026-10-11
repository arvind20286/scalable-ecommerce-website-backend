package com.product_service_api.Authorization;

import com.product_service_api.Exceptions.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class GatewayIdentityImpl implements GatewayIdentity {

    @Override
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    @Override
    public boolean isSeller() {
        return hasRole("SELLER");
    }

    @Override
    public Long getUserId() {
        String userId = currentRequest().getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            throw new ForbiddenException("User identity not provided by gateway");
        }

        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException exception) {
            throw new ForbiddenException("Invalid user identity provided by gateway");
        }
    }

    private boolean hasRole(String role) {
        String requestRole = currentRequest().getHeader("X-User-Role");
        return role.equalsIgnoreCase(requestRole);
    }

    private HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        throw new ForbiddenException("Gateway identity is unavailable");
    }
}
