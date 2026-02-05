package com.pm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.entity.Subscription;
import com.pm.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService service;

    @Autowired
    private ObjectMapper objectMapper;

    // ==========================
    // POST TEST
    // ==========================
    @Test
    void save_subscription_success() throws Exception {

        Subscription sub = new Subscription();
        sub.setId(1L);
        sub.setTicker("AAPL");
        sub.setCompanyName("Apple Inc");

        when(service.save(any(Subscription.class)))
                .thenReturn(sub);

        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sub)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("AAPL"))
                .andExpect(jsonPath("$.companyName").value("Apple Inc"));

        verify(service).save(any(Subscription.class));
    }

    // ==========================
    // GET TEST
    // ==========================
    @Test
    void getAll_returnsSubscriptions() throws Exception {

        Subscription s1 =
                new Subscription("AAPL","Apple Inc");
        Subscription s2 =
                new Subscription("GOOG","Google");

        when(service.getAll())
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ticker").value("AAPL"))
                .andExpect(jsonPath("$[1].ticker").value("GOOG"));

        verify(service).getAll();
    }

    // ==========================
    // DELETE TEST
    // ==========================
    @Test
    void delete_subscription_success() throws Exception {

        doNothing().when(service).delete("AAPL");

        mockMvc.perform(delete("/subscriptions/AAPL"))
                .andExpect(status().isOk());

        verify(service).delete("AAPL");
    }
}
