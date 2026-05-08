package com.gasfgrv.mockenventpublisher.infrastructure.publisher;

import com.exemplo.User;
import com.gasfgrv.mockenventpublisher.TestcontainersConfiguration;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.mock.KafkaEventDTOMock;
import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class KafkaEventProducerTest {

    @Autowired
    private KafkaEventProducer producer;

    @Test
    @DisplayName("Should send message to Kafka and return the sent message")
    void shouldSendMessageToKafkaAndReturnTheSent() {
        var event = KafkaEventDTOMock.generate();

        var specificRecord = producer.sendMessage(event);

        assertThat(specificRecord).isNotNull().isInstanceOf(User.class);

        var dataSent = (User) specificRecord;
        assertThat(dataSent.get(0)).isEqualTo(event.message().get("id"));
        assertThat(dataSent.get(1)).isEqualTo(event.message().get("name"));
        assertThat(dataSent.get(2)).isEqualTo(event.message().get("age"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when class not found")
    void shouldThrowRuntimeExceptionWhenClassNotFound() {
        var event = KafkaEventDTOMock.generate();
        var wrongEvent = new KafkaEventDTO(event.topic(), "com.exemplo.NonExistentClass", event.message());

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> producer.sendMessage(wrongEvent))
                .withMessageContaining("com.exemplo.NonExistentClass");
    }

}
