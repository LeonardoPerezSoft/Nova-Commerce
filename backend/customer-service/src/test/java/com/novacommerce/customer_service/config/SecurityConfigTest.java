package com.novacommerce.customer_service.config;

import com.novacommerce.customer_service.adapter.in.security.InternalApiKeyFilter;
import com.novacommerce.customer_service.adapter.in.security.JwtAuthenticationFilter;
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
 * se construye correctamente.
 */
@SpringBootTest(classes = SecurityConfigTest.TestApp.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration",
    "app.jwt.secret=aW0tYS1zdXBlci1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLWV4dHJhLWxvbmctb25l",
    "app.jwt.internal-api-key=nova-internal-service-key-2024"
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
