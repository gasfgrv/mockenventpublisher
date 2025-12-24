package com.gasfgrv.mockenventpublisher.infrastructure.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KafkaEventProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final ObjectMapper mapper;

    public KafkaEventProducer(KafkaTemplate<String, SpecificRecord> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public SpecificRecord sendMessage(KafkaEventDTO dto) {
        try {
            log.info("Serializing message for topic: {}", dto.topic());
            var schemaClass = Class.forName(dto.schema());
            var messageJson = mapper.writeValueAsString(dto.message());
            var message = mapper.readValue(messageJson, schemaClass.<SpecificRecord>asSubclass(SpecificRecord.class));

            log.info("Sending message to topic: {}", dto.topic());
            var producerRecord = new ProducerRecord<String, SpecificRecord>(dto.topic(), message);
            var send = kafkaTemplate.send(producerRecord);
            var join = send.whenComplete(this::printSendStatus).join();
            return join.getProducerRecord().value();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printSendStatus(SendResult<String, SpecificRecord> result, Throwable ex) {
        if (ex != null) {
            log.info("Error sending message: {}", ex.getMessage());
        } else {
            log.info("Message sent successfully to topic: {}", result.getRecordMetadata().topic());
        }
    }

}
