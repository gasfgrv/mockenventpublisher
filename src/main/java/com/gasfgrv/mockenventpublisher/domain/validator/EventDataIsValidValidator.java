package com.gasfgrv.mockenventpublisher.domain.validator;

import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventDataIsValidValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(EventDataIsValidValidator.class);

    @Override
    public void validate(KafkaEventDTO dto) {
        if (dto.topic().isEmpty()) {
            log.error("Topic cannot be empty");
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        if (dto.schema().isEmpty()) {
            log.error("Schema cannot be empty");
            throw new IllegalArgumentException("Schema cannot be empty");
        }

        if (dto.message() == null || dto.message().isEmpty()) {
            log.error("Message cannot be null or empty");
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }

}
