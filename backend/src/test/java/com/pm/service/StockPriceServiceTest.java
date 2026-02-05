package com.pm.service;

import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import com.pm.repo.StockPriceRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockPriceServiceTest {

    StockPriceRepo repo = mock(StockPriceRepo.class);
    StockPriceService service = new StockPriceService(repo);

    // ==========================
    // SAVE PRICE
    // ==========================
    @Test
    void savePrice_delegatesToRepo() {

        // FIX: create stock
        Stock stock = new Stock();
        stock.setId(1);

        // FIX: attach stock
        StockPrice price = new StockPrice();
        price.setStock(stock);

        when(repo.save(price)).thenReturn(price);

        StockPrice result =
                service.savePrice(price);

        assertNotNull(result);
        verify(repo).save(price);
    }

    // ==========================
    // GET PRICES
    // ==========================
    @Test
    void getPricesForStock_returnsList() {

        when(repo.findByStock_Id(1))
                .thenReturn(List.of(new StockPrice()));

        List<StockPrice> list =
                service.getPricesForStock(1);

        assertEquals(1, list.size());
    }

    // ==========================
    // EXISTS
    // ==========================
    @Test
    void priceExists_returnsTrue() {

        LocalDate date = LocalDate.now();

        when(repo.existsByStock_IdAndPriceDate(1, date))
                .thenReturn(true);

        assertTrue(
                service.priceExists(1, date)
        );
    }

    // ==========================
    // BETWEEN DATES
    // ==========================
    @Test
    void getPricesBetweenDates_returnsData() {

        LocalDate s = LocalDate.now();
        LocalDate e = LocalDate.now();

        when(repo.findByStock_IdAndPriceDateBetween(1,s,e))
                .thenReturn(List.of(new StockPrice()));

        List<StockPrice> list =
                service.getPricesBetweenDates(1,s,e);

        assertEquals(1, list.size());
    }
}
