package com.pm.controller;

import com.pm.dto.BuyRequest;
import com.pm.dto.HoldingResponseDTO;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.PortfolioTransaction;
import com.pm.service.PortfolioTransactionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTransactionControllerTest {

    // ===== Fake Service =====
    static class FakeService extends PortfolioTransactionService {

        public FakeService() {
            super(null, null, null);
        }

        @Override
        public String fetchSectorFromYahoo(String ticker) {
            return "TECH";
        }

        @Override
        public List<TransactionResponseDTO> getAllTransactions() {
            return List.of(new TransactionResponseDTO(),
                    new TransactionResponseDTO());
        }

        @Override
        public PortfolioTransaction buy(BuyRequest r) {
            return new PortfolioTransaction();
        }

        @Override
        public PortfolioTransaction sell(BuyRequest r) {
            return new PortfolioTransaction();
        }

        @Override
        public List<HoldingResponseDTO> getHoldings() {
            return List.of(new HoldingResponseDTO(),
                    new HoldingResponseDTO());
        }
    }

    // ======================
    // FETCH SECTOR
    // ======================
    @Test
    void fetchSector_shouldReturnValue() {

        PortfolioTransactionController controller =
                new PortfolioTransactionController(
                        new FakeService());

        String result =
                controller.getAllSector("AAPL");

        assertEquals("TECH", result);
    }

    // ======================
    // GET TRANSACTIONS
    // ======================
    @Test
    void getTransactions_shouldReturnList() {

        PortfolioTransactionController controller =
                new PortfolioTransactionController(
                        new FakeService());

        List<TransactionResponseDTO> result =
                controller.getAllTransactions();

        assertEquals(2, result.size());
    }

    // ======================
    // BUY
    // ======================
    @Test
    void buy_shouldReturnTransaction() {

        PortfolioTransactionController controller =
                new PortfolioTransactionController(
                        new FakeService());

        BuyRequest req = new BuyRequest();

        PortfolioTransaction tx =
                controller.buy(req);

        assertNotNull(tx);
    }

    // ======================
    // SELL
    // ======================
    @Test
    void sell_shouldReturnTransaction() {

        PortfolioTransactionController controller =
                new PortfolioTransactionController(
                        new FakeService());

        BuyRequest req = new BuyRequest();

        PortfolioTransaction tx =
                controller.sell(req);

        assertNotNull(tx);
    }

    // ======================
    // HOLDINGS
    // ======================
    @Test
    void holdings_shouldReturnList() {

        PortfolioTransactionController controller =
                new PortfolioTransactionController(
                        new FakeService());

        List<HoldingResponseDTO> result =
                controller.getHoldings();

        assertEquals(2, result.size());
    }
}
