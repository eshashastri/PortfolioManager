package com.pm.service;

import com.pm.dto.PredictionResponseDTO;
import com.pm.exceptions.PredictionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PredictionService {

    private final RestTemplate restTemplate;

    @Value("${predictor.api.url:http://localhost:5001}")
    private String predictorBaseUrl;

    public PredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PredictionResponseDTO getPrediction(String ticker) {
        try {
            String encoded = URLEncoder.encode(ticker, StandardCharsets.UTF_8);
            String url = predictorBaseUrl + "/predict/" + encoded;
            return restTemplate.getForObject(URI.create(url), PredictionResponseDTO.class);
        } catch (HttpClientErrorException ex) {
            throw new PredictionException(
                    "Prediction not found for ticker: " + ticker,
                    "TICKER_NOT_FOUND",
                    HttpStatus.NOT_FOUND.value(),
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new PredictionException(
                    "Cannot connect to prediction service",
                    "SERVICE_CONNECTION_ERROR",
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    ex
            );
        } catch (Exception ex) {
            throw new PredictionException(
                    "Unexpected error during prediction: " + ex.getMessage(),
                    "PREDICTION_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }
}
