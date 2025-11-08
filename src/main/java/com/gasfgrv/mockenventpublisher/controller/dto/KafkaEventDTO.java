package com.gasfgrv.mockenventpublisher.controller.dto;

import java.util.Map;

public record KafkaEventDTO(String topic, String schema, Map<String, Object> message) {
}
