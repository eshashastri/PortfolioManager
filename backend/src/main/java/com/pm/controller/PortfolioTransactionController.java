package com.hsbc.controller;

import com.hsbc.dto.BuyRequest;
import com.hsbc.entity.PortfolioTransaction;
import com.hsbc.service.PortfolioTransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolio")
public class PortfolioTransactionController {

    private final PortfolioTransactionService service;

    public PortfolioTransactionController(
            PortfolioTransactionService service
    ) {
        this.service = service;
    }

    // BUY
    @PostMapping("/buy")
    public PortfolioTransaction buy(
            @RequestBody BuyRequest buyRequest
            ) {
        return service.buy(buyRequest);
    }

    // SELL
    @PostMapping("/sell")
    public PortfolioTransaction sell(@RequestBody BuyRequest buyRequest) {
        return service.sell(buyRequest);
    }
}
