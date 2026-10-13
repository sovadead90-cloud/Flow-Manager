package com.example.flowManager.exception;

public class FileRecordNotFoundException extends RuntimeException {
    public FileRecordNotFoundException(String message) {
        super(message);
    }
}
