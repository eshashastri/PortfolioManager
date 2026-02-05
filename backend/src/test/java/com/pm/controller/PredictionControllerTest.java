package com.pm.controller;

import com.pm.dto.PredictionResponseDTO;
import com.pm.service.PredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class PredictionControllerTest {

    static class FakePredictionService extends PredictionService {

        public FakePredictionService() {
            super(null);
        }

        @Override
        public PredictionResponseDTO getPrediction(String ticker) {
            if (ticker.equals("AAPL")) {
                return new PredictionResponseDTO();
            }
            return null;
        }
    }

    // ======================
    // SUCCESS CASE
    // ======================
    @Test
    void getPrediction_shouldReturnOk() {

        PredictionController controller =
                new PredictionController(
                        new FakePredictionService()
                );

        ResponseEntity<PredictionResponseDTO> response =
                controller.getPrediction("AAPL");

        assertEquals(HttpStatus.OK,
                response.getStatusCode());

        assertNotNull(response.getBody());
    }

    // ======================
    // NOT FOUND CASE
    // ======================
    @Test
    void getPrediction_shouldReturnNotFound() {

        PredictionController controller =
                new PredictionController(
                        new FakePredictionService()
                );

        ResponseEntity<PredictionResponseDTO> response =
                controller.getPrediction("XXXX");

        assertEquals(HttpStatus.NOT_FOUND,
                response.getStatusCode());

        assertNull(response.getBody());
    }
}
