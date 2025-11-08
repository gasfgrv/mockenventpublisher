package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;

public interface Validator {
    void validate(KafkaEventDTO dto);
}
