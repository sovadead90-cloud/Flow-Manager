package com.example.flowManager.controller;

import com.example.flowManager.exception.FileRecordNotFoundException;
import com.example.flowManager.exception.FileNotReadyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FileRecordNotFoundException.class)
    public ResponseEntity<String> handleNotFound(FileRecordNotFoundException e) {
        log.warn("Файл отсутствует в системе: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(FileNotReadyException.class)
    public ResponseEntity<String> handleBadRequest(FileNotReadyException e) {
        log.warn("Попытка скачать не готовый файл: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUnknownErrors(Exception e) {
        log.error("Критическая системная ошибка инфраструктуры", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                body("Внутренняя ошибка сервера при работе с хранилищем.");
    }
}
