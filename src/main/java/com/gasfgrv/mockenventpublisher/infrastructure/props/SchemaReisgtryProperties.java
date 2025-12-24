package com.gasfgrv.mockenventpublisher.infrastructure.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.properties.schema.registry")
public record SchemaReisgtryProperties(String url) {
}
