package com.example.flowManager.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCacheInvalidationListener {

    private final CacheManager cacheManager;
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

            Cache subscriptionsCache = cacheManager.getCache("subscriptions");

            if (subscriptionsCache == null) {
                log.warn("Кеш subscriptions не найден");
                return;
            }

            subscriptionsCache.evict(login);
            log.info("Кеш подписки пользователя {} удалён", login);
        } catch (Exception exception) {
            log.error("Не удалось обработать событие инвалидации кеша: {}", message, exception);
        }
    }
}