package com.novacommerce.order_service.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro para validar llave interna en endpoints /internal/*.
 */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final String internalApiKey;

    public InternalApiKeyFilter(@Value("${app.jwt.internal-api-key}") String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String header = request.getHeader("X-Internal-API-Key");
        
        if (header == null || !header.equals(internalApiKey)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
