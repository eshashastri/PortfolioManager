package com.hsbc.repo;

import com.hsbc.entity.PortfolioTransaction;
import com.hsbc.entity.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioTransactionRepo extends JpaRepository<PortfolioTransaction, Integer> {

    List<PortfolioTransaction> findByPortfolioStock(PortfolioStock portfolioStock);
}
