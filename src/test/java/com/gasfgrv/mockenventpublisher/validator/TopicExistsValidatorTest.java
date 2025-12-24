package com.gasfgrv.mockenventpublisher.validator;

import com.gasfgrv.mockenventpublisher.domain.validator.TopicExistsValidator;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TopicExistsValidatorTest {

    @Mock
    private KafkaAdmin kafkaAdmin;

    @Mock
    private AdminClient adminClient;

    @InjectMocks
    private TopicExistsValidator validator;

    @Test
    @DisplayName("Should pass validation when topic exists")
    void shouldPassValidationWhenTopicExists() {
        try (MockedStatic<AdminClient> adminClientStatic = mockStatic(AdminClient.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("someTopic", "some.schema.ClassName", Map.of("key", "value"));
            ListTopicsResult topics = mock(ListTopicsResult.class);
            KafkaFuture<?> namesFuture = mock(KafkaFuture.class);

            doReturn(Map.of("bootstrap.servers", "localhost:9092")).when(kafkaAdmin).getConfigurationProperties();
            doReturn(topics).when(adminClient).listTopics();
            doReturn(namesFuture).when(topics).names();
            doReturn(Set.of("someTopic")).when(namesFuture).get();

            adminClientStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(adminClient);
            assertDoesNotThrow(() -> validator.validate(dto));
        } catch (InterruptedException | ExecutionException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when topic does not exist")
    void shouldThrowExceptionWhenTopicDoesNotExist() {
        try (MockedStatic<AdminClient> adminClientStatic = mockStatic(AdminClient.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("nonexistent", "some.schema.ClassName", Map.of("key", "value"));
            ListTopicsResult topics = mock(ListTopicsResult.class);
            KafkaFuture<?> namesFuture = mock(KafkaFuture.class);

            doReturn(Map.of("bootstrap.servers", "localhost:9092")).when(kafkaAdmin).getConfigurationProperties();
            doReturn(topics).when(adminClient).listTopics();
            doReturn(namesFuture).when(topics).names();
            doReturn(Set.of("existingTopic")).when(namesFuture).get();

            adminClientStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(adminClient);
            Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));

            assertEquals("Topic nonexistent does not exist in the Kafka cluster.", exception.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should throw RuntimeException when error occurs while checking topics")
    void shouldThrowRuntimeExceptionWhenErrorOccursWhileCheckingTopics() {
        try (MockedStatic<AdminClient> adminClientStatic = mockStatic(AdminClient.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("someTopic", "some.schema.ClassName", Map.of("key", "value"));
            ListTopicsResult topics = mock(ListTopicsResult.class);
            KafkaFuture<?> namesFuture = mock(KafkaFuture.class);

            doReturn(Map.of("bootstrap.servers", "localhost:9092")).when(kafkaAdmin).getConfigurationProperties();
            doReturn(topics).when(adminClient).listTopics();
            doReturn(namesFuture).when(topics).names();
            doThrow(new InterruptedException()).when(namesFuture).get();

            adminClientStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(adminClient);

            Exception exception = assertThrows(RuntimeException.class, () -> validator.validate(dto));
            assertEquals("Error while checking Kafka topics: null", exception.getMessage());
            assertInstanceOf(InterruptedException.class, exception.getCause());
        } catch (ExecutionException | InterruptedException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}