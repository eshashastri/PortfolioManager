package com.hsbc.service;

import com.hsbc.entity.Stock;
import com.hsbc.entity.StockPrice;
import com.hsbc.repo.StockPriceRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockPriceService {

    private final StockPriceRepo stockPriceRepo;

    public StockPriceService(StockPriceRepo stockPriceRepo) {
        this.stockPriceRepo = stockPriceRepo;
    }

    public StockPrice savePrice(StockPrice price) {
        return stockPriceRepo.save(price);
    }

    public List<StockPrice> getPricesForStock(int stockId) {
        return stockPriceRepo.findByStock_Id(stockId);
    }

    public List<StockPrice> getPricesBetweenDates(
            int stockId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return stockPriceRepo.findByStock_IdAndPriceDateBetween(
                stockId, startDate, endDate
        );
    }
}
