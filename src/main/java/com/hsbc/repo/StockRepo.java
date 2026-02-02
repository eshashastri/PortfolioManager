package com.hsbc.repo;

import com.hsbc.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepo extends JpaRepository<Stock, Integer> {

    // Find a stock using ticker symbol (GOOG, AAPL, etc.)
    Stock findByTicker(String ticker);
}
