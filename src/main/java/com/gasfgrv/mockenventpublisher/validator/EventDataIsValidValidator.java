package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import org.springframework.stereotype.Component;

@Component
public class EventDataIsValidValidator implements Validator {

    @Override
    public void validate(KafkaEventDTO dto) {
        if (dto.topic().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        if (dto.schema().isEmpty()) {
            throw new IllegalArgumentException("Schema cannot be empty");
        }
        if (dto.message() == null || dto.message().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }

}
