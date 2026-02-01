package com.hsbc.service;

import com.hsbc.entity.Stock;
import com.hsbc.repo.StockRepo;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
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

            // Skip CSV header
            br.readLine();

            while ((line = br.readLine()) != null) {

                // CSV format: ticker,company_name
                String[] parts = line.split(",", 2);

                if (parts.length < 2) {
                    continue; // skip malformed lines
                }

                String ticker = parts[0].trim();
                String companyName = parts[1].trim();

                // Avoid duplicates
                if (stockRepo.findByTicker(ticker) == null) {

                    Stock stock = new Stock(ticker, companyName);
                    stockRepo.save(stock);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load stocks from CSV", e);
        }
    }
}
