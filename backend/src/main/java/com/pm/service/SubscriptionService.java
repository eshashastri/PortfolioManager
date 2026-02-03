package com.pm.service;

import com.pm.entity.Subscription;
import com.pm.repo.SubscriptionRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepo repo;
    private final FlaskIngestionService ingestionService;

    public SubscriptionService(
            SubscriptionRepo repo,
            FlaskIngestionService ingestionService
    ) {
        this.repo = repo;
        this.ingestionService = ingestionService;
    }

    public Subscription save(Subscription s) {

        // prevent duplicates
        if (repo.existsByTicker(s.getTicker())) {
            return null;
        }

        // 1️⃣ Save subscription immediately (FAST)
        Subscription saved = repo.save(s);

        // 2️⃣ Trigger async ingestion (NON-BLOCKING)
        ingestionService.ingestStock(saved.getTicker(), "1y");

        // 3️⃣ Return instantly to frontend
        return saved;
    }

    public List<Subscription> getAll() {
        return repo.findAll();
    }

    public void delete(String ticker) {
        repo.deleteByTicker(ticker);
    }
}
