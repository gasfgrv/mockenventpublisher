package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class TopicExistsValidator implements Validator {

    private final KafkaAdmin kafkaAdmin;

    public TopicExistsValidator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void validate(KafkaEventDTO dto) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> names = topics.names().get();
            if (!names.contains(dto.topic())) {
                throw new IllegalArgumentException("Tópico Kafka inexistente: " + dto.topic());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erro ao verificar tópico Kafka", e);
        }
    }

}
