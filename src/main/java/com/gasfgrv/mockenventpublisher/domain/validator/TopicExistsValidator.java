package com.gasfgrv.mockenventpublisher.domain.validator;

import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
public class TopicExistsValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(TopicExistsValidator.class);

    private final KafkaAdmin kafkaAdmin;

    public TopicExistsValidator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void validate(KafkaEventDTO dto) {
        log.info("Validating existence of topic: {}", dto.topic());
        try (var adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            var topics = adminClient.listTopics();
            var future = topics.names();
            var names = future.get();
            checkTopic(dto.topic(), names);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while checking Kafka topics", e);
            throw new RuntimeException("Error while checking Kafka topics: " + e.getMessage(), e);
        }
    }

    private void checkTopic(String topic, Set<String> names) {
        if (!names.contains(topic)) {
            log.error("Topic {} does not exist in the Kafka cluster.", topic);
            throw new IllegalArgumentException("Topic " + topic + " does not exist in the Kafka cluster.");
        }
    }

}
