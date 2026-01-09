package com.novacommerce.auth_service.client.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceFeignConfig Tests")
class UserServiceFeignConfigTest {

    private UserServiceFeignConfig config;

    @BeforeEach
    void setUp() {
        config = new UserServiceFeignConfig();
        ReflectionTestUtils.setField(config, "internalApiKey", "test-api-key-123");
    }

    @Test
    @DisplayName("Debe crear RequestInterceptor bean")
    void testInternalApiKeyInterceptorBean() {
        RequestInterceptor interceptor = config.internalApiKeyInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    @DisplayName("Debe añadir header X-Internal-API-Key en interceptor")
    void testInterceptorAddsApiKeyHeader() {
        RequestInterceptor interceptor = config.internalApiKeyInterceptor();
        RequestTemplate template = mock(RequestTemplate.class);

        interceptor.apply(template);

        verify(template, times(1)).header(eq("X-Internal-API-Key"), eq("test-api-key-123"));
    }

    @Test
    @DisplayName("Debe funcionar con API key vacía")
    void testWithEmptyApiKey() {
        ReflectionTestUtils.setField(config, "internalApiKey", "");
        RequestInterceptor interceptor = config.internalApiKeyInterceptor();
        RequestTemplate template = mock(RequestTemplate.class);

        interceptor.apply(template);

        verify(template).header(eq("X-Internal-API-Key"), eq(""));
    }

    @Test
    @DisplayName("Debe funcionar con API key nula")
    void testWithNullApiKey() {
        ReflectionTestUtils.setField(config, "internalApiKey", null);
        RequestInterceptor interceptor = config.internalApiKeyInterceptor();
        RequestTemplate template = mock(RequestTemplate.class);

        interceptor.apply(template);

        verify(template).header(eq("X-Internal-API-Key"), (String) isNull());
    }

    @Test
    @DisplayName("Debe añadir header en múltiples invocaciones")
    void testMultipleInvocations() {
        RequestInterceptor interceptor = config.internalApiKeyInterceptor();
        RequestTemplate template1 = mock(RequestTemplate.class);
        RequestTemplate template2 = mock(RequestTemplate.class);

        interceptor.apply(template1);
        interceptor.apply(template2);

        verify(template1).header(eq("X-Internal-API-Key"), eq("test-api-key-123"));
        verify(template2).header(eq("X-Internal-API-Key"), eq("test-api-key-123"));
    }

    @Test
    @DisplayName("Debe usar valor de @Value para internalApiKey")
    void testValueAnnotation() throws NoSuchFieldException {
        var field = UserServiceFeignConfig.class.getDeclaredField("internalApiKey");
        var valueAnnotation = field.getAnnotation(org.springframework.beans.factory.annotation.Value.class);
        
        assertNotNull(valueAnnotation);
        assertEquals("${app.jwt.internal-api-key}", valueAnnotation.value());
    }

    @Test
    @DisplayName("Debe tener @Bean en método internalApiKeyInterceptor")
    void testBeanAnnotation() throws NoSuchMethodException {
        var method = UserServiceFeignConfig.class.getMethod("internalApiKeyInterceptor");
        var beanAnnotation = method.getAnnotation(org.springframework.context.annotation.Bean.class);
        
        assertNotNull(beanAnnotation);
    }

    @Test
    @DisplayName("Debe tener @Configuration en clase")
    void testConfigurationAnnotation() {
        var configAnnotation = UserServiceFeignConfig.class.getAnnotation(
            org.springframework.context.annotation.Configuration.class
        );
        
        assertNotNull(configAnnotation);
    }
}
