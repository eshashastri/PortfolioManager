package com.pm.service;

import com.pm.dto.PredictionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PredictionService {

    private final RestTemplate restTemplate; // Renamed for consistency with your config

    @Value("${predictor.api.url:http://localhost:5001}")
    private String predictorBaseUrl;

    // REMOVED the @Qualifier("predictorRestTemplate")
    public PredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PredictionResponseDTO getPrediction(String ticker) {
        try {
            String encoded = URLEncoder.encode(ticker, StandardCharsets.UTF_8);
            String url = predictorBaseUrl + "/predict/" + encoded;
            // Now using the correctly injected restTemplate
            return restTemplate.getForObject(URI.create(url), PredictionResponseDTO.class);
        } catch (Exception e) {
            // Log the error so you know why it failed (Connection refused vs Ticker not found)
            System.err.println("Prediction failed for " + ticker + ": " + e.getMessage());
            return null;
        }
    }
}
