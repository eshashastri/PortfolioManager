package com.pm.controller;

import com.pm.service.FlaskIngestionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngestionControllerTest {

    // ===== Fake Service =====
    static class FakeIngestionService extends FlaskIngestionService {

        public FakeIngestionService() {
            super(null, null, null);
        }

        @Override
        public void ingestStock(String ticker, String period) {
            // do nothing
        }
    }

    // ======================
    // TEST INGEST
    // ======================
    @Test
    void ingestStock_shouldReturnMessage() {

        IngestionController controller =
                new IngestionController(
                        new FakeIngestionService()
                );

        String result =
                controller.ingestStock("AAPL", "1y");

        assertEquals(
                "Ingestion completed for AAPL",
                result
        );
    }

    // ======================
    // TEST DEFAULT PERIOD
    // ======================
    @Test
    void ingestStock_defaultPeriod_shouldWork() {

        IngestionController controller =
                new IngestionController(
                        new FakeIngestionService()
                );

        String result =
                controller.ingestStock("MSFT", "3mo");

        assertEquals(
                "Ingestion completed for MSFT",
                result
        );
    }
}
