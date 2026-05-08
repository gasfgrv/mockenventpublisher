package com.gasfgrv.mockenventpublisher.mock;

import com.exemplo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.Map;

public class KafkaEventDTOMock {

    public static KafkaEventDTO generate() {
        try {
            var user = UserMock.generate();

            var userJson = new ObjectMapper().<Map<String, Object>>readValue(user.toString(), new TypeReference<>() {
            });

            return Instancio.of(KafkaEventDTO.class)
                    .set(Select.field(KafkaEventDTO::topic), "Usuarios")
                    .set(Select.field(KafkaEventDTO::schema), "com.exemplo.User")
                    .set(Select.field(KafkaEventDTO::message), userJson)
                    .create();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize user", e);
        }

    }

}
