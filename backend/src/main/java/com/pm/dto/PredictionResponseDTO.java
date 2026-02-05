package com.pm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionResponseDTO {

    private List<Double> forecast;
    private String accuracy;

    public PredictionResponseDTO() {
    }

    public PredictionResponseDTO(List<Double> forecast, String accuracy) {
        this.forecast = forecast;
        this.accuracy = accuracy;
    }

    public List<Double> getForecast() {
        return forecast;
    }

    public void setForecast(List<Double> forecast) {
        this.forecast = forecast;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
