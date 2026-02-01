package com.hsbc.service;


import com.hsbc.entity.Subscription;
import com.hsbc.repo.SubscriptionRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepo repo;

    public SubscriptionService(SubscriptionRepo repo) {
        this.repo = repo;
    }

    public Subscription save(Subscription s) {

        if(repo.existsByTicker(s.getTicker())) {
            return null; // prevent duplicates
        }

        return repo.save(s);
    }

    public List<Subscription> getAll() {
        return repo.findAll();
    }

    public void delete(String ticker) {
        repo.deleteByTicker(ticker);
    }
}
