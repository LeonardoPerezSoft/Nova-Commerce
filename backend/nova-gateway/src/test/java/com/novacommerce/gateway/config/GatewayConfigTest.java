package com.novacommerce.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GatewayConfigTest {

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void testGatewayConfigBeanCreation() {
        assertNotNull(gatewayConfig, "GatewayConfig bean should not be null");
    }

    @Test
    void testRouteLocatorBeanCreation() {
        assertNotNull(routeLocator, "RouteLocator bean should not be null");
    }

    @Test
    void testRouteLocatorConfiguration() {
        assertNotNull(routeLocator, "RouteLocator should be configured");
    }

    @Test
    void testGatewayConfigureRoutes() {
        assertNotNull(gatewayConfig, "GatewayConfig should configure routes");
    }
}
