package com.pm.service;

import com.pm.entity.Stock;
import com.pm.repo.StockRepo;
import com.pm.exceptions.StockServiceException;  // Import the custom exception
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepo repo;

    public StockService(StockRepo repo) {
        this.repo = repo;
    }

    // Search method to find stocks by ticker or company name
    public List<Stock> search(String q){
        try {
            return repo.findTop20ByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(q, q);
        } catch (Exception e) {
            // Throw a custom exception if an error occurs during search
            throw new StockServiceException("Error occurred while searching for stocks", "SEARCH_ERROR", 500, e);
        }
    }

    // Save a new stock
    public Stock saveStock(Stock s){
        try {
            return repo.save(s);
        } catch (Exception e) {
            // Throw a custom exception if an error occurs while saving the stock
            throw new StockServiceException("Error occurred while saving stock", "SAVE_ERROR", 500, e);
        }
    }

    // Get all stocks
    public List<Stock> getAllStocks(){
        try {
            return repo.findAll();
        } catch (Exception e) {
            // Throw a custom exception if an error occurs while retrieving all stocks
            throw new StockServiceException("Error occurred while retrieving all stocks", "RETRIEVE_ERROR", 500, e);
        }
    }

    // Get a stock by its ticker
    public Stock getByTicker(String ticker){
        try {
            return repo.findByTicker(ticker);
        } catch (Exception e) {
            // Throw a custom exception if an error occurs while retrieving stock by ticker
            throw new StockServiceException("Error occurred while retrieving stock by ticker", "RETRIEVE_TICKER_ERROR", 500, e);
        }
    }
}
