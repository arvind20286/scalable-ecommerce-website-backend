package com.product_service_api.Authorization;

public interface GatewayIdentity {
    boolean isAdmin();

    boolean isSeller();

    Long getUserId();
}
