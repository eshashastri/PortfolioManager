package com.pm.dto;

public class StockDTO {

    private String ticker;
    private String name;

    public StockDTO(String ticker, String name){
        this.ticker = ticker;
        this.name = name;
    }

    public String getTicker(){
        return ticker;
    }

    public String getName(){
        return name;
    }
}
