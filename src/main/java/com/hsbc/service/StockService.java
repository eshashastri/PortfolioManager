package com.hsbc.service;

import com.hsbc.entity.Stock;
import com.hsbc.repo.StockRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepo stockRepo;

    public StockService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    public Stock saveStock(Stock stock) {
        return stockRepo.save(stock);
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }

    public Stock getByTicker(String ticker) {
        return stockRepo.findByTicker(ticker);
    }
}
