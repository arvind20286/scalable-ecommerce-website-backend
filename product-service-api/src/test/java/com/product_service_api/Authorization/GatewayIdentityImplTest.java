package com.product_service_api.Authorization;

import com.product_service_api.Exceptions.ForbiddenException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GatewayIdentityImplTest {

    private final GatewayIdentityImpl gatewayIdentity = new GatewayIdentityImpl();

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void readsIdentityClaimsAddedByGateway() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "42");
        request.addHeader("X-User-Role", "SELLER");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThat(gatewayIdentity.getUserId()).isEqualTo(42L);
        assertThat(gatewayIdentity.isSeller()).isTrue();
        assertThat(gatewayIdentity.isAdmin()).isFalse();
    }

    @Test
    void rejectsMissingGatewayIdentity() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));

        assertThatThrownBy(gatewayIdentity::getUserId)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User identity not provided by gateway");
    }
}
