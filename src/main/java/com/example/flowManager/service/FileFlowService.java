package com.example.flowManager.service;

import com.example.flowManager.exception.FileRecordNotFoundException;
import com.example.flowManager.exception.FileNotReadyException;
import com.example.flowManager.domain.FileRecord;
import com.example.flowManager.enums.FileStatus;
import com.example.flowManager.exception.FileSizeLimitExceededException;
import com.example.flowManager.repository.FileRecordRepository;
import com.example.flowManager.producer.KafkaCommandProducer;
import com.example.flowManager.dto.SubscriptionResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileFlowService {

    private static final long MAX_FREE_FILE_SIZE_BYTES = 100L * 1024 * 1024;
    private final FileRecordRepository repository;
    private final KafkaCommandProducer commandProducer;
    private final MinioClient minioClient;
    private final SubscriptionLookupService subscriptionLookupService;

    @Transactional
    public UUID processIncomingFile(
            MultipartFile file,
            String bucketName,
            String login
    ) throws Exception {
        SubscriptionResponse subscription =
                subscriptionLookupService.getByLogin(login);

        log.info(
                "Для пользователя {} получена подписка: тип={}, активна={}",
                login,
                subscription.type(),
                subscription.active()
        );

        boolean activePaidSubscription =
                "PAID".equals(subscription.type()) && subscription.active();

        if (!activePaidSubscription && file.getSize() > MAX_FREE_FILE_SIZE_BYTES) {
            throw new FileSizeLimitExceededException(
                    "Для бесплатной или истёкшей подписки максимальный размер файла — 100 МБ."
            );
        }

        UUID fileId = UUID.randomUUID();
        String minioPath = "source/" + fileId + "_" + file.getOriginalFilename();

        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(minioPath)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());

        FileRecord record = FileRecord.builder()
                .id(fileId)
                .status(FileStatus.PROCESSING)
                .sourcePath(minioPath)
                .build();
        repository.save(record);

        commandProducer.sendConversionCommand(fileId, minioPath);

        return fileId;
    }

    public String presignedDownloadUrl(UUID id, String bucketName) throws Exception {
        FileRecord record = repository.findById(id)
                .orElseThrow(() -> new FileRecordNotFoundException("Файл с ID " + id + " не найден в базе данных"));

        if (record.getStatus() != FileStatus.SUCCESS) {
            throw new FileNotReadyException("Файл еще обрабатывается или сконвертирован с ошибкой.");
        }

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(record.getResultPath())
                        .expiry(15, TimeUnit.MINUTES)
                        .build()
        );
    }

    public FileStatus getFileStatus(UUID id) {
        return repository.findById(id)
                .map(FileRecord::getStatus)
                .orElseThrow(() -> new FileRecordNotFoundException("Файл с ID " + id + " не найден в базе данных"));
    }
}
