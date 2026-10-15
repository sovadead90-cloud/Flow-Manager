package com.example.flowManager.exception;

public class FileNotReadyException extends RuntimeException {
    public FileNotReadyException(String message) {
        super(message);
    }
}
