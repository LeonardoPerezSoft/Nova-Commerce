package com.novacommerce.customer_service.adapter.in.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenValidatorAdapterTest")
class JwtTokenValidatorAdapterTest {

    private JwtTokenValidatorAdapter validator;
    private String secret;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        secret = Base64.getEncoder().encodeToString("my-super-secret-key-for-testing-purposes".getBytes(StandardCharsets.UTF_8));
        validator = new JwtTokenValidatorAdapter(secret);
        signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    @Test
    @DisplayName("givenValidToken_whenValidate_thenReturnClaims")
    void givenValidToken_whenValidate_thenReturnClaims() {
        // GIVEN
        String token = Jwts.builder()
            .subject("user@example.com")
            .claim("authorities", "ROLE_USER,ROLE_ADMIN")
            .signWith(signingKey)
            .compact();

        // WHEN
        Claims claims = validator.validate(token);

        // THEN
        assertNotNull(claims);
        assertEquals("user@example.com", claims.getSubject());
        assertEquals("ROLE_USER,ROLE_ADMIN", claims.get("authorities"));
    }

    @Test
    @DisplayName("givenExpiredToken_whenValidate_thenThrowException")
    void givenExpiredToken_whenValidate_thenThrowException() {
        // GIVEN
        String expiredToken = Jwts.builder()
            .subject("user@example.com")
            .expiration(new Date(System.currentTimeMillis() - 1000)) // expired 1 second ago
            .signWith(signingKey)
            .compact();

        // WHEN & THEN
        assertThrows(JwtException.class, () -> validator.validate(expiredToken));
    }

    @Test
    @DisplayName("givenInvalidToken_whenValidate_thenThrowException")
    void givenInvalidToken_whenValidate_thenThrowException() {
        // GIVEN
        String invalidToken = "invalid.token.here";

        // WHEN & THEN
        assertThrows(JwtException.class, () -> validator.validate(invalidToken));
    }

    @Test
    @DisplayName("givenTokenWithWrongSignature_whenValidate_thenThrowException")
    void givenTokenWithWrongSignature_whenValidate_thenThrowException() {
        // GIVEN
        // Use a strong secret key (256+ bits) - this will create a token with different signature
        String wrongSecretString = "wrong-secret-key-for-testing-purposes-extra-long-for-256bits";
        SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecretString.getBytes(StandardCharsets.UTF_8));
        String tokenWithWrongKey = Jwts.builder()
            .subject("user@example.com")
            .signWith(wrongKey)
            .compact();

        // WHEN & THEN
        assertThrows(JwtException.class, () -> validator.validate(tokenWithWrongKey));
    }

    @Test
    @DisplayName("givenValidTokenWithAuthoritiesAsString_whenExtractAuthorities_thenReturnList")
    void givenValidTokenWithAuthoritiesAsString_whenExtractAuthorities_thenReturnList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", "ROLE_USER,ROLE_ADMIN")
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("givenValidTokenWithAuthoritiesAsList_whenExtractAuthorities_thenReturnList")
    void givenValidTokenWithAuthoritiesAsList_whenExtractAuthorities_thenReturnList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", List.of("ROLE_USER", "ROLE_CUSTOMER"))
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_CUSTOMER"));
    }

    @Test
    @DisplayName("givenTokenWithoutAuthorities_whenExtractAuthorities_thenReturnEmptyList")
    void givenTokenWithoutAuthorities_whenExtractAuthorities_thenReturnEmptyList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("givenTokenWithNullAuthorities_whenExtractAuthorities_thenReturnEmptyList")
    void givenTokenWithNullAuthorities_whenExtractAuthorities_thenReturnEmptyList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", null)
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("givenTokenWithEmptyStringAuthorities_whenExtractAuthorities_thenReturnEmptyList")
    void givenTokenWithEmptyStringAuthorities_whenExtractAuthorities_thenReturnEmptyList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", "")
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    @DisplayName("givenTokenWithAuthoritiesAsMap_whenExtractAuthorities_thenReturnList")
    void givenTokenWithAuthoritiesAsMap_whenExtractAuthorities_thenReturnList() {
        // GIVEN
        Claims claims = Jwts.claims()
            .subject("user@example.com")
            .add("authorities", Map.of("roles", List.of("ROLE_ADMIN", "ROLE_OPERATOR")))
            .build();

        // WHEN
        List<String> authorities = validator.extractAuthorities(claims);

        // THEN
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_OPERATOR"));
    }

    @Test
    @DisplayName("givenValidToken_whenMultipleCalls_thenConsistent")
    void givenValidToken_whenMultipleCalls_thenConsistent() {
        // GIVEN
        String token = Jwts.builder()
            .subject("user@example.com")
            .claim("authorities", "ROLE_USER")
            .signWith(signingKey)
            .compact();

        // WHEN
        Claims claims1 = validator.validate(token);
        Claims claims2 = validator.validate(token);

        // THEN
        assertEquals(claims1.getSubject(), claims2.getSubject());
        assertEquals(claims1.get("authorities"), claims2.get("authorities"));
    }
}
