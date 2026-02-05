package com.pm.service;

import com.pm.entity.Subscription;
import com.pm.repo.SubscriptionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepo repo;

    @Mock
    private FlaskIngestionService ingestionService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    // ==========================
    // SAVE SUCCESS
    // ==========================
    @Test
    void save_newSubscription_triggersIngestion() {

        Subscription sub = new Subscription();
        sub.setTicker("AAPL");

        when(repo.existsByTicker("AAPL"))
                .thenReturn(false);

        when(repo.save(sub))
                .thenReturn(sub);

        Subscription result =
                subscriptionService.save(sub);

        assertNotNull(result);

        verify(repo).save(sub);
        verify(ingestionService)
                .ingestStock("AAPL", "1y");
    }

    // ==========================
    // DUPLICATE BLOCK
    // ==========================
    @Test
    void save_duplicate_returnsNull() {

        Subscription sub = new Subscription();
        sub.setTicker("AAPL");

        when(repo.existsByTicker("AAPL"))
                .thenReturn(true);

        Subscription result =
                subscriptionService.save(sub);

        assertNull(result);

        verify(repo, never()).save(any());
        verifyNoInteractions(ingestionService);
    }

    // ==========================
    // GET ALL
    // ==========================
    @Test
    void getAll_returnsList() {

        List<Subscription> list =
                List.of(new Subscription(), new Subscription());

        when(repo.findAll()).thenReturn(list);

        List<Subscription> result =
                subscriptionService.getAll();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    // ==========================
    // DELETE
    // ==========================
    @Test
    void delete_callsRepo() {

        subscriptionService.delete("AAPL");

        verify(repo).deleteByTicker("AAPL");
    }
}
