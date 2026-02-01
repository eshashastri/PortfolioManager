package com.hsbc.controller;

import com.hsbc.service.StockCsvLoaderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockCsvController {

    private final StockCsvLoaderService csvLoaderService;

    public StockCsvController(StockCsvLoaderService csvLoaderService) {
        this.csvLoaderService = csvLoaderService;
    }

    @PostMapping("/load-csv")
    public String loadStockMaster() {
        csvLoaderService.loadStocksFromCsv("nasdaq_symbols.csv");
        return "Stock master loaded successfully";
    }
}
