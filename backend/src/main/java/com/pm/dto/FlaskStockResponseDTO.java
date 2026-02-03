package com.hsbc.dto;

import java.util.List;

public class FlaskStockResponseDTO {

    private FlaskMetadataDTO metadata;
    private List<FlaskPriceDTO> historicalData;
    private String period;
    private double latestPrice;
    private double returnPercentage;

    // getters & setters

    public FlaskMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(FlaskMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<FlaskPriceDTO> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(List<FlaskPriceDTO> historicalData) {
        this.historicalData = historicalData;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public void setReturnPercentage(double returnPercentage) {
        this.returnPercentage = returnPercentage;
    }
}
