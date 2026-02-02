package com.hsbc.controller;

import com.hsbc.entity.Stock;
import com.hsbc.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // Create a stock
    @PostMapping
    public Stock saveStock(@RequestBody Stock stock) {
        return stockService.saveStock(stock);
    }

    // Get all stocks
    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    // Get stock by ticker
    @GetMapping("/{ticker}")
    public Stock getStockByTicker(@PathVariable String ticker) {
        return stockService.getByTicker(ticker);
    }
}
