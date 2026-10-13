package com.example.flowManager.controller;

import com.example.flowManager.enums.FileStatus;
import com.example.flowManager.repository.FileRecordRepository;
import com.example.flowManager.service.FileFlowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@RequiredArgsConstructor
public class FileFlowController {

    private final FileRecordRepository repository;
    private final FileFlowService fileFlowService;

    @Value("${minio.bucketName}")
    private String bucketName;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Запрос на загрузку файла: {}", file.getOriginalFilename());
        return ResponseEntity.ok(fileFlowService.processIncomingFile(file, bucketName));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<FileStatus> getStatus(@PathVariable UUID id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok(record.getStatus()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadResult(@PathVariable UUID id) throws Exception {
        log.info("Запрос временной Presigned-ссылки для ID: [{}]", id);
        String presignedUrl = fileFlowService.presignedDownloadUrl(id, bucketName);
        return ResponseEntity.ok(presignedUrl);
    }

}
