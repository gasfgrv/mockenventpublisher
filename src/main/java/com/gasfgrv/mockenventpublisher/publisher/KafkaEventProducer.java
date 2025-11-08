package com.gasfgrv.mockenventpublisher.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final ObjectMapper mapper;

    public KafkaEventProducer(KafkaTemplate<String, SpecificRecord> kafkaTemplate, ObjectMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public SpecificRecord sendMessage(KafkaEventDTO dto) {
        try {
            Class<?> schemaClass = Class.forName(dto.schema());
            String messageJson = mapper.writeValueAsString(dto.message());
            SpecificRecord message = mapper.readValue(messageJson, schemaClass.asSubclass(SpecificRecord.class));

            ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<>(dto.topic(), message);
            CompletableFuture<SendResult<String, SpecificRecord>> send = kafkaTemplate.send(producerRecord);
            SendResult<String, SpecificRecord> join = send.whenComplete((result, ex) -> {
                if (ex != null) {
                    System.out.println("Error sending message: " + ex.getMessage());
                } else {
                    System.out.println("Message sent successfully to topic: " + result.getRecordMetadata().topic());
                }
            }).join();
            return join.getProducerRecord().value();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
