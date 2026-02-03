package com.pm.repo;

import com.pm.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPriceRepo extends JpaRepository<StockPrice, Integer> {

    // Get all prices for a given stock
    List<StockPrice> findByStock_Id(int stockId);

    // Get prices for a stock between start and end date
    List<StockPrice> findByStock_IdAndPriceDateBetween(
            int stockId,
            LocalDate startDate,
            LocalDate endDate
    );
    boolean existsByStock_IdAndPriceDate(int stockId, LocalDate priceDate);

}
