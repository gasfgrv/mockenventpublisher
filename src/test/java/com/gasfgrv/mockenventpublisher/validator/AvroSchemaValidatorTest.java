package com.gasfgrv.mockenventpublisher.validator;

import com.exemplo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.domain.validator.AvroSchemaValidator;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AvroSchemaValidatorTest {

    @Mock
    private SchemaRegistryClient client;

    @Spy
    private ObjectMapper mapper;

    @InjectMocks
    private AvroSchemaValidator validator;

    @Test
    @DisplayName("Should validate Avro schema successfully")
    void shouldValidateAvroSchemaSuccessfully() {
        try (MockedStatic<DecoderFactory> decoderFactoryStatic = mockStatic(DecoderFactory.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("Topic", "com.test.Schema", Map.of("field1", "value1", "field2", 123));
            Schema schema = new User().getSchema();

            SchemaMetadata schemaMetadata = mock(SchemaMetadata.class);
            doReturn(schemaMetadata).when(client).getLatestSchemaMetadata(anyString());
            doReturn(schema.toString()).when(schemaMetadata).getSchema();

            DecoderFactory decoderFactory = mock(DecoderFactory.class);
            JsonDecoder decoder = mock(JsonDecoder.class);
            decoderFactoryStatic.when(DecoderFactory::get).thenReturn(decoderFactory);
            doReturn(decoder).when(decoderFactory).jsonDecoder(eq(schema), anyString());

            assertDoesNotThrow(() -> validator.validate(dto));

            verify(mapper).writeValueAsString(dto.message());
        } catch (RestClientException | IOException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Should throw exception for invalid Avro schema")
    void shouldThrowExceptionForInvalidAvroSchema() {
        try (MockedStatic<DecoderFactory> decoderFactoryStatic = mockStatic(DecoderFactory.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("Topic", "com.test.Schema", Map.of("field1", "value1", "field2", 123));

            doThrow(IOException.class).when(client).getLatestSchemaMetadata(anyString());

            Exception exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(dto));
            assertInstanceOf(IOException.class, exception.getCause());

            verify(mapper, never()).writeValueAsString(dto.message());
        } catch (RestClientException | IOException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Should throw RuntimeException when JSON conversion fails")
    void shouldThrowRuntimeExceptionWhenJsonConversionFails() {
        try (MockedStatic<DecoderFactory> decoderFactoryStatic = mockStatic(DecoderFactory.class)) {
            KafkaEventDTO dto = new KafkaEventDTO("Topic", "com.test.Schema", Map.of("field1", "value1", "field2", 123));
            Schema schema = new User().getSchema();

            SchemaMetadata schemaMetadata = mock(SchemaMetadata.class);
            doReturn(schemaMetadata).when(client).getLatestSchemaMetadata(anyString());
            doReturn(schema.toString()).when(schemaMetadata).getSchema();

            DecoderFactory decoderFactory = mock(DecoderFactory.class);
            decoderFactoryStatic.when(DecoderFactory::get).thenReturn(decoderFactory);

            doThrow(JsonProcessingException.class).when(mapper).writeValueAsString(anyMap());

            Exception exception = assertThrows(RuntimeException.class, () -> validator.validate(dto));
            assertInstanceOf(JsonProcessingException.class, exception.getCause());

            verify(mapper).writeValueAsString(dto.message());
        } catch (RestClientException | IOException e) {
            fail("Unexpected exception: " + e.getMessage(), e);
        }
    }

}