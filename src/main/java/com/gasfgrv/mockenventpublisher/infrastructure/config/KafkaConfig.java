package com.gasfgrv.mockenventpublisher.infrastructure.config;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(KafkaProperties properties) {
        var kafkaProps = properties.buildProducerProperties();
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProps));
    }

}
