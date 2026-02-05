package com.pm.service;

import com.pm.dto.BuyRequest;
import com.pm.dto.HoldingResponseDTO;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.*;
import com.pm.repo.PortfolioStockRepo;
import com.pm.repo.PortfolioTransactionRepo;
import com.pm.repo.StockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioTransactionServiceTest {

    @Mock
    PortfolioTransactionRepo transactionRepo;

    @Mock
    PortfolioStockRepo portfolioRepo;

    @Mock
    StockRepo stockRepo;

    @InjectMocks
    @Spy
    PortfolioTransactionService service;

    // =========================
    // BUY - new stock
    // =========================
    @Test
    void buy_createsNewPortfolioStock() {

        BuyRequest req = new BuyRequest();
        req.setTicker("AAPL");
        req.setQuantity(10);
        req.setPrice(100);

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setCompanyName("Apple");

        when(stockRepo.findByTicker("AAPL")).thenReturn(stock);
        doReturn("TECH").when(service).fetchSectorFromYahoo("AAPL");

        when(portfolioRepo.findByStock_Ticker("AAPL"))
                .thenReturn(Optional.empty());

        PortfolioStock savedPs = new PortfolioStock();
        savedPs.setStock(stock);
        savedPs.setQuantity(10);
        savedPs.setAvgBuyPrice(100);
        savedPs.setSector("TECH");

        when(portfolioRepo.save(any())).thenReturn(savedPs);
        when(transactionRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        PortfolioTransaction tx = service.buy(req);

        assertEquals(TransactionType.BUY, tx.getType());
        verify(transactionRepo).save(any());
    }

    // =========================
    // BUY - averaging
    // =========================
    @Test
    void buy_updatesAveragePrice() {

        BuyRequest req = new BuyRequest();
        req.setTicker("AAPL");
        req.setQuantity(10);
        req.setPrice(200);

        Stock stock = new Stock();
        stock.setTicker("AAPL");

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);
        ps.setQuantity(10);
        ps.setAvgBuyPrice(100);

        when(stockRepo.findByTicker("AAPL")).thenReturn(stock);
        doReturn("TECH").when(service).fetchSectorFromYahoo("AAPL");
        when(portfolioRepo.findByStock_Ticker("AAPL"))
                .thenReturn(Optional.of(ps));

        when(transactionRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        service.buy(req);

        assertEquals(20, ps.getQuantity());
        assertEquals(150, ps.getAvgBuyPrice());
    }

    // =========================
    // SELL success
    // =========================
    @Test
    void sell_success() {

        BuyRequest req = new BuyRequest();
        req.setTicker("AAPL");
        req.setQuantity(5);
        req.setPrice(150);

        Stock stock = new Stock();
        stock.setTicker("AAPL");

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);
        ps.setQuantity(10);

        when(stockRepo.findByTicker("AAPL")).thenReturn(stock);
        when(portfolioRepo.findByStock_Ticker("AAPL"))
                .thenReturn(Optional.of(ps));
        when(transactionRepo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        PortfolioTransaction tx = service.sell(req);

        assertEquals(5, ps.getQuantity());
        assertEquals(TransactionType.SELL, tx.getType());
    }

    // =========================
    // SELL - not enough qty
    // =========================
    @Test
    void sell_notEnoughQuantity() {

        BuyRequest req = new BuyRequest();
        req.setTicker("AAPL");
        req.setQuantity(20);

        Stock stock = new Stock();
        stock.setTicker("AAPL");

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);
        ps.setQuantity(10);

        when(stockRepo.findByTicker("AAPL")).thenReturn(stock);
        when(portfolioRepo.findByStock_Ticker("AAPL"))
                .thenReturn(Optional.of(ps));

        assertThrows(RuntimeException.class,
                () -> service.sell(req));
    }

    // =========================
    // getAllTransactions
    // =========================
    @Test
    void getAllTransactions_mapsCorrectly() {

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setCompanyName("Apple");

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);

        PortfolioTransaction tx = new PortfolioTransaction(
                ps,
                TransactionType.BUY,
                10,
                100,
                LocalDate.now()
        );

        when(transactionRepo.findAll())
                .thenReturn(List.of(tx));


        List<TransactionResponseDTO> list =
                service.getAllTransactions();

        assertEquals(1, list.size());
        assertEquals("AAPL", list.get(0).getTicker());
    }

    // =========================
    // getHoldings
    // =========================
    @Test
    void getHoldings_returnsOnlyOwnedStocks() {

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setCompanyName("Apple");

        PortfolioStock ps = new PortfolioStock();
        ps.setStock(stock);
        ps.setQuantity(10);
        ps.setAvgBuyPrice(100);
        ps.setSector("TECH");

        when(portfolioRepo.findAll())
                .thenReturn(List.of(ps));

        List<HoldingResponseDTO> holdings =
                service.getHoldings();

        assertEquals(1, holdings.size());
        assertEquals("AAPL", holdings.get(0).getTicker());
    }
}
