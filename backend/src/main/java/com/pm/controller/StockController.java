package com.hsbc.controller;

import com.hsbc.entity.Stock;
import com.hsbc.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@CrossOrigin
public class StockController {

    private final StockService service;

    public StockController(StockService service){
        this.service = service;
    }

    @GetMapping("/search")
    public List<Stock> search(@RequestParam String q){
        return service.search(q);
    }
}
