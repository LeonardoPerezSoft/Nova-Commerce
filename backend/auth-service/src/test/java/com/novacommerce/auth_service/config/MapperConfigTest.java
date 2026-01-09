package com.novacommerce.auth_service.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MapperConfig.class)
@DisplayName("MapperConfig Tests")
class MapperConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should load MapperConfig as Spring bean")
    void testMapperConfigIsLoaded() {
        // When
        MapperConfig mapperConfig = applicationContext.getBean(MapperConfig.class);

        // Then
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be a Spring Configuration")
    void testIsConfiguration() {
        // When
        boolean hasConfigurationAnnotation = MapperConfig.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class
        );

        // Then
        assertTrue(hasConfigurationAnnotation);
    }

    @Test
    @DisplayName("Should create MapperConfig instance")
    void testMapperConfigInstantiation() {
        // When
        MapperConfig mapperConfig = new MapperConfig();

        // Then
        assertNotNull(mapperConfig);
    }

    @Test
    @DisplayName("Should be singleton by default")
    void testMapperConfigIsSingleton() {
        // When
        MapperConfig bean1 = applicationContext.getBean(MapperConfig.class);
        MapperConfig bean2 = applicationContext.getBean(MapperConfig.class);

        // Then
        assertSame(bean1, bean2);
    }
}
