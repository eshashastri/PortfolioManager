package com.pm.service;

import com.pm.dto.PredictionResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PredictionServiceTest {

    // ==========================
    // SUCCESS CASE
    // ==========================
    @Test
    void getPrediction_success() {

        RestTemplate restTemplate =
                mock(RestTemplate.class);

        PredictionResponseDTO dto =
                new PredictionResponseDTO();

        // FIX: use doReturn
        doReturn(dto)
                .when(restTemplate)
                .getForObject(
                        any(URI.class),
                        eq(PredictionResponseDTO.class)
                );

        PredictionService service =
                new PredictionService(restTemplate);

        PredictionResponseDTO result =
                service.getPrediction("AAPL");

        assertNotNull(result);
    }


}
