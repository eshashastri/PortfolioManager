package com.pm.repo;

import com.pm.entity.PortfolioStock;
import com.pm.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioStockRepo
        extends JpaRepository<PortfolioStock, Integer> {

    Optional<PortfolioStock> findByStock(Stock stock);
    Optional<PortfolioStock> findByStock_Ticker(String ticker);
}
