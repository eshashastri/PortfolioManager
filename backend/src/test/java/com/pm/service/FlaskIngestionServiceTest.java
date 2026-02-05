package com.pm.service;

import com.pm.dto.FlaskMetadataDTO;
import com.pm.dto.FlaskPriceDTO;
import com.pm.dto.FlaskStockResponseDTO;
import com.pm.entity.Stock;
import com.pm.entity.StockPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlaskIngestionServiceTest {

    @Mock
    RestTemplate restTemplate;

    @Mock
    StockService stockService;

    @Mock
    StockPriceService stockPriceService;

    @InjectMocks
    FlaskIngestionService service;

    // =========================
    // TEST 1
    // =========================
    @Test
    void ingestStock_savesNewStockAndPrice() {

        FlaskMetadataDTO metadata = new FlaskMetadataDTO();
        metadata.setTicker("AAPL");
        metadata.setCompanyName("Apple Inc");

        FlaskPriceDTO priceDTO = new FlaskPriceDTO();
        priceDTO.setDate("2025-01-01");
        priceDTO.setOpen(100.0);
        priceDTO.setHigh(110.0);
        priceDTO.setLow(90.0);
        priceDTO.setClose(105.0);
        priceDTO.setVolume(1000L);

        FlaskStockResponseDTO response =
                new FlaskStockResponseDTO();
        response.setMetadata(metadata);
        response.setHistoricalData(List.of(priceDTO));

        doReturn(response).when(restTemplate)
                .getForObject(anyString(),
                        eq(FlaskStockResponseDTO.class));

        // stock not exists
        doReturn(null).when(stockService)
                .getByTicker("AAPL");

        Stock mockStock = mock(Stock.class);
        doReturn(1L).when(mockStock).getId();

        doReturn(mockStock).when(stockService)
                .saveStock(any());

        // FIXED LINE
        doReturn(false).when(stockPriceService)
                .priceExists((int) anyLong(),
                        any(LocalDate.class));

        service.ingestStock("AAPL", "1mo");

        verify(stockService).saveStock(any());
        verify(stockPriceService)
                .savePrice(any(StockPrice.class));
    }

    // =========================
    // TEST 2
    // =========================
    @Test
    void ingestStock_duplicatePrice_notSaved() {

        FlaskMetadataDTO metadata = new FlaskMetadataDTO();
        metadata.setTicker("AAPL");
        metadata.setCompanyName("Apple Inc");

        FlaskPriceDTO priceDTO = new FlaskPriceDTO();
        priceDTO.setDate("2025-01-01");

        FlaskStockResponseDTO response =
                new FlaskStockResponseDTO();
        response.setMetadata(metadata);
        response.setHistoricalData(List.of(priceDTO));

        doReturn(response).when(restTemplate)
                .getForObject(anyString(),
                        eq(FlaskStockResponseDTO.class));

        Stock mockStock = mock(Stock.class);
        doReturn(1L).when(mockStock).getId();

        doReturn(mockStock).when(stockService)
                .getByTicker("AAPL");

        // FIXED LINE
        doReturn(true).when(stockPriceService)
                .priceExists((int) anyLong(),
                        any(LocalDate.class));

        service.ingestStock("AAPL", "1mo");

        verify(stockService, never())
                .saveStock(any());
        verify(stockPriceService, never())
                .savePrice(any());
    }
}
