package com.pm.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false, unique = true)
    private String ticker;
    private String companyName;


    // One stock has many daily price entries
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StockPrice> prices;

    public Stock() {
    }

    public Stock(String ticker, String companyName) {
        this.ticker = ticker;
        this.companyName = companyName;

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

    public List<StockPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<StockPrice> prices) {
        this.prices = prices;
    }
}
