package com.pm.service;

import com.pm.entity.StockPrice;
import com.pm.repo.StockPriceRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockPriceServiceTest {

    @Mock
    private StockPriceRepo stockPriceRepo;

    @InjectMocks
    private StockPriceService stockPriceService;

    // ==========================
    // savePrice
    // ==========================
    @Test
    void savePrice_delegatesToRepo() {

        StockPrice price = new StockPrice();

        when(stockPriceRepo.save(price))
                .thenReturn(price);

        StockPrice result =
                stockPriceService.savePrice(price);

        assertEquals(price, result);
        verify(stockPriceRepo).save(price);
    }

    // ==========================
    // getPricesForStock
    // ==========================
    @Test
    void getPricesForStock_returnsList() {

        List<StockPrice> mockList =
                List.of(new StockPrice(), new StockPrice());

        when(stockPriceRepo.findByStock_Id(1))
                .thenReturn(mockList);

        List<StockPrice> result =
                stockPriceService.getPricesForStock(1);

        assertEquals(2, result.size());
        verify(stockPriceRepo).findByStock_Id(1);
    }

    // ==========================
    // priceExists
    // ==========================
    @Test
    void priceExists_returnsTrue() {

        LocalDate date = LocalDate.now();

        when(stockPriceRepo
                .existsByStock_IdAndPriceDate(1, date))
                .thenReturn(true);

        boolean exists =
                stockPriceService.priceExists(1, date);

        assertTrue(exists);
        verify(stockPriceRepo)
                .existsByStock_IdAndPriceDate(1, date);
    }

    // ==========================
    // getPricesBetweenDates
    // ==========================
    @Test
    void getPricesBetweenDates_returnsResults() {

        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        List<StockPrice> mockList =
                List.of(new StockPrice());

        when(stockPriceRepo
                .findByStock_IdAndPriceDateBetween(1, start, end))
                .thenReturn(mockList);

        List<StockPrice> result =
                stockPriceService
                        .getPricesBetweenDates(1, start, end);

        assertEquals(1, result.size());

        verify(stockPriceRepo)
                .findByStock_IdAndPriceDateBetween(1, start, end);
    }
}
