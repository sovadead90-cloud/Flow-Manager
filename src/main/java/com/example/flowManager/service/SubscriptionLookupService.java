package com.example.flowManager.service;

import com.example.flowManager.client.SubscriptionClient;
import com.example.flowManager.dto.SubscriptionResponse;
import com.example.flowManager.exception.SubscriptionNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}