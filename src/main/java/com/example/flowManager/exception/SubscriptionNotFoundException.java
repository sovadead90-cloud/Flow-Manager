package com.example.flowManager.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}