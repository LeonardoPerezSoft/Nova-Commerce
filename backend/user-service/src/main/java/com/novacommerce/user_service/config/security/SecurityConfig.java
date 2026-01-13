package com.novacommerce.user_service.config.security;

import com.novacommerce.user_service.adapter.in.filter.InternalApiKeyFilter;
import com.novacommerce.user_service.adapter.in.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de Spring Security para user-service.
 * Implementa validación JWT y protección con API Key para endpoints internos.
 * 
 * CLEAN ARCHITECTURE: Usa adaptadores (filtros) para seguridad.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalApiKeyFilter internalApiKeyFilter;

    /**
     * Configura el PasswordEncoder con BCrypt.
     * Usa un strength de 12 para mayor seguridad.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Define qué endpoints son públicos y cuáles requieren autenticación.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Endpoints internos protegidos con API Key (filtro InternalApiKeyFilter)
                .requestMatchers(HttpMethod.POST, "/internal/users/validate").permitAll()
                .requestMatchers(HttpMethod.POST, "/internal/users").permitAll()
                .requestMatchers(HttpMethod.PUT, "/internal/users/**").permitAll()
                // Documentación Swagger - público
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                // Actuator - público
                .requestMatchers("/actuator/**").permitAll()
                // Todos los demás endpoints requieren autenticación JWT
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"No autenticado\", \"message\": \"" + authException.getMessage() + "\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Acceso denegado\", \"message\": \"" + accessDeniedException.getMessage() + "\"}");
                }))
            // Agregar filtro de API Key ANTES del filtro JWT
            .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            // Agregar filtro JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
