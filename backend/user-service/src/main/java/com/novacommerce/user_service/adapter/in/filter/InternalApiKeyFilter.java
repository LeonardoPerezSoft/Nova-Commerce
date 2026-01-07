package com.novacommerce.user_service.adapter.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro para validar API Key en endpoints internos.
 * Protege los endpoints /internal/** con una API Key.
 */
@Component
@Slf4j
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final String internalApiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InternalApiKeyFilter(@Value("${app.jwt.internal-api-key:nova-internal-service-key-2024}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Solo aplicar a endpoints /internal/**
        if (requestPath.startsWith("/internal/")) {
            String apiKey = request.getHeader("X-Internal-API-Key");
            if (log.isDebugEnabled()) {
                var names = request.getHeaderNames();
                StringBuilder sb = new StringBuilder("Headers recibidos en ").append(requestPath).append(": [");
                while (names != null && names.hasMoreElements()) {
                    String name = names.nextElement();
                    sb.append(name).append(", ");
                }
                sb.append("]");
                log.debug(sb.toString());
            }

            if (apiKey == null || !apiKey.equals(internalApiKey)) {
                log.warn("Intento de acceso a endpoint interno sin API Key válida: {}", requestPath);
                handleUnauthorized(response, requestPath);
                return;
            }

            log.debug("API Key válida para endpoint interno: {}", requestPath);
        }

        filterChain.doFilter(request, response);
    }

    private void handleUnauthorized(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "API Key inválida o faltante");
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", new Date());
        errorResponse.put("path", path);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
