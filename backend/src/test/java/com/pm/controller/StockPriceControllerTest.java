package com.pm.controller;

import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import com.pm.service.StockPriceService;
import com.pm.service.StockService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockPriceControllerTest {

    // ===== Fake StockService =====
    static class FakeStockService extends StockService {
        public FakeStockService() { super(null); }

        @Override
        public Stock getByTicker(String ticker) {
            Stock stock = new Stock();
            stock.setId(1);
            stock.setTicker(ticker);
            return stock;
        }
    }

    // ===== Fake StockPriceService =====
    static class FakeStockPriceService extends StockPriceService {
        public FakeStockPriceService() { super(null); }

        @Override
        public StockPrice savePrice(StockPrice price) {
            return price;
        }

        @Override
        public List<StockPrice> getPricesForStock(int stockId) {
            return List.of(new StockPrice(), new StockPrice());
        }

        @Override
        public List<StockPrice> getPricesBetweenDates(
                int stockId,
                LocalDate start,
                LocalDate end) {
            return List.of(new StockPrice());
        }
    }

    // ==========================
    // TEST SAVE SINGLE PRICE
    // ==========================
    @Test
    void saveSinglePrice_shouldAttachStock() {

        StockPriceController controller =
                new StockPriceController(
                        new FakeStockPriceService(),
                        new FakeStockService()
                );

        StockPrice price = new StockPrice();

        StockPrice result =
                controller.saveSinglePrice("AAPL", price);

        assertNotNull(result);
        assertNotNull(result.getStock());
        assertEquals("AAPL",
                result.getStock().getTicker());
    }

    // ==========================
    // TEST BULK SAVE
    // ==========================
    @Test
    void saveBulkPrices_shouldSaveAll() {

        StockPriceController controller =
                new StockPriceController(
                        new FakeStockPriceService(),
                        new FakeStockService()
                );

        List<StockPrice> prices =
                List.of(new StockPrice(), new StockPrice());

        List<StockPrice> result =
                controller.saveBulkPrices("AAPL", prices);

        assertEquals(2, result.size());
        assertNotNull(result.get(0).getStock());
    }

    // ==========================
    // TEST GET ALL PRICES
    // ==========================
    @Test
    void getAllPrices_shouldReturnList() {

        StockPriceController controller =
                new StockPriceController(
                        new FakeStockPriceService(),
                        new FakeStockService()
                );

        List<StockPrice> result =
                controller.getAllPrices("AAPL");

        assertEquals(2, result.size());
    }

    // ==========================
    // TEST BETWEEN DATES
    // ==========================
    @Test
    void getPricesBetweenDates_shouldReturnData() {

        StockPriceController controller =
                new StockPriceController(
                        new FakeStockPriceService(),
                        new FakeStockService()
                );

        List<StockPrice> result =
                controller.getPricesBetweenDates(
                        "AAPL",
                        "2024-01-01",
                        "2024-01-10"
                );

        assertEquals(1, result.size());
    }
}
