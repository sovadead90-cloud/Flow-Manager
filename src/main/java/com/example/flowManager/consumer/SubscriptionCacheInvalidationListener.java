package com.example.flowManager.consumer;

import com.example.flowManager.service.SubscriptionLookupService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCacheInvalidationListener {

    private final SubscriptionLookupService subscriptionLookupService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "subscription-cache-invalidation",
            groupId = "flow-manager-subscription-cache"
    )
    public void invalidateSubscriptionCache(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String login = event.path("login").asText(null);

            if (login == null || login.isBlank()) {
                log.warn("В Kafka-событии инвалидации отсутствует login: {}", message);
                return;
            }
            subscriptionLookupService.evictSubscriptionCache(login);
        } catch (Exception exception) {
            log.error("Не удалось обработать событие инвалидации кеша: {}", message, exception);
        }
    }
}