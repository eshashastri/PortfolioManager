package com.hsbc.controller;

import com.hsbc.entity.Subscription;
import com.hsbc.service.SubscriptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@CrossOrigin
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PostMapping
    public Subscription save(@RequestBody Subscription s) {
        return service.save(s);
    }

    @GetMapping
    public List<Subscription> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{ticker}")
    public void delete(@PathVariable String ticker) {
        service.delete(ticker);
    }
}
