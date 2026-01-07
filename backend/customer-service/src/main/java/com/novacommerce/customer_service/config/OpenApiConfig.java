package com.novacommerce.customer_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${app.name:Customer Service}") String appName,
            @Value("${app.description:Servicio de gestión de clientes para Nova Commerce}") String appDescription,
            @Value("${app.version:1.0.0}") String appVersion,
            @Value("${app.contact.name:Nova Commerce}") String contactName,
            @Value("${app.contact.email:yesid.perez@sofka.com.co}") String contactEmail,
            @Value("${app.contact.url:https://novacomerce.com}") String contactUrl
    ) {
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(appDescription)
                        .contact(new Contact().name(contactName).email(contactEmail).url(contactUrl))
                );
    }
}
