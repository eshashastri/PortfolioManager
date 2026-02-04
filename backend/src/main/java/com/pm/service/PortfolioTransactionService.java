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
        int buyQty = buyRequest.getQuantity();
        double buyPrice = buyRequest.getPrice();
        Sector sector = buyRequest.getSector();

        // 1️⃣ Validate stock
        Stock stock = stockRepo.findByTicker(ticker);
        if (stock == null) {
            throw new RuntimeException("Stock not found");
        }

        // 2️⃣ Get or create PortfolioStock
        PortfolioStock ps = portfolioRepo.findByStock(stock)
                .orElseGet(() -> {
                    PortfolioStock p = new PortfolioStock();
                    p.setStock(stock);
                    p.setSector(sector);     // ✅ sector set ONCE
                    p.setQuantity(0);
                    p.setAvgBuyPrice(0);
                    return portfolioRepo.save(p);
                });

        // 3️⃣ Average-cost calculation
        int oldQty = ps.getQuantity();
        double oldAvg = ps.getAvgBuyPrice();

        int newQty = oldQty + buyQty;
        double newAvg =
                (oldQty * oldAvg + buyQty * buyPrice) / newQty;

        ps.setQuantity(newQty);
        ps.setAvgBuyPrice(newAvg);

        portfolioRepo.save(ps);

        // 4️⃣ Save BUY transaction (NO sector here)
        return transactionRepo.save(
                new PortfolioTransaction(
                        ps,
                        TransactionType.BUY,
                        buyQty,
                        buyPrice
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
//
//        String ticker = buyRequest.getTicker();
//        int sellQty = buyRequest.getQuantity();
//        double sellPrice = buyRequest.getPrice();
//
//        Stock stock = stockRepo.findByTicker(ticker);
//        if (stock == null) {
//            throw new RuntimeException("Stock not found");
//        }
//
//        PortfolioStock ps = portfolioRepo.findByStock(stock)
//                .orElseThrow(() -> new RuntimeException("Stock not in portfolio"));
//
//        int ownedQty = ps.getQuantity();
//
//        if (sellQty > ownedQty) {
//            throw new RuntimeException("Not enough quantity to sell");
//        }
//
//        // ✅ Reduce quantity (average price DOES NOT change)
//        ps.setQuantity(ownedQty - sellQty);
//        portfolioRepo.save(ps);
//
//        // ✅ Record SELL transaction
//        return transactionRepo.save(
//                new PortfolioTransaction(
//                        ps,
//                        TransactionType.SELL,
//                        sellQty,
//                        sellPrice,
//                        ps.getStock().getSector() // ✅ correct sector
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
