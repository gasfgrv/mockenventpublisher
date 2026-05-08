package com.gasfgrv.mockenventpublisher.infrastructure.config;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(DefaultKafkaProducerFactory<String, SpecificRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
