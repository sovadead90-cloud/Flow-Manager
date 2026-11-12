package com.example.flowManager.exception;

public class FileSizeLimitExceededException extends RuntimeException {

    public FileSizeLimitExceededException(String message) {
        super(message);
    }
}