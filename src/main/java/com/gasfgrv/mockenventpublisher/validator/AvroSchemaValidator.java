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
            Schema schema = getSchema(dto);
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, toJsonString(dto.message()));
            DatumReader<GenericData.Record> reader = new GenericDatumReader<>(schema);
            reader.read(null, decoder);
        } catch (IOException | RestClientException e) {
            throw new IllegalArgumentException("Mensagem inválida para o schema Avro do tópico " + dto.topic() + ": " + e.getMessage(), e);
        }
    }

    private String toJsonString(Map<String, Object> message) throws JsonProcessingException {
        return mapper.writeValueAsString(message);
    }

    private Schema getSchema(KafkaEventDTO dto) throws IOException, RestClientException {
        SchemaMetadata schemaMetadata = client.getLatestSchemaMetadata(dto.topic().concat("-value"));
        return new Schema.Parser().parse(schemaMetadata.getSchema());
    }

}
