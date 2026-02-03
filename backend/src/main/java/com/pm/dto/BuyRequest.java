package com.pm.dto;

public class BuyRequest {

    private String ticker;
    private int quantity;
    private double price;

    public BuyRequest() {}

    public BuyRequest(String ticker, int quantity, double price) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
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
