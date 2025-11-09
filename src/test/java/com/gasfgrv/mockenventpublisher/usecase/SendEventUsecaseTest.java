package com.gasfgrv.mockenventpublisher.usecase;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.publisher.KafkaEventProducer;
import com.gasfgrv.mockenventpublisher.validator.EventDataIsValidValidator;
import com.gasfgrv.mockenventpublisher.validator.TopicExistsValidator;
import com.gasfgrv.mockenventpublisher.validator.Validator;
import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEventUsecaseTest {

    @Mock
    private EventDataIsValidValidator eventDataIsValidValidator;

    @Mock
    private TopicExistsValidator topicExistsValidator;

    @Mock
    private KafkaEventProducer producer;

    @Mock
    private SpecificRecord record;

    private SendEventUsecase usecase;

    @BeforeEach
    void setUp() {
        Set<Validator> validatorSet = Set.of(eventDataIsValidValidator, topicExistsValidator);
        this.usecase = new SendEventUsecase(validatorSet, producer);
    }

    @Test
    @DisplayName("Should send event successfully when all validators pass")
    void shouldSendEventSuccessfullyWhenAllValidatorsPass() {
        KafkaEventDTO dto = mountDTO();

        doNothing().when(eventDataIsValidValidator).validate(dto);
        doNothing().when(topicExistsValidator).validate(dto);
        when(producer.sendMessage(dto)).thenReturn(record);

        String execute = usecase.execute(dto);

        assertFalse(execute.isEmpty());
        assertEquals(record.toString(), execute);
        verify(eventDataIsValidValidator).validate(dto);
        verify(topicExistsValidator).validate(dto);
        verify(producer).sendMessage(dto);
    }

    @Test
    @DisplayName("Should throw exception when a validator fails")
    void shouldThrowExceptionWhenValidatorFails() {
        KafkaEventDTO dto = mountDTO();
        doThrow(new RuntimeException("Validation failed")).when(eventDataIsValidValidator).validate(dto);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> usecase.execute(dto));

        assertEquals("Validation failed", exception.getMessage());
        verify(producer, never()).sendMessage(dto);
    }

    @Test
    @DisplayName("Should throw exception when producer fails to send message")
    void shouldThrowExceptionWhenProducerFails() {
        KafkaEventDTO dto = mountDTO();

        doNothing().when(eventDataIsValidValidator).validate(dto);
        doNothing().when(topicExistsValidator).validate(dto);
        doThrow(new RuntimeException("Producer failed")).when(producer).sendMessage(dto);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> usecase.execute(dto));

        assertEquals("Producer failed", exception.getMessage());
        verify(eventDataIsValidValidator).validate(dto);
        verify(topicExistsValidator).validate(dto);
        verify(producer).sendMessage(dto);
    }

    private KafkaEventDTO mountDTO() {
        return new KafkaEventDTO("test-topic",
                "com.example.TestSchema",
                Map.of("key", "value"));
    }

}