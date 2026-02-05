package com.pm.dto;

import com.pm.entity.Sector;

public class BuyRequest {

    private String ticker;
    private int quantity;
    private double price;
    private String sector;
    public BuyRequest() {}

    public BuyRequest(String ticker, int quantity, double price, String sector) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.sector = sector;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
