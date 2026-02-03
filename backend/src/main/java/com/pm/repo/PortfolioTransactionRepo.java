package com.pm.repo;

import com.pm.entity.PortfolioTransaction;
import com.pm.entity.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioTransactionRepo extends JpaRepository<PortfolioTransaction, Integer> {

    List<PortfolioTransaction> findByPortfolioStock(PortfolioStock portfolioStock);
}
