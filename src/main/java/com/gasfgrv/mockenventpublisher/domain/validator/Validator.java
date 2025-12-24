package com.gasfgrv.mockenventpublisher.domain.validator;

import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;

public interface Validator {
    void validate(KafkaEventDTO dto);
}
