package com.pm.service;

import com.pm.dto.StockDTO;
import com.pm.exceptions.StockSearchException;  // Import the custom exception
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockSearchService {

    private List<StockDTO> stocks = new ArrayList<>();

    // Load NASDAQ list at startup
    @PostConstruct
    public void loadStocks() {
        try {
            URL url = new URL("https://www.nasdaqtrader.com/dynamic/symdir/nasdaqlisted.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            br.lines()
                    .skip(1)
                    .forEach(line -> {
                        try {
                            String[] parts = line.split("\\|");
                            if (parts.length > 1) {
                                stocks.add(new StockDTO(parts[0], parts[1]));
                            }
                        } catch (Exception e) {
                            // Log the exception but continue processing other lines
                            System.err.println("Error processing line: " + line);
                            e.printStackTrace();
                        }
                    });

            System.out.println("Loaded stocks: " + stocks.size());

        } catch (Exception e) {
            // Throw a custom exception with error code and HTTP status
            throw new StockSearchException("Failed to load stock data from the URL", "STOCK_FETCH_ERROR", 500, e);
        }
    }

    // Search method to search stocks by query
    public List<StockDTO> search(String query) {
        try {
            String q = query.toLowerCase();
            return stocks.stream()
                    .filter(s -> s.getTicker().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Throw custom exception for search errors
            throw new StockSearchException("Error occurred during stock search", "SEARCH_ERROR", 400, e);
        }
    }
}
