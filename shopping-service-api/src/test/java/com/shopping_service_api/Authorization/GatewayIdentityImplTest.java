package com.shopping_service_api.Authorization;

import com.shopping_service_api.Exceptions.ForbiddenException;
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
    void readsUserIdAddedByGateway() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "42");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThat(gatewayIdentity.getUserId()).isEqualTo(42L);
    }

    @Test
    void rejectsMissingGatewayIdentity() {
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));

        assertThatThrownBy(gatewayIdentity::getUserId)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("User identity not provided by gateway");
    }

    @Test
    void rejectsInvalidGatewayIdentity() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "not-a-number");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThatThrownBy(gatewayIdentity::getUserId)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Invalid user identity provided by gateway");
    }
}
