package com.novacommerce.auth_service.application.service;

import com.novacommerce.auth_service.application.port.in.AuthenticateUserUseCase;
import com.novacommerce.auth_service.application.port.in.RefreshTokenUseCase;
import com.novacommerce.auth_service.application.port.in.ValidateTokenUseCase;
import com.novacommerce.auth_service.application.port.out.TokenGeneratorPort;
import com.novacommerce.auth_service.application.port.out.UserValidationPort;
import com.novacommerce.auth_service.client.dto.UserValidationRequest;
import com.novacommerce.auth_service.client.dto.UserValidationResponse;
import com.novacommerce.auth_service.web.api.dto.request.LoginRequest;
import com.novacommerce.auth_service.web.api.dto.request.RefreshTokenRequest;
import com.novacommerce.auth_service.web.api.dto.response.LoginResponse;
import com.novacommerce.auth_service.web.api.dto.response.TokenValidationResponse;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidCredentialsException;
import com.novacommerce.auth_service.web.rest.exceptions.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servicio de aplicación que implementa los casos de uso de autenticación
 * Esta clase orquesta las operaciones usando los puertos definidos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements AuthenticateUserUseCase, RefreshTokenUseCase, ValidateTokenUseCase {

    private final UserValidationPort userValidationPort;
    private final TokenGeneratorPort tokenGeneratorPort;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            log.debug("Starting authentication for: {}", loginRequest.userIdentifier());
            
            // Validar credenciales usando el puerto de salida
            UserValidationRequest validationRequest = new UserValidationRequest(
                loginRequest.userIdentifier(),
                loginRequest.password()
            );

            UserValidationResponse userInfo = userValidationPort.validateCredentials(validationRequest);
            
            log.debug("User validated: {}", userInfo.username());

            // Verificar estado del usuario
            if (!userInfo.enabled()) {
                log.warn("User disabled: {}", userInfo.username());
                throw new InvalidCredentialsException("Usuario deshabilitado");
            }

            if (userInfo.locked()) {
                log.warn("User locked: {}", userInfo.username());
                throw new InvalidCredentialsException("Usuario bloqueado");
            }

            // Crear Authentication con roles y permisos
            Set<SimpleGrantedAuthority> authorities = Stream.concat(
                userInfo.roles().stream().map(role -> "ROLE_" + role),
                userInfo.permissions().stream()
            ).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userInfo.username(),
                null,
                authorities
            );

            // Generar tokens usando el puerto
            log.debug("Generating JWT tokens...");
            String accessToken = tokenGeneratorPort.generateToken(authentication);
            String refreshToken = tokenGeneratorPort.generateRefreshToken(userInfo.username());

            log.info("Authentication successful for user: {}", userInfo.username());

            return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtExpirationMs / 1000,
                userInfo.username(),
                userInfo.roles().stream().toList()
            );
                
        } catch (InvalidCredentialsException e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            throw new InvalidCredentialsException("Error al autenticar usuario");
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            log.debug("Refreshing token...");
            
            String refreshToken = refreshTokenRequest.refreshToken();
            
            // Validar el refresh token
            if (!tokenGeneratorPort.validateToken(refreshToken)) {
                throw new InvalidTokenException("Refresh token inválido o expirado");
            }

            // Extraer username del refresh token
            String username = tokenGeneratorPort.getUsernameFromToken(refreshToken);
            log.debug("Refresh token valid for user: {}", username);

            // TODO: Aquí se podría consultar user-service para obtener roles actualizados
            // Por ahora, generamos un nuevo token básico
            
            // Generar nuevo access token (manteniendo las autoridades del token anterior si están disponibles)
            String authorities = tokenGeneratorPort.getAuthoritiesFromToken(refreshToken);
            
            Set<SimpleGrantedAuthority> grantedAuthorities = Set.of();
            if (authorities != null && !authorities.isEmpty()) {
                grantedAuthorities = Stream.of(authorities.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                grantedAuthorities
            );

            String newAccessToken = tokenGeneratorPort.generateToken(authentication);
            
            log.info("Token refreshed successfully for user: {}", username);

            return new LoginResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                jwtExpirationMs / 1000,
                username,
                List.of()
            );
                
        } catch (InvalidTokenException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new InvalidTokenException("Error al refrescar token");
        }
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        try {
            log.debug("Validating token...");
            
            boolean isValid = tokenGeneratorPort.validateToken(token);
            
            if (!isValid) {
                return new TokenValidationResponse(
                    false,
                    null,
                    null
                );
            }

            String username = tokenGeneratorPort.getUsernameFromToken(token);
            String authorities = tokenGeneratorPort.getAuthoritiesFromToken(token);
            
            log.debug("Token valid for user: {} with authorities: {}", username, authorities);

            return new TokenValidationResponse(
                true,
                username,
                authorities
            );
                
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return new TokenValidationResponse(
                false,
                null,
                null
            );
        }
    }
}
