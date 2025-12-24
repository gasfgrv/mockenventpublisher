package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.domain.validator.EventDataIsValidValidator;
import com.gasfgrv.mockenventpublisher.domain.validator.Validator;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventDataIsValidValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        this.validator = new EventDataIsValidValidator();
    }

    @Test
    @DisplayName("Should validate successfully when all fields are valid")
    void validate_ShouldPass_WhenAllFieldsAreValid() {
        KafkaEventDTO dto = createDto(false, false, false, false);
        assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when topic is empty")
    void validate_ShouldThrowException_WhenTopicIsEmpty() {
        KafkaEventDTO dto = createDto(true, false, false, false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
        assertEquals("Topic cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when schema is empty")
    void validate_ShouldThrowException_WhenSchemaIsEmpty() {
        KafkaEventDTO dto = createDto(false, true, false, false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
        assertEquals("Schema cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when message is null")
    void validate_ShouldThrowException_WhenMessageIsNull() {
        KafkaEventDTO dto = createDto(false, false, true, false);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
        assertEquals("Message cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when message is empty")
    void validate_ShouldThrowException_WhenMessageIsEmpty() {
        KafkaEventDTO dto = createDto(false, false, false, true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
        assertEquals("Message cannot be null or empty", exception.getMessage());
    }


    private KafkaEventDTO createDto(boolean emptyTopic, boolean emptySchema, boolean nullMessage, boolean emptyMessage) {
        String topic = emptyTopic ? "" : "valid-topic";
        String schema = emptySchema ? "" : "com.test.Schema";
        Map<String, Object> message;

        if (nullMessage) {
            message = null;
        } else if (emptyMessage) {
            message = Collections.emptyMap();
        } else {
            message = Collections.singletonMap("key", "value");
        }

        return new KafkaEventDTO(topic, schema, message);
    }

}