package com.gasfgrv.mockenventpublisher.domain.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AvroSchemaValidator implements Validator {

    private static final Logger log = LoggerFactory.getLogger(AvroSchemaValidator.class);

    private final SchemaRegistryClient client;
    private final ObjectMapper mapper;

    public AvroSchemaValidator(SchemaRegistryClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public void validate(KafkaEventDTO dto) {
        log.info("Validating message against Avro schema for topic: {}", dto.topic());
        try {
            var schemaMetadata = client.getLatestSchemaMetadata(dto.topic().concat("-value"));
            var parser = new Schema.Parser();
            var schema = parser.parse(schemaMetadata.getSchema());
            var decoderFactory = DecoderFactory.get();
            var decoder = decoderFactory.jsonDecoder(schema, toJsonString(dto.message()));
            var reader = new GenericDatumReader<>(schema);
            reader.read(null, decoder);
        } catch (IOException | RestClientException e) {
            log.error("Message does not conform to Avro schema: {}", e.getMessage());
            throw new IllegalArgumentException("Message does not conform to Avro schema: " + e.getMessage(), e);
        }
    }

    private String toJsonString(Map<String, Object> message) {
        try {
            log.info("Converting message map to JSON string");
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Error converting message to JSON string: {}", e.getMessage());
            throw new RuntimeException("Error converting message to JSON string", e);
        }
    }

}
