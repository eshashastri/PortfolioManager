package com.pm.service;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.pm.dto.StockDTO;
@Service
public class StockSearchService {

    private List<StockDTO> stocks = new ArrayList<>();

    // Load NASDAQ list at startup
    @PostConstruct
    public void loadStocks() {

        try {

            URL url = new URL(
                    "https://www.nasdaqtrader.com/dynamic/symdir/nasdaqlisted.txt"
            );

            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(url.openStream())
                    );

            br.lines()
                    .skip(1)
                    .forEach(line -> {

                        String[] parts = line.split("\\|");

                        if(parts.length > 1){
                            stocks.add(
                                    new StockDTO(parts[0], parts[1])
                            );
                        }
                    });

            System.out.println("Loaded stocks: " + stocks.size());

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<StockDTO> search(String query){

        String q = query.toLowerCase();

        return stocks.stream()
                .filter(s ->
                        s.getTicker().toLowerCase().contains(q) ||
                                s.getName().toLowerCase().contains(q))
                .limit(10)
                .collect(Collectors.toList());
    }
}
