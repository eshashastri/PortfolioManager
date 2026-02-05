package com.pm.controller;

import com.pm.dto.PredictionResponseDTO;
import com.pm.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predictions")
@CrossOrigin(origins = "*") // Allows your frontend to talk to this
public class PredictionController {
    private final PredictionService predictionService;

    // Standard constructor injection
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<PredictionResponseDTO> getPrediction(@PathVariable String ticker) {
        PredictionResponseDTO result = predictionService.getPrediction(ticker);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}