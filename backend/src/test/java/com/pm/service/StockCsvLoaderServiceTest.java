package com.pm.service;

import com.pm.entity.Stock;
import com.pm.exceptions.StockCsvLoaderException;
import com.pm.repo.StockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockCsvLoaderServiceTest {

    StockRepo repo = mock(StockRepo.class);
    StockCsvLoaderService service =
            new StockCsvLoaderService(repo);

    // ==========================
    // SUCCESS CASE
    // ==========================
    @Test
    void loadStocksFromCsv_savesValidStocks(@TempDir File tempDir)
            throws IOException {

        File csv = new File(tempDir,"stocks.csv");

        try(FileWriter w = new FileWriter(csv)){
            w.write("ticker,company\n");
            w.write("AAPL,Apple Inc\n");
            w.write("GOOG,Google\n");
        }

        when(repo.findByTicker(anyString()))
                .thenReturn(null);

        service.loadStocksFromCsv(csv.getAbsolutePath());

        verify(repo, times(2))
                .save(any(Stock.class));
    }

    // ==========================
    // INVALID PATH
    // ==========================
    @Test
    void loadStocksFromCsv_invalidPath_throwsException() {

        assertThrows(
                StockCsvLoaderException.class,
                () -> service.loadStocksFromCsv("bad/path.csv")
        );
    }

    // ==========================
    // MALFORMED LINE
    // ==========================
    @Test
    void loadStocksFromCsv_malformedLine_throwsException(
            @TempDir File tempDir) throws IOException {

        File csv = new File(tempDir,"stocks.csv");

        try(FileWriter w = new FileWriter(csv)){
            w.write("ticker,company\n");
            w.write("INVALID_LINE\n"); // malformed
        }

        assertThrows(
                StockCsvLoaderException.class,
                () -> service.loadStocksFromCsv(
                        csv.getAbsolutePath())
        );
    }
}
