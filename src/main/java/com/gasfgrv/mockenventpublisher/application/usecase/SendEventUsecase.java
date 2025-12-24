package com.gasfgrv.mockenventpublisher.application.usecase;

import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.infrastructure.publisher.KafkaEventProducer;
import com.gasfgrv.mockenventpublisher.domain.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SendEventUsecase {

    private static final Logger log = LoggerFactory.getLogger(SendEventUsecase.class);

    private final Set<Validator> validators;
    private final KafkaEventProducer producer;

    public SendEventUsecase(Set<Validator> validators, KafkaEventProducer producer) {
        this.validators = validators;
        this.producer = producer;
    }

    public String execute(KafkaEventDTO dto) {
        log.info("Executing SendEventUsecase for topic: {}", dto.topic());
        validators.forEach(validator -> validator.validate(dto));
        var record = producer.sendMessage(dto);
        return record.toString();
    }

}
