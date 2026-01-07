package com.novacommerce.user_service.adapter.in.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacommerce.user_service.application.port.out.TokenValidatorPort;
import com.novacommerce.user_service.domain.model.Token;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Filtro de autenticación JWT para user-service.
 * Valida tokens JWT y establece el contexto de seguridad.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidatorPort tokenValidatorPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token != null) {
                Token validatedToken = tokenValidatorPort.validateToken(token);

                if (validatedToken.isValid()) {
                    Set<SimpleGrantedAuthority> authorities = parseAuthorities(validatedToken.getAuthorities());

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            validatedToken.getUsername(),
                            null,
                            authorities
                        );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Usuario autenticado: {}", validatedToken.getUsername());
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error en filtro de autenticación JWT: {}", e.getMessage());
            handleAuthenticationError(response, "Error de autenticación: " + e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (log.isDebugEnabled()) {
            var names = request.getHeaderNames();
            StringBuilder sb = new StringBuilder("Headers recibidos en user-service: [");
            while (names != null && names.hasMoreElements()) {
                String name = names.nextElement();
                sb.append(name).append(", ");
            }
            sb.append("]");
            log.debug(sb.toString());
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private Set<SimpleGrantedAuthority> parseAuthorities(String authoritiesString) {
        if (authoritiesString == null || authoritiesString.isEmpty()) {
            return Collections.emptySet();
        }

        return Stream.of(authoritiesString.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", message);
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", new Date());
        errorResponse.put("path", "");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}
