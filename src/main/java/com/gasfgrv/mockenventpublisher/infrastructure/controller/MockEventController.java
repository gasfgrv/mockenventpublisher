package com.gasfgrv.mockenventpublisher.infrastructure.controller;

import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.application.usecase.SendEventUsecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock-event")
public class MockEventController {

    private static final Logger log = LoggerFactory.getLogger(MockEventController.class);

    private final SendEventUsecase usecase;

    public MockEventController(SendEventUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    public ResponseEntity<String> publishMockEvent(@RequestBody KafkaEventDTO dto) {
        log.info("Received request to publish mock event: {}", dto);
        var response = usecase.execute(dto);
        return ResponseEntity.ok(response);
    }

}
