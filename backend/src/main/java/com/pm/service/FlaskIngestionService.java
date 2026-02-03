package com.pm.service;

import com.pm.dto.FlaskPriceDTO;
import com.pm.dto.FlaskStockResponseDTO;
import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class FlaskIngestionService {

    private final RestTemplate restTemplate;
    private final StockService stockService;
    private final StockPriceService stockPriceService;

    public FlaskIngestionService(
            RestTemplate restTemplate,
            StockService stockService,
            StockPriceService stockPriceService
    ) {
        this.restTemplate = restTemplate;
        this.stockService = stockService;
        this.stockPriceService = stockPriceService;
    }
    @Async
    public void ingestStock(String ticker, String period) {

        String url = "http://localhost:5000/stock/" + ticker + "?period=" + period;

        FlaskStockResponseDTO response =
                restTemplate.getForObject(url, FlaskStockResponseDTO.class);
        Stock stock = stockService.getByTicker(ticker);

        if (stock == null) {
            stock = new Stock(
                    response.getMetadata().getTicker(),
                    response.getMetadata().getCompanyName()
            );
            stock = stockService.saveStock(stock);
        }

        for (FlaskPriceDTO dto : response.getHistoricalData()) {

            LocalDate date = LocalDate.parse(dto.getDate());

            if (!stockPriceService.priceExists(stock.getId(), date)) {

                StockPrice price = new StockPrice(
                        stock,
                        date,
                        dto.getOpen(),
                        dto.getHigh(),
                        dto.getLow(),
                        dto.getClose(),
                        dto.getVolume()
                );

                stockPriceService.savePrice(price);
            }
        }
    }
}
