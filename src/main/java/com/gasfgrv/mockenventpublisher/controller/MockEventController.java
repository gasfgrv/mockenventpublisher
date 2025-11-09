package com.gasfgrv.mockenventpublisher.controller;

import com.gasfgrv.mockenventpublisher.controller.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.usecase.SendEventUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/mock-event")
public class MockEventController {

    private final SendEventUsecase usecase;

    public MockEventController(SendEventUsecase usecase) {
        this.usecase = usecase;
    }

    @PostMapping
    public ResponseEntity<String> publishMockEnvent(@RequestBody KafkaEventDTO dto) throws IOException, ClassNotFoundException {
        String response = usecase.execute(dto);
        return ResponseEntity.ok(response);
    }

}
