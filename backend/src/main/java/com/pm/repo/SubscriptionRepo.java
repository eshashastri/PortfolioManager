package com.hsbc.repo;

import com.hsbc.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepo extends JpaRepository<Subscription, Integer> {

    boolean existsByTicker(String ticker);

    void deleteByTicker(String ticker);
}
