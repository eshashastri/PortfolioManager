package com.pm.controller;

import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import com.pm.service.StockPriceService;
import com.pm.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/prices")
@CrossOrigin
public class StockPriceController {

    private final StockPriceService stockPriceService;
    private final StockService stockService;

    public StockPriceController(
            StockPriceService stockPriceService,
            StockService stockService
    ) {
        this.stockPriceService = stockPriceService;
        this.stockService = stockService;
    }

    // Save ONE daily price
    @PostMapping("/{ticker}")
    public StockPrice saveSinglePrice(
            @PathVariable String ticker,
            @RequestBody StockPrice price
    ) {
        Stock stock = stockService.getByTicker(ticker);
        price.setStock(stock);
        return stockPriceService.savePrice(price);
    }

    // Save MULTIPLE daily prices (bulk insert)
    @PostMapping("/{ticker}/bulk")
    public List<StockPrice> saveBulkPrices(
            @PathVariable String ticker,
            @RequestBody List<StockPrice> prices
    ) {
        Stock stock = stockService.getByTicker(ticker);

        for (StockPrice price : prices) {
            price.setStock(stock);
        }

        return prices.stream()
                .map(stockPriceService::savePrice)
                .toList();
    }
    @GetMapping("/{ticker}/all")
    public List<StockPrice> getAllPrices(@PathVariable String ticker) {

        Stock stock = stockService.getByTicker(ticker);
        if (stock == null) {
            throw new RuntimeException("Stock not found: " + ticker);
        }

        return stockPriceService.getPricesForStock(stock.getId());
    }

    @GetMapping("/{ticker}")
    public List<StockPrice> getPricesBetweenDates(
            @PathVariable String ticker,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        Stock stock = stockService.getByTicker(ticker);

        return stockPriceService.getPricesBetweenDates(
                stock.getId(),
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
    }
}
