package com.pm.service;

import com.pm.entity.Stock;
import com.pm.repo.StockRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepo repo;

    public StockService(StockRepo repo) {
        this.repo = repo;
    }

    public List<Stock> search(String q){
        return repo
                .findTop20ByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(q,q);
    }

    public Stock saveStock(Stock s){
        return repo.save(s);
    }

    public List<Stock> getAllStocks(){
        return repo.findAll();
    }

    public Stock getByTicker(String ticker){
        return repo.findByTicker(ticker);
    }
}

