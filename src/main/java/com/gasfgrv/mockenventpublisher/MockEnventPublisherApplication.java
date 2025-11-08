package com.gasfgrv.mockenventpublisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MockEnventPublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockEnventPublisherApplication.class, args);
    }

}
