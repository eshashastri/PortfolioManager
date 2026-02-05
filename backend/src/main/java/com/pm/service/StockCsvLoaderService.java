package com.pm.service;

import com.pm.entity.Stock;
import com.pm.exceptions.StockCsvLoaderException;
import com.pm.repo.StockRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Service
public class StockCsvLoaderService {

    private final StockRepo stockRepo;

    public StockCsvLoaderService(StockRepo stockRepo) {
        this.stockRepo = stockRepo;
    }

    public void loadStocksFromCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            int lineNumber = 0;

            // Skip CSV header
            br.readLine();
            lineNumber++;

            while ((line = br.readLine()) != null) {
                lineNumber++;

                // CSV format: ticker,company_name
                String[] parts = line.split(",", 2);

                if (parts.length < 2) {
                    throw new StockCsvLoaderException(
                            "Malformed CSV record at line " + lineNumber + ": " + line,
                            "INVALID_CSV_FORMAT",
                            HttpStatus.BAD_REQUEST.value()
                    );
                }

                String ticker = parts[0].trim();
                String companyName = parts[1].trim();

                if (ticker.isEmpty() || companyName.isEmpty()) {
                    throw new StockCsvLoaderException(
                            "Empty ticker or company name at line " + lineNumber,
                            "EMPTY_FIELD",
                            HttpStatus.BAD_REQUEST.value()
                    );
                }

                // Avoid duplicates
                if (stockRepo.findByTicker(ticker) == null) {
                    Stock stock = new Stock(ticker, companyName);
                    stockRepo.save(stock);
                }
            }

        } catch (FileNotFoundException ex) {
            throw new StockCsvLoaderException(
                    "CSV file not found at path: " + filePath,
                    "FILE_NOT_FOUND",
                    HttpStatus.NOT_FOUND.value(),
                    ex
            );
        } catch (IOException ex) {
            throw new StockCsvLoaderException(
                    "Error reading CSV file: " + filePath,
                    "CSV_READ_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        } catch (StockCsvLoaderException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StockCsvLoaderException(
                    "Unexpected error loading stocks from CSV",
                    "CSV_LOADER_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }
}
