package com.novacommerce.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceApplicationTests {

    // Evitar instanciación del bean real de ProductEntityMapper durante el contexto
    @org.springframework.boot.test.mock.mockito.MockBean(name = "productEntityMapperImpl")
    private com.novacommerce.product_service.repository.mapper.ProductEntityMapper productEntityMapper;

	@Test
	void contextLoads() {
	}

}
