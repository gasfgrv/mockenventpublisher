package com.gasfgrv.mockenventpublisher.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.mockenventpublisher.application.usecase.SendEventUsecase;
import com.gasfgrv.mockenventpublisher.infrastructure.dto.KafkaEventDTO;
import com.gasfgrv.mockenventpublisher.mock.KafkaEventDTOMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockEventController.class)
class MockEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SendEventUsecase usecase;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Should return 200 OK when publishing a mock event")
    void publishMockEvent_Success() throws Exception {
        KafkaEventDTO input = KafkaEventDTOMock.generate();

        when(usecase.execute(input)).thenReturn(mapper.writeValueAsString(input.message()));

        mockMvc.perform(post("/mock-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(input.message().get("id")))
                .andExpect(jsonPath("$.name").value(input.message().get("name")))
                .andExpect(jsonPath("$.age").value(input.message().get("age")));
    }

}
