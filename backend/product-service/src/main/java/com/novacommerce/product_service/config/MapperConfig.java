package com.novacommerce.product_service.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for MapStruct mappers.
 * This class ensures that all MapStruct mappers are properly configured as Spring beans.
 */
@Configuration
public class MapperConfig {
    // MapStruct configuration is handled via annotation processor
    // All mappers use @Mapper(componentModel = "spring") for Spring integration
}

/**
 * Shared MapStruct configuration interface.
 * Defines common configuration for all MapStruct mappers in the application.
 */
@org.mapstruct.MapperConfig(componentModel = "spring")
interface MapstructSpringConfig {
    // This interface serves as a shared configuration for all MapStruct mappers
    // Mappers can reference this config using: @Mapper(config = MapstructSpringConfig.class)
}
