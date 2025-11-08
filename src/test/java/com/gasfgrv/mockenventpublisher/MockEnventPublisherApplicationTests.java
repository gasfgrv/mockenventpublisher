package com.gasfgrv.mockenventpublisher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MockEnventPublisherApplicationTests {

	@Test
	void contextLoads() {
	}

}
