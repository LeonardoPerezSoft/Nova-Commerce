package com.novacommerce.order_service.config;

import com.novacommerce.order_service.adapter.in.security.InternalApiKeyFilter;
import com.novacommerce.order_service.adapter.in.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de contexto para SecurityConfig que asegura que el bean SecurityFilterChain
 * se construye correctamente (lo que ejecuta el método filterChain y cubre el archivo).
 */
@SpringBootTest(classes = SecurityConfigTest.TestApp.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    // Propiedades mínimas para evitar resoluciones faltantes en otros beans opcionales
    "spring.main.allow-bean-definition-overriding=true",
    // Desactivar auto-configs de BD/Liquibase para no requerir datasource ni changelogs
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
})
class SecurityConfigTest {

    @EnableAutoConfiguration
    static class TestApp { }

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    InternalApiKeyFilter internalApiKeyFilter;

    @Autowired
    SecurityFilterChain securityFilterChain;

    @Test
    @WithMockUser
    void contextLoads_andSecurityFilterChainBeanExists() {
        assertNotNull(securityFilterChain);
    }
}
