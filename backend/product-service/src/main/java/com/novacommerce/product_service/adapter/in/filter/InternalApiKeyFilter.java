package com.novacommerce.product_service.adapter.in.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-API-Key";
    private final String internalApiKey;

    public InternalApiKeyFilter(
            @Value("${app.jwt.internal-api-key:nova-internal-service-key-2024}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/internal/")) {
            String providedKey = request.getHeader(INTERNAL_API_KEY_HEADER);

            if (providedKey == null || !providedKey.equals(internalApiKey)) {
                log.warn("Unauthorized internal API access attempt to: {}", requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Intento de acceso a endpoint interno sin API Key válida\"}");
                return;
            }

            log.debug("Internal API key validated for: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }
}
