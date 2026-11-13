package com.example.flowManager.service;

import com.example.flowManager.client.SubscriptionClient;
import com.example.flowManager.dto.SubscriptionResponse;
import com.example.flowManager.exception.SubscriptionNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionLookupService {

    private final SubscriptionClient subscriptionClient;

    @Cacheable(cacheNames = "subscriptions", key = "#login")
    public SubscriptionResponse getByLogin(String login) {

        try {
            return subscriptionClient.getByLogin(login);
        } catch (FeignException.NotFound exception) {
            throw new SubscriptionNotFoundException(
                    "Подписка пользователя '" + login
                            + "' не найдена. Добавьте пользователя вручную в таблицу subscriptions.",
                    exception
            );
        }
    }

    @CacheEvict(cacheNames = "subscriptions", key = "#login")
    public void evictSubscriptionCache(String login) {
        log.info("Кеш подписки пользователя {} удалён", login);
    }
}