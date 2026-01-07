package com.novacommerce.auth_service.config.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación JWT.
 * Intercepta cada request, extrae el token JWT del header Authorization,
 * lo valida y establece el contexto de seguridad.
 * Se ejecuta una sola vez por request.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                String authorities = tokenProvider.getAuthoritiesFromToken(token);

                Collection<SimpleGrantedAuthority> authoritiesCollection = parseAuthorities(authorities);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authoritiesCollection
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Autenticación establecida para usuario: {}", username);
            }
        } catch (Exception ex) {
            log.error("No se pudo establecer la autenticación del usuario en el contexto de seguridad", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization del request.
     *
     * @param request el HttpServletRequest
     * @return el token JWT o null si no existe
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        return tokenProvider.extractTokenFromHeader(authorizationHeader);
    }

    /**
     * Parsea las autoridades desde una cadena separada por comas.
     *
     * @param authorities la cadena de autoridades
     * @return la colección de GrantedAuthority
     */
    private Collection<SimpleGrantedAuthority> parseAuthorities(String authorities) {
        if (!StringUtils.hasText(authorities)) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(authorities.split(","))
            .map(String::trim)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
