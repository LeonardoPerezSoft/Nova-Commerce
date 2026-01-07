package com.novacommerce.user_service.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuración vacía - Los mappers de MapStruct se registran automáticamente
 * gracias a componentModel = "spring" en las anotaciones @Mapper.
 */
@Configuration
public class MapperConfig {
    // MapStruct registra automáticamente los beans con componentModel = "spring"
}
