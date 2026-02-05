package com.pm.service;

import com.pm.entity.Stock;
import com.pm.repo.StockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockCsvLoaderServiceTest {

    @Mock
    private StockRepo stockRepo;

    @InjectMocks
    private StockCsvLoaderService csvLoaderService;

    // ==========================
    // SUCCESS CASE
    // ==========================
    @Test
    void loadStocksFromCsv_savesNewStocks(@TempDir File tempDir)
            throws IOException {

        File csv = new File(tempDir, "stocks.csv");

        try (FileWriter writer = new FileWriter(csv)) {
            writer.write("ticker,company\n");
            writer.write("AAPL,Apple Inc\n");
            writer.write("GOOG,Google\n");
        }

        when(stockRepo.findByTicker(anyString()))
                .thenReturn(null);

        csvLoaderService.loadStocksFromCsv(csv.getAbsolutePath());

        verify(stockRepo, times(2))
                .save(any(Stock.class));
    }

    // ==========================
    // DUPLICATE SKIP CASE
    // ==========================
    @Test
    void loadStocksFromCsv_skipsDuplicates(@TempDir File tempDir)
            throws IOException {

        File csv = new File(tempDir, "stocks.csv");

        try (FileWriter writer = new FileWriter(csv)) {
            writer.write("ticker,company\n");
            writer.write("AAPL,Apple Inc\n");
        }

        when(stockRepo.findByTicker("AAPL"))
                .thenReturn(new Stock("AAPL", "Apple Inc"));

        csvLoaderService.loadStocksFromCsv(csv.getAbsolutePath());

        verify(stockRepo, never())
                .save(any());
    }

    // ==========================
    // MALFORMED LINE CASE
    // ==========================
    @Test
    void loadStocksFromCsv_skipsMalformedLines(@TempDir File tempDir)
            throws IOException {

        File csv = new File(tempDir, "stocks.csv");

        try (FileWriter writer = new FileWriter(csv)) {
            writer.write("ticker,company\n");
            writer.write("INVALID_LINE\n"); // no comma
            writer.write("MSFT,Microsoft\n");
        }

        when(stockRepo.findByTicker(anyString()))
                .thenReturn(null);

        csvLoaderService.loadStocksFromCsv(csv.getAbsolutePath());

        verify(stockRepo, times(1))
                .save(any());
    }

    // ==========================
    // EXCEPTION CASE
    // ==========================
    @Test
    void loadStocksFromCsv_invalidPath_throwsException() {

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> csvLoaderService.loadStocksFromCsv("bad/path.csv")
        );

        assertTrue(ex.getMessage()
                .contains("Failed to load stocks from CSV"));
    }
}
