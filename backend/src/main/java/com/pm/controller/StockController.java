package com.pm.controller;

import com.pm.entity.Stock;
import com.pm.service.StockService;
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
