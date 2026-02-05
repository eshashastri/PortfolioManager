package com.pm.controller;

import com.pm.entity.Stock;
import com.pm.service.StockService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockControllerTest {

    // ===== Fake Service =====
    static class FakeStockService extends StockService {

        public FakeStockService() {
            super(null);
        }

        @Override
        public List<Stock> search(String q) {
            Stock s1 = new Stock();
            s1.setTicker("AAPL");
            s1.setCompanyName("Apple Inc");

            Stock s2 = new Stock();
            s2.setTicker("GOOG");
            s2.setCompanyName("Google");

            return List.of(s1, s2);
        }
    }

    // ==========================
    // TEST SEARCH
    // ==========================
    @Test
    void search_shouldReturnResults() {

        StockController controller =
                new StockController(new FakeStockService());

        List<Stock> result =
                controller.search("A");

        assertEquals(2, result.size());
        assertEquals("AAPL",
                result.get(0).getTicker());
    }
}
