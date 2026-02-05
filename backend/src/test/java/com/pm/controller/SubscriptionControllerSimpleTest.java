package com.pm.controller;

import com.pm.entity.Subscription;
import com.pm.service.SubscriptionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionControllerSimpleTest {

    // Fake service (manual stub)
    static class FakeSubscriptionService extends SubscriptionService {

        public FakeSubscriptionService() {
            super(null, null);
        }

        @Override
        public Subscription save(Subscription s) {
            return s;
        }

        @Override
        public List<Subscription> getAll() {
            return List.of(
                    new Subscription("AAPL","Apple Inc"),
                    new Subscription("GOOG","Google")
            );
        }

        @Override
        public void delete(String ticker) {
            // do nothing
        }
    }

    // ======================
    // TEST SAVE
    // ======================
    @Test
    void save_shouldReturnSubscription() {

        SubscriptionService fakeService =
                new FakeSubscriptionService();

        SubscriptionController controller =
                new SubscriptionController(fakeService);

        Subscription sub =
                new Subscription("AAPL","Apple Inc");

        Subscription result =
                controller.save(sub);

        assertEquals("AAPL", result.getTicker());
    }

    // ======================
    // TEST GET ALL
    // ======================
    @Test
    void getAll_shouldReturnList() {

        SubscriptionController controller =
                new SubscriptionController(
                        new FakeSubscriptionService());

        List<Subscription> list =
                controller.getAll();

        assertEquals(2, list.size());
    }

    // ======================
    // TEST DELETE
    // ======================
    @Test
    void delete_shouldNotThrowError() {

        SubscriptionController controller =
                new SubscriptionController(
                        new FakeSubscriptionService());

        assertDoesNotThrow(() ->
                controller.delete("AAPL"));
    }
}
