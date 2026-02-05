package com.pm.service;

import com.pm.entity.StockPrice;
import com.pm.exceptions.StockPriceException;
import com.pm.repo.StockPriceRepo;
import org.springframework.http.HttpStatus;
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
        try {
            if (price == null) {
                throw new StockPriceException(
                        "Stock price cannot be null",
                        "NULL_STOCK_PRICE",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            if (price.getStock() == null) {
                throw new StockPriceException(
                        "Stock reference cannot be null",
                        "NULL_STOCK_REFERENCE",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            return stockPriceRepo.save(price);
        } catch (StockPriceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockPriceException(
                    "Error saving stock price",
                    "SAVE_PRICE_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    public List<StockPrice> getPricesForStock(int stockId) {
        try {
            if (stockId <= 0) {
                throw new StockPriceException(
                        "Invalid stock ID: " + stockId,
                        "INVALID_STOCK_ID",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            List<StockPrice> prices = stockPriceRepo.findByStock_Id(stockId);
            if (prices.isEmpty()) {
                throw new StockPriceException(
                        "No prices found for stock ID: " + stockId,
                        "NO_PRICES_FOUND",
                        HttpStatus.NOT_FOUND.value()
                );
            }
            return prices;
        } catch (StockPriceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockPriceException(
                    "Error retrieving prices for stock",
                    "FETCH_PRICES_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    public boolean priceExists(int stockId, LocalDate date) {
        try {
            if (stockId <= 0) {
                throw new StockPriceException(
                        "Invalid stock ID: " + stockId,
                        "INVALID_STOCK_ID",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            if (date == null) {
                throw new StockPriceException(
                        "Price date cannot be null",
                        "NULL_PRICE_DATE",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            return stockPriceRepo.existsByStock_IdAndPriceDate(stockId, date);
        } catch (StockPriceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockPriceException(
                    "Error checking price existence",
                    "CHECK_PRICE_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    public List<StockPrice> getPricesBetweenDates(
            int stockId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        try {
            if (stockId <= 0) {
                throw new StockPriceException(
                        "Invalid stock ID: " + stockId,
                        "INVALID_STOCK_ID",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            if (startDate == null || endDate == null) {
                throw new StockPriceException(
                        "Start date and end date cannot be null",
                        "NULL_DATES",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            if (startDate.isAfter(endDate)) {
                throw new StockPriceException(
                        "Start date cannot be after end date",
                        "INVALID_DATE_RANGE",
                        HttpStatus.BAD_REQUEST.value()
                );
            }
            List<StockPrice> prices = stockPriceRepo.findByStock_IdAndPriceDateBetween(
                    stockId, startDate, endDate
            );
            if (prices.isEmpty()) {
                throw new StockPriceException(
                        "No prices found between " + startDate + " and " + endDate,
                        "NO_PRICES_IN_RANGE",
                        HttpStatus.NOT_FOUND.value()
                );
            }
            return prices;
        } catch (StockPriceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockPriceException(
                    "Error retrieving prices between dates",
                    "FETCH_PRICES_RANGE_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }
}
