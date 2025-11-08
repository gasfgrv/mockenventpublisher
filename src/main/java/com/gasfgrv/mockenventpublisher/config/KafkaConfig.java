package com.gasfgrv.mockenventpublisher.config;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(KafkaProperties properties) {
        Map<String, Object> kafkaProps = properties.buildProducerProperties();
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProps));
    }

}
