package com.pm.repo;

import com.pm.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepo extends JpaRepository<Stock, Long> {

    // For CSV duplicate check
   Stock findByTicker(String ticker);

    // 🔥 THIS FIXES YOUR ERROR
    List<Stock> findTop20ByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase(
            String ticker,
            String companyName
    );
}
