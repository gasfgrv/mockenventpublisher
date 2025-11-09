package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
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
            KafkaFuture<Set<String>> future = topics.names();
            Set<String> names = future.get();
            checkTopic(dto.topic(), names);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while checking Kafka topics: " + e.getMessage(), e);
        }
    }

    private void checkTopic(String topic, Set<String> names) {
        if (!names.contains(topic)) {
            throw new IllegalArgumentException("Topic " + topic + " does not exist in the Kafka cluster.");
        }
    }

}
