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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

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
            var key = generateKey(dto);
            var producerRecord = new ProducerRecord<>(dto.topic(), key, message);
            producerRecord.headers().add("created_at", LocalDateTime.now().toString().getBytes());
            var send = kafkaTemplate.send(producerRecord);
            var join = send.whenComplete(this::printSendStatus).join();
            return join.getProducerRecord().value();
        } catch (ClassNotFoundException | IOException e) {
            log.error("Error processing message for topic {}: {}", dto.topic(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String generateKey(KafkaEventDTO dto) {
        try {
            byte[] bytes = dto.toString().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : md5.digest(bytes)) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
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
