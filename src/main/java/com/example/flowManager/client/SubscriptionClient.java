package com.example.flowManager.client;

import com.example.flowManager.dto.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/v1/subscriptions/{login}")
    SubscriptionResponse getByLogin(@PathVariable("login") String login);
}