package com.pm.controller;

import com.pm.entity.Subscription;
import com.pm.service.SubscriptionService;
import jakarta.transaction.Transactional;
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

    @Transactional
    @DeleteMapping("/{ticker}")
    public void delete(@PathVariable String ticker) {
        service.delete(ticker);
    }
}
