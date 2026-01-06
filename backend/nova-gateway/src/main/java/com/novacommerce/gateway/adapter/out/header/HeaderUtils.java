package com.novacommerce.gateway.adapter.out.header;

import java.util.List;

import org.springframework.http.server.reactive.ServerHttpRequest;

import com.novacommerce.gateway.routing.RouteConstants;

/**
 * Adaptador de salida para gestión de headers HTTP
 * Proporciona utilidades para extraer y añadir headers personalizados
 */
public final class HeaderUtils {

    private HeaderUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Extrae el token JWT del header Authorization
     *
     * @param request la solicitud HTTP
     * @return el token sin el prefijo "Bearer " o null si no existe
     */
    public static String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().get(RouteConstants.HEADER_AUTHORIZATION);

        if (authHeaders == null || authHeaders.isEmpty()) {
            return null;
        }

        String authHeader = authHeaders.get(0);
        if (authHeader != null && authHeader.startsWith(RouteConstants.BEARER_PREFIX)) {
            return authHeader.substring(RouteConstants.BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Añade headers personalizados a la solicitud
     *
     * @param request la solicitud original
     * @param username el nombre de usuario a inyectar
     * @param authorities las autoridades a inyectar
     * @return una nueva solicitud con los headers personalizados añadidos
     */
    public static ServerHttpRequest addCustomHeaders(ServerHttpRequest request, String username, String authorities) {
        return request.mutate()
                .header(RouteConstants.HEADER_USERNAME, username)
                .header(RouteConstants.HEADER_AUTHORITIES, authorities)
                .build();
    }
}
