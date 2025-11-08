package com.gasfgrv.mockenventpublisher.usecase;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.publisher.KafkaEventProducer;
import com.gasfgrv.mockenventpublisher.validator.Validator;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SendEventUsecase {

    private final Set<Validator> validators;
    private final KafkaEventProducer producer;

    public SendEventUsecase(Set<Validator> validators, KafkaEventProducer producer) {
        this.validators = validators;
        this.producer = producer;
    }

    public String execute(KafkaEventDTO dto) {
        validators.forEach(validator -> validator.validate(dto));
        SpecificRecord record = producer.sendMessage(dto);
        return record.toString();
    }

}
