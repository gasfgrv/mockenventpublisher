package com.gasfgrv.mockenventpublisher;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MockEnventPublisherApplicationTests {

    private final ApplicationContext applicationContext;

    MockEnventPublisherApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void contextLoads() {
        Set<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames()).collect(Collectors.toSet());
        assertTrue(beans.contains("kafkaTemplate"));
        assertTrue(beans.contains("schemaRegistryClient"));
    }

}
