package com.pm.service;

import com.pm.dto.BuyRequest;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.*;
import com.pm.repo.PortfolioStockRepo;
import com.pm.repo.PortfolioTransactionRepo;
import com.pm.repo.StockRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioTransactionService {

    private final PortfolioTransactionRepo transactionRepo;
    private final PortfolioStockRepo portfolioRepo;
    private final StockRepo stockRepo;

    public PortfolioTransactionService(
            PortfolioTransactionRepo transactionRepo,
            PortfolioStockRepo portfolioRepo,
            StockRepo stockRepo
    ) {
        this.transactionRepo = transactionRepo;
        this.portfolioRepo = portfolioRepo;
        this.stockRepo = stockRepo;
    }

    public PortfolioTransaction buy(BuyRequest buyRequest) {
        String ticker = buyRequest.getTicker();
        int quantity = buyRequest.getQuantity();
        double price = buyRequest.getPrice();
        Sector sector = buyRequest.getSector();
        Stock stock = stockRepo.findByTicker(ticker);
        if (stock == null) {
            throw new RuntimeException("Stock not found");
        }

        PortfolioStock portfolioStock =
                portfolioRepo.findByStock(stock)
                        .orElseGet(() -> portfolioRepo.save(new PortfolioStock(stock)));

        return transactionRepo.save(
                new PortfolioTransaction(
                        portfolioStock,
                        TransactionType.BUY,
                        quantity,
                        price,
                        sector
                )
        );
    }
    public List<TransactionResponseDTO> getAllTransactions() {

        return transactionRepo.findAll().stream().map(tx -> {

            TransactionResponseDTO dto = new TransactionResponseDTO();

            dto.setId(tx.getId());
            dto.setType(tx.getType().name());
            dto.setQuantity(tx.getQuantity());
            dto.setPrice(tx.getPrice());
            dto.setTransactionTime(tx.getTransactionTime());


            var portfolioStock = tx.getPortfolioStock();
            var stock = portfolioStock.getStock();

            dto.setTicker(stock.getTicker());
            dto.setCompanyName(stock.getCompanyName());

            return dto;

        }).toList();
    }

//    public PortfolioTransaction sell(BuyRequest buyRequest) {
//        String ticker = buyRequest.getTicker();
//        int quantity = buyRequest.getQuantity();
//        double price = buyRequest.getPrice();
//        Stock stock = stockRepo.findByTicker(ticker);
//        PortfolioStock ps = portfolioRepo.findByStock(stock)
//                .orElseThrow(() -> new RuntimeException("Stock not in portfolio"));
//
//        int ownedQuantity = calculateOwnedQuantity(ps);
//        if (quantity > ownedQuantity) {
//            throw new RuntimeException("Not enough quantity to sell");
//        }
//
//        return transactionRepo.save(
//                new PortfolioTransaction(
//                        ps,
//                        TransactionType.SELL,
//                        quantity,
//                        price
//                )
//        );
//    }

    private int calculateOwnedQuantity(PortfolioStock ps) {
        List<PortfolioTransaction> txs =
                transactionRepo.findByPortfolioStock(ps);

        int qty = 0;
        for (PortfolioTransaction tx : txs) {
            if (tx.getType() == TransactionType.BUY) {
                qty += tx.getQuantity();
            } else {
                qty -= tx.getQuantity();
            }
        }
        return qty;
    }
}
