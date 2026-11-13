package com.example.flowManager.dto;

import java.time.Instant;

public record SubscriptionResponse(
        String login,
        String type,
        Instant expiresAt,
        boolean active
) {
}