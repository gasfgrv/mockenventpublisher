package com.gasfgrv.mockenventpublisher;

import org.springframework.boot.SpringApplication;

public class TestMockEnventPublisherApplication {

	public static void main(String[] args) {
		SpringApplication.from(MockEnventPublisherApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
