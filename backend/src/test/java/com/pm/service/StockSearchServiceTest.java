package com.pm.service;

import com.pm.dto.StockDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockSearchServiceTest {

    private StockSearchService stockSearchService;

    @BeforeEach
    void setup() {
        stockSearchService = new StockSearchService();

        // Inject test data instead of calling real API
        List<StockDTO> mockStocks = List.of(
                new StockDTO("AAPL", "Apple Inc"),
                new StockDTO("GOOG", "Google LLC"),
                new StockDTO("MSFT", "Microsoft"),
                new StockDTO("AMZN", "Amazon"),
                new StockDTO("TSLA", "Tesla")
        );

        ReflectionTestUtils.setField(
                stockSearchService,
                "stocks",
                mockStocks
        );
    }

    // ==========================
    // Search by ticker
    // ==========================
    @Test
    void search_byTicker() {

        List<StockDTO> result =
                stockSearchService.search("AAPL");

        assertEquals(1, result.size());
        assertEquals("AAPL",
                result.get(0).getTicker());
    }

    // ==========================
    // Search by name
    // ==========================
    @Test
    void search_byName() {

        List<StockDTO> result =
                stockSearchService.search("apple");

        assertEquals(1, result.size());
        assertEquals("Apple Inc",
                result.get(0).getName());
    }

    // ==========================
    // Case insensitive
    // ==========================
    @Test
    void search_caseInsensitive() {

        List<StockDTO> result =
                stockSearchService.search("goo");

        assertEquals(1, result.size());
        assertEquals("GOOG",
                result.get(0).getTicker());
    }

    // ==========================
    // Limit to 10 results
    // ==========================
    @Test
    void search_limitsToTenResults() {

        // Inject 20 stocks
        List<StockDTO> manyStocks =
                java.util.stream.IntStream.range(0,20)
                        .mapToObj(i ->
                                new StockDTO("T"+i,"Test"+i))
                        .toList();

        ReflectionTestUtils.setField(
                stockSearchService,
                "stocks",
                manyStocks
        );

        List<StockDTO> result =
                stockSearchService.search("T");

        assertEquals(10, result.size());
    }

    // ==========================
    // No match
    // ==========================
    @Test
    void search_noMatch_returnsEmpty() {

        List<StockDTO> result =
                stockSearchService.search("XYZ");

        assertTrue(result.isEmpty());
    }
}
