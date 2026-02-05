package com.pm.service;

import com.pm.dto.FlaskPriceDTO;
import com.pm.dto.FlaskStockResponseDTO;
import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import com.pm.exceptions.StockIngestionException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        try {
            String url = "http://localhost:5000/stock/" + ticker + "?period=" + period;

            FlaskStockResponseDTO response = restTemplate.getForObject(url, FlaskStockResponseDTO.class);

            if (response == null) {
                throw new StockIngestionException(
                        "No data received from Flask service for ticker: " + ticker,
                        "EMPTY_FLASK_RESPONSE",
                        HttpStatus.NO_CONTENT.value()
                );
            }

            Stock stock = stockService.getByTicker(ticker);

            if (stock == null) {
                stock = new Stock(response.getMetadata().getTicker(),
                        response.getMetadata().getCompanyName()
                );
                stock = stockService.saveStock(stock);
            }

            for (FlaskPriceDTO dto : response.getHistoricalData()) {
                try {
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
                } catch (DateTimeParseException ex) {
                    throw new StockIngestionException(
                            "Invalid date format in historical data: " + dto.getDate(),
                            "INVALID_DATE_FORMAT",
                            HttpStatus.BAD_REQUEST.value(),
                            ex
                    );
                }
            }

        } catch (StockIngestionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockIngestionException(
                    "Error ingesting stock data for ticker: " + ticker,
                    "STOCK_INGESTION_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }
}
