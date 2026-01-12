package com.novacommerce.product_service.config;

import com.novacommerce.product_service.adapter.in.web.mapper.CategoryDtoMapper;
import com.novacommerce.product_service.adapter.in.web.mapper.ProductDtoMapper;
import com.novacommerce.product_service.repository.mapper.CategoryEntityMapper;
import com.novacommerce.product_service.repository.mapper.ProductEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("MapperConfig Tests")
class MapperConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private ProductDtoMapper productDtoMapper;

    @Autowired(required = false)
    private CategoryDtoMapper categoryDtoMapper;

    @Autowired(required = false)
    private ProductEntityMapper productEntityMapper;

    @Autowired(required = false)
    private CategoryEntityMapper categoryEntityMapper;

    @org.springframework.boot.test.mock.mockito.MockBean(name = "productEntityMapperImpl")
    private ProductEntityMapper mockedProductEntityMapper;

    @Test
    @DisplayName("Should load MapperConfig as Configuration")
    void testMapperConfigIsConfiguration() {
        MapperConfig config = applicationContext.getBean(MapperConfig.class);
        
        assertNotNull(config);
        assertTrue(MapperConfig.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    @DisplayName("Should have ProductDtoMapper bean available")
    void testProductDtoMapperAvailable() {
        assertNotNull(productDtoMapper, "ProductDtoMapper should be available in context");
    }

    @Test
    @DisplayName("Should have CategoryDtoMapper bean available")
    void testCategoryDtoMapperAvailable() {
        assertNotNull(categoryDtoMapper, "CategoryDtoMapper should be available in context");
    }

    @Test
    @DisplayName("Should have ProductEntityMapper bean available")
    void testProductEntityMapperAvailable() {
        assertNotNull(productEntityMapper, "ProductEntityMapper should be available in context");
    }

    @Test
    @DisplayName("Should have CategoryEntityMapper bean available")
    void testCategoryEntityMapperAvailable() {
        assertNotNull(categoryEntityMapper, "CategoryEntityMapper should be available in context");
    }

    @Test
    @DisplayName("Should have MapperConfig bean in application context")
    void testMapperConfigBeanExists() {
        assertTrue(applicationContext.containsBean("mapperConfig"));
    }

    @Test
    @DisplayName("Should have all mapper beans registered")
    void testAllMapperBeansRegistered() {
        assertAll(
            () -> assertNotNull(productDtoMapper, "ProductDtoMapper should be registered"),
            () -> assertNotNull(categoryDtoMapper, "CategoryDtoMapper should be registered"),
            () -> assertNotNull(productEntityMapper, "ProductEntityMapper should be registered"),
            () -> assertNotNull(categoryEntityMapper, "CategoryEntityMapper should be registered")
        );
    }

    @Test
    @DisplayName("Should create MapperConfig instance")
    void testMapperConfigInstantiation() {
        MapperConfig config = new MapperConfig();
        
        assertNotNull(config);
    }

    @Test
    @DisplayName("Should have Configuration annotation on MapperConfig class")
    void testConfigurationAnnotation() {
        assertTrue(MapperConfig.class.isAnnotationPresent(Configuration.class));
    }

    @Test
    @DisplayName("Should retrieve MapperConfig from context")
    void testGetMapperConfigFromContext() {
        MapperConfig config = applicationContext.getBean(MapperConfig.class);
        
        assertNotNull(config);
        assertSame(config, applicationContext.getBean(MapperConfig.class), 
            "MapperConfig should be singleton");
    }
}
