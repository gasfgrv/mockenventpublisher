package com.gasfgrv.mockenventpublisher.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AvroSchemaValidator implements Validator {

    private final SchemaRegistryClient client;
    private final ObjectMapper mapper;

    public AvroSchemaValidator(SchemaRegistryClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public void validate(KafkaEventDTO dto) {
        try {
            SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(dto.topic().concat("-value"));
            Schema.Parser parser = new Schema.Parser();
            Schema schema = parser.parse(schemaMetadata.getSchema());
            DecoderFactory decoderFactory = DecoderFactory.get();
            Decoder decoder = decoderFactory.jsonDecoder(schema, toJsonString(dto.message()));
            DatumReader<GenericData.Record> reader = new GenericDatumReader<>(schema);
            reader.read(null, decoder);
        } catch (IOException | RestClientException e) {
            throw new IllegalArgumentException("Message does not conform to Avro schema: " + e.getMessage(), e);
        }
    }

    private String toJsonString(Map<String, Object> message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting message to JSON string", e);
        }
    }

}
