package com.pm.controller;

import com.pm.service.StockCsvLoaderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockCsvControllerTest {

    // ===== Fake Service =====
    static class FakeStockCsvLoaderService extends StockCsvLoaderService {

        public FakeStockCsvLoaderService() {
            super(null);
        }

        @Override
        public void loadStocksFromCsv(String filePath) {
            // do nothing (fake)
        }
    }

    // ==========================
    // TEST LOAD CSV
    // ==========================
    @Test
    void loadStockMaster_shouldReturnSuccessMessage() {

        StockCsvController controller =
                new StockCsvController(
                        new FakeStockCsvLoaderService()
                );

        String result = controller.loadStockMaster();

        assertEquals(
                "Stock master loaded successfully",
                result
        );
    }
}
