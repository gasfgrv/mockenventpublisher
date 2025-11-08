package com.gasfgrv.mockenventpublisher.config;

import com.gasfgrv.mockenventpublisher.config.props.SchemaReisgtryProperties;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaRegistryConfig {

    private final SchemaReisgtryProperties properties;

    public SchemaRegistryConfig(SchemaReisgtryProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SchemaRegistryClient schemaRegistryClient() {
        return new CachedSchemaRegistryClient(properties.url(), 100);
    }


}
