package com.pm.controller;

import com.pm.dto.BuyRequest;
import com.pm.dto.HoldingResponseDTO;
import com.pm.dto.TransactionResponseDTO;
import com.pm.entity.PortfolioTransaction;
import com.pm.service.PortfolioTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
@CrossOrigin
public class PortfolioTransactionController {

    private final PortfolioTransactionService service;

    public PortfolioTransactionController(
            PortfolioTransactionService service
    ) {
        this.service = service;
    }
    @GetMapping("/transactions")
    public List<TransactionResponseDTO> getAllTransactions() {
        return service.getAllTransactions();
    }
    // BUY
    @PostMapping("/buy")
    public PortfolioTransaction buy(
            @RequestBody BuyRequest buyRequest
            ) {
        return service.buy(buyRequest);
    }
    @GetMapping("/holdings")
    public List<HoldingResponseDTO> getHoldings() {
        return service.getHoldings();
    }

    //     SELL
    @PostMapping("/sell")
    public PortfolioTransaction sell(@RequestBody BuyRequest buyRequest) {
        return service.sell(buyRequest);
    }
}
