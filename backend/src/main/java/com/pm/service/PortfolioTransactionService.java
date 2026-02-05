package com.pm.service;

import com.pm.dto.BuyRequest;
import com.pm.dto.HoldingResponseDTO;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.*;
import com.pm.repo.PortfolioStockRepo;
import com.pm.repo.PortfolioTransactionRepo;
import com.pm.repo.StockRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

        Stock stock = stockRepo.findByTicker(ticker);
        if (stock == null) {
            throw new RuntimeException("Stock not found");
        }

        String sector = fetchSectorFromYahoo(ticker);

        PortfolioStock ps = portfolioRepo.findByStock_Ticker(ticker)
                .orElseGet(() -> {
                    PortfolioStock p = new PortfolioStock();
                    p.setStock(stock);
                    p.setSector(sector);
                    p.setQuantity(0);
                    p.setAvgBuyPrice(0);
                    return portfolioRepo.save(p);
                });

        // 🔹 Averaging logic
        int oldQty = ps.getQuantity();
        double oldAvg = ps.getAvgBuyPrice();

        int newQty = oldQty + buyQty;
        double newAvg =
                (oldQty * oldAvg + buyQty * buyPrice) / newQty;

        ps.setQuantity(newQty);
        ps.setAvgBuyPrice(newAvg);
        portfolioRepo.save(ps);
        LocalDate transactionDate = buyRequest.getDate() != null ? buyRequest.getDate() : LocalDate.now();
        System.out.println("Transaction Date: " + transactionDate);  // Debugging log

        return transactionRepo.save(
                new PortfolioTransaction(
                        ps,
                        TransactionType.BUY,
                        buyQty,
                        buyPrice,
                        transactionDate
                )
        );
    }

    public String fetchSectorFromYahoo(String ticker) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:5000/stock/" + ticker +"/sector";

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.get("sector") == null) {
                return "UNKNOWN";
            }

            return response.get("sector").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN";
        }
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
    public PortfolioTransaction sell(BuyRequest buyRequest) {

        String ticker = buyRequest.getTicker();
        int sellQty = buyRequest.getQuantity();
        double sellPrice = buyRequest.getPrice();

        Stock stock = stockRepo.findByTicker(ticker);
        if (stock == null) {
            throw new RuntimeException("Stock not found");
        }

        PortfolioStock ps = portfolioRepo.findByStock_Ticker(stock.getTicker())
                .orElseThrow(() -> new RuntimeException("Stock not in portfolio"));

        if (sellQty > ps.getQuantity()) {
            throw new RuntimeException("Not enough quantity to sell");
        }
        LocalDate transactionDate = buyRequest.getDate() != null ? buyRequest.getDate() : LocalDate.now();

        ps.setQuantity(ps.getQuantity() - sellQty);
        portfolioRepo.save(ps);

        return transactionRepo.save(
                new PortfolioTransaction(
                        ps,
                        TransactionType.SELL,
                        sellQty,
                        sellPrice,
                        transactionDate
                )
        );
    }


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
    public List<HoldingResponseDTO> getHoldings() {

        return portfolioRepo.findAll().stream()
                .filter(ps -> ps.getQuantity() > 0) // only owned stocks
                .map(ps -> {
                    HoldingResponseDTO dto = new HoldingResponseDTO();
                    dto.setTicker(ps.getStock().getTicker());
                    dto.setCompanyName(ps.getStock().getCompanyName());
                    dto.setQuantity(ps.getQuantity());
                    dto.setAvgBuyPrice(ps.getAvgBuyPrice());
                    dto.setSector(ps.getSector());
                    return dto;
                })
                .toList();
    }

}
