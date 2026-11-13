package com.example.flowManager.config;

import com.example.flowManager.exception.FileRecordNotFoundException;
import com.example.flowManager.exception.FileNotReadyException;
import com.example.flowManager.exception.FileSizeLimitExceededException;
import com.example.flowManager.exception.SubscriptionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(FileRecordNotFoundException.class)
    public ResponseEntity<String> handleFileRecordNotFound(
            FileRecordNotFoundException e
    ) {
        log.warn("Файл отсутствует в системе: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(FileNotReadyException.class)
    public ResponseEntity<String> handleBadRequest(FileNotReadyException e) {
        log.warn("Попытка скачать не готовый файл: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<String> handleSubscriptionNotFound(
            SubscriptionNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUnknownErrors(Exception e) {
        log.error("Критическая системная ошибка инфраструктуры", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера.");
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<String> handleFileSizeLimitExceeded(
            FileSizeLimitExceededException e
    ) {
        log.warn("Загрузка отклонена из-за размера файла: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(e.getMessage());
    }
}
