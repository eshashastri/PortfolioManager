package com.hsbc.repo;

import com.hsbc.entity.PortfolioStock;
import com.hsbc.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioStockRepo
        extends JpaRepository<PortfolioStock, Integer> {

    Optional<PortfolioStock> findByStock(Stock stock);
}
