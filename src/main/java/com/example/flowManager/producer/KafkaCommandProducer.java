package com.example.flowManager.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaCommandProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendConversionCommand(UUID fileId, String minioPath) {

        UUID messageId = UUID.randomUUID();

        String jsonPayload = String.format(
                "{\"messageId\":\"%s\",\"sagaId\":\"%s\",\"minioPath\":\"%s\"}",
                messageId, fileId, minioPath
        );

        log.info("SAGA [{}]: Отправка команды конвертации в топик 'file-conversion-commands'", fileId);

        kafkaTemplate.send("file-conversion-commands", fileId.toString(), jsonPayload);
    }
}
