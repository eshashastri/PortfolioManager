package com.hsbc.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // Many prices belong to one stock
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private LocalDate priceDate;

    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private double adjClosePrice;
    private long volume;

    public StockPrice() {
    }

    public StockPrice(Stock stock, LocalDate priceDate,
                      double openPrice, double highPrice,
                      double lowPrice, double closePrice,
                      double adjClosePrice, long volume) {
        this.stock = stock;
        this.priceDate = priceDate;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.adjClosePrice = adjClosePrice;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getAdjClosePrice() {
        return adjClosePrice;
    }

    public void setAdjClosePrice(double adjClosePrice) {
        this.adjClosePrice = adjClosePrice;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}
