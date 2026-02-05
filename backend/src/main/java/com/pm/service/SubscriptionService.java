package com.pm.service;

import com.pm.entity.Subscription;
import com.pm.repo.SubscriptionRepo;
import com.pm.exceptions.SubscriptionException;  // Import the renamed exception
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

    // Save subscription, prevent duplicates
    public Subscription save(Subscription s) {
        try {
            // Prevent duplicates
            if (repo.existsByTicker(s.getTicker())) {
                return null; // Or throw a custom exception here if needed
            }

            // Save subscription
            Subscription saved = repo.save(s);

            // Call FlaskIngestionService for stock ingestion
            ingestionService.ingestStock(saved.getTicker(), "1y");

            return saved;
        } catch (Exception e) {
            // Log and throw a custom exception with error details
            throw new SubscriptionException("Error occurred while saving subscription", "SUBSCRIPTION_SAVE_ERROR", 500, e);
        }
    }

    // Get all subscriptions
    public List<Subscription> getAll() {
        try {
            return repo.findAll();
        } catch (Exception e) {
            // Log and throw a custom exception if there's an error while retrieving subscriptions
            throw new SubscriptionException("Error occurred while retrieving all subscriptions", "SUBSCRIPTION_RETRIEVE_ERROR", 500, e);
        }
    }

    // Delete a subscription by ticker
    public void delete(String ticker) {
        try {
            repo.deleteByTicker(ticker);
        } catch (Exception e) {
            // Log and throw a custom exception if there's an error while deleting a subscription
            throw new SubscriptionException("Error occurred while deleting subscription with ticker: " + ticker, "SUBSCRIPTION_DELETE_ERROR", 500, e);
        }
    }
}
