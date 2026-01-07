package com.novacommerce.gateway.routing;

public final class RouteConstants {

    private RouteConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_AUTHORITIES = "X-Authorities";
    public static final String BEARER_PREFIX = "Bearer ";
    
    public static final String AUTH_SERVICE_ID = "auth-service";
    public static final String AUTH_SERVICE_PATH = "/api/auth/**";
}
