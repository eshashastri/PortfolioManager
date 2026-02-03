package com.pm.repo;

import com.pm.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface SubscriptionRepo extends JpaRepository<Subscription, Integer> {

    boolean existsByTicker(String ticker);

    @Modifying
    void deleteByTicker(String ticker);
}
