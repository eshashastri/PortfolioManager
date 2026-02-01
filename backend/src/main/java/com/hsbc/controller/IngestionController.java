package com.hsbc.controller;

import com.hsbc.service.FlaskIngestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ingest")
public class IngestionController {

    private final FlaskIngestionService ingestionService;

    public IngestionController(FlaskIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/{ticker}")
    public String ingestStock(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "3mo") String period
    ) {
        ingestionService.ingestStock(ticker, period);
        return "Ingestion completed for " + ticker;
    }
}
