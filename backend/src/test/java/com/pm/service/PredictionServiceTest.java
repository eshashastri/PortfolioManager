package com.pm.service;

import com.pm.dto.PredictionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PredictionService predictionService;

    @BeforeEach
    void setup() {
        // Inject base URL manually since @Value doesn't work in unit tests
        ReflectionTestUtils.setField(
                predictionService,
                "predictorBaseUrl",
                "http://localhost:5001"
        );
    }

    // ==========================
    // SUCCESS CASE
    // ==========================
    @Test
    void getPrediction_success() {

        PredictionResponseDTO mockResponse =
                new PredictionResponseDTO();

        doReturn(mockResponse)
                .when(restTemplate)
                .getForObject(any(URI.class),
                        eq(PredictionResponseDTO.class));

        PredictionResponseDTO result =
                predictionService.getPrediction("AAPL");

        assertNotNull(result);
        verify(restTemplate).getForObject(
                any(URI.class),
                eq(PredictionResponseDTO.class));
    }

    // ==========================
    // URL ENCODING TEST
    // ==========================
    @Test
    void getPrediction_encodesTicker() {

        PredictionResponseDTO mockResponse =
                new PredictionResponseDTO();

        doReturn(mockResponse)
                .when(restTemplate)
                .getForObject(any(URI.class),
                        eq(PredictionResponseDTO.class));

        predictionService.getPrediction("BRK.B");

        verify(restTemplate).getForObject(
                argThat(uri ->
                        uri.toString().contains("BRK.B")
                ),
                eq(PredictionResponseDTO.class));
    }

    // ==========================
    // FAILURE CASE
    // ==========================
    @Test
    void getPrediction_exception_returnsNull() {

        doThrow(new RuntimeException("API down"))
                .when(restTemplate)
                .getForObject(any(URI.class),
                        eq(PredictionResponseDTO.class));

        PredictionResponseDTO result =
                predictionService.getPrediction("AAPL");

        assertNull(result);
    }
}
