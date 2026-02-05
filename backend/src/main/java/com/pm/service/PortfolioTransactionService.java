package com.pm.service;

import com.pm.dto.BuyRequest;
import com.pm.dto.HoldingResponseDTO;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.*;
import com.pm.exceptions.PortfolioTransactionException;
import com.pm.repo.PortfolioStockRepo;
import com.pm.repo.PortfolioTransactionRepo;
import com.pm.repo.StockRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        try {
            String ticker = buyRequest.getTicker();
            int buyQty = buyRequest.getQuantity();
            double buyPrice = buyRequest.getPrice();

            Stock stock = stockRepo.findByTicker(ticker);
            if (stock == null) {
                throw new PortfolioTransactionException(
                        "Stock not found for ticker: " + ticker,
                        "STOCK_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                );
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

            int oldQty = ps.getQuantity();
            double oldAvg = ps.getAvgBuyPrice();

            int newQty = oldQty + buyQty;
            double newAvg = (oldQty * oldAvg + buyQty * buyPrice) / newQty;

            ps.setQuantity(newQty);
            ps.setAvgBuyPrice(newAvg);
            portfolioRepo.save(ps);

            return transactionRepo.save(
                    new PortfolioTransaction(
                            ps,
                            TransactionType.BUY,
                            buyQty,
                            buyPrice
                    )
            );
        } catch (PortfolioTransactionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PortfolioTransactionException(
                    "Error processing buy transaction",
                    "BUY_TRANSACTION_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    public String fetchSectorFromYahoo(String ticker) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:5000/stock/" + ticker + "/sector";

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.get("sector") == null) {
                throw new PortfolioTransactionException(
                        "Sector information not available for ticker: " + ticker,
                        "SECTOR_FETCH_ERROR",
                        HttpStatus.BAD_GATEWAY.value()
                );
            }

            return response.get("sector").toString();

        } catch (PortfolioTransactionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PortfolioTransactionException(
                    "Failed to fetch sector from Yahoo service",
                    "SECTOR_SERVICE_ERROR",
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    ex
            );
        }
    }

    public List<TransactionResponseDTO> getAllTransactions() {
        try {
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
        } catch (Exception ex) {
            throw new PortfolioTransactionException(
                    "Error retrieving transactions",
                    "FETCH_TRANSACTIONS_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    public PortfolioTransaction sell(BuyRequest buyRequest) {
        try {
            String ticker = buyRequest.getTicker();
            int sellQty = buyRequest.getQuantity();
            double sellPrice = buyRequest.getPrice();

            Stock stock = stockRepo.findByTicker(ticker);
            if (stock == null) {
                throw new PortfolioTransactionException(
                        "Stock not found for ticker: " + ticker,
                        "STOCK_NOT_FOUND",
                        HttpStatus.NOT_FOUND.value()
                );
            }

            PortfolioStock ps = portfolioRepo.findByStock_Ticker(stock.getTicker())
                    .orElseThrow(() -> new PortfolioTransactionException(
                            "Stock not in portfolio: " + ticker,
                            "STOCK_NOT_IN_PORTFOLIO",
                            HttpStatus.NOT_FOUND.value()
                    ));

            if (sellQty > ps.getQuantity()) {
                throw new PortfolioTransactionException(
                        "Insufficient quantity to sell. Available: " + ps.getQuantity() + ", Requested: " + sellQty,
                        "INSUFFICIENT_QUANTITY",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            ps.setQuantity(ps.getQuantity() - sellQty);
            portfolioRepo.save(ps);

            return transactionRepo.save(
                    new PortfolioTransaction(
                            ps,
                            TransactionType.SELL,
                            sellQty,
                            sellPrice
                    )
            );
        } catch (PortfolioTransactionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PortfolioTransactionException(
                    "Error processing sell transaction",
                    "SELL_TRANSACTION_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }

    private int calculateOwnedQuantity(PortfolioStock ps) {
        List<PortfolioTransaction> txs = transactionRepo.findByPortfolioStock(ps);

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
        try {
            return portfolioRepo.findAll().stream()
                    .filter(ps -> ps.getQuantity() > 0)
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
        } catch (Exception ex) {
            throw new PortfolioTransactionException(
                    "Error retrieving portfolio holdings",
                    "FETCH_HOLDINGS_ERROR",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ex
            );
        }
    }
}
