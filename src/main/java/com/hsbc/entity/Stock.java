package com.hsbc.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String ticker;
    private String companyName;
    private String exchange;
    private String currency;

    // One stock has many daily price entries
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StockPrice> prices;

    public Stock() {
    }

    public Stock(String ticker, String companyName, String exchange, String currency) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.exchange = exchange;
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<StockPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<StockPrice> prices) {
        this.prices = prices;
    }
}
