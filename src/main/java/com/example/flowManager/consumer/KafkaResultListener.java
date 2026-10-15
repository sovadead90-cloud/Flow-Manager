package com.example.flowManager.consumer;

import com.example.flowManager.domain.FileRecord;
import com.example.flowManager.enums.FileStatus;
import com.example.flowManager.repository.FileRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaResultListener {

    private final FileRecordRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "file-conversion-results", groupId = "flow-manager-group")
    public void handleConversionResult(String message) {
        log.info("Получено новое событие из топика результатов: {}", message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            UUID sagaId = UUID.fromString(jsonNode.get("sagaId").asText());
            String status = jsonNode.get("status").asText();

            FileRecord fileRecord = repository.findById(sagaId)
                    .orElseThrow(() -> new RuntimeException("Транзакция SAGA с ID " + sagaId + " не найдена в БД"));

            if ("SUCCESS".equals(status)) {
                fileRecord.setStatus(FileStatus.SUCCESS);
                fileRecord.setResultPath("converted/" + sagaId + ".pdf");
                log.info("SAGA [{}]: Конвертация завершена УСПЕШНО. Статус обновлен в БД.", sagaId);
            } else {
                fileRecord.setStatus(FileStatus.ERROR);
                log.warn("SAGA [{}]: Конвертация завершилась ОШИБКОЙ на стороне сервиса-исполнителя.", sagaId);
            }

            repository.save(fileRecord);

        } catch (Exception e) {
            log.error("Критическая ошибка при десериализации или сохранении результата конвертации", e);
        }
    }
}
