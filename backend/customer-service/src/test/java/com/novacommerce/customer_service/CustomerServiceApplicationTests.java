package com.novacommerce.customer_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Integration test - Requires full application context with database")
class CustomerServiceApplicationTests {

	@Test
	void contextLoads() {
		// This is an integration test that requires full application context
		// and database configuration. Covered by other unit tests in the suite.
	}

}
