package com.pm.service;

import com.pm.entity.Stock;
import com.pm.repo.StockRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepo repo;

    @InjectMocks
    private StockService stockService;

    // ==========================
    // search
    // ==========================
    @Test
    void search_delegatesToRepo() {

        List<Stock> mockList =
                List.of(new Stock(), new Stock());

        when(repo
                .findTop20ByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase("A","A"))
                .thenReturn(mockList);

        List<Stock> result =
                stockService.search("A");

        assertEquals(2, result.size());

        verify(repo)
                .findTop20ByTickerContainingIgnoreCaseOrCompanyNameContainingIgnoreCase("A","A");
    }

    // ==========================
    // saveStock
    // ==========================
    @Test
    void saveStock_callsRepoSave() {

        Stock s = new Stock();

        when(repo.save(s)).thenReturn(s);

        Stock result =
                stockService.saveStock(s);

        assertEquals(s, result);
        verify(repo).save(s);
    }

    // ==========================
    // getAllStocks
    // ==========================
    @Test
    void getAllStocks_returnsList() {

        List<Stock> list =
                List.of(new Stock(), new Stock());

        when(repo.findAll()).thenReturn(list);

        List<Stock> result =
                stockService.getAllStocks();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    // ==========================
    // getByTicker
    // ==========================
    @Test
    void getByTicker_returnsStock() {

        Stock stock = new Stock();

        when(repo.findByTicker("AAPL"))
                .thenReturn(stock);

        Stock result =
                stockService.getByTicker("AAPL");

        assertEquals(stock, result);
        verify(repo).findByTicker("AAPL");
    }
}
