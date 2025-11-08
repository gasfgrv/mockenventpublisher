package com.gasfgrv.mockenventpublisher.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "spring.kafka.properties.schema.registry")
public record SchemaReisgtryProperties(String url) {
}
