package com.example.flowManager.controller;

import com.example.flowManager.enums.FileStatus;
import com.example.flowManager.service.FileFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileFlowController {

    private final FileFlowService fileFlowService;

    @Value("${minio.bucketName}")
    private String bucketName;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Login") String login
    ) throws Exception {
        return ResponseEntity.ok(
                fileFlowService.processIncomingFile(file, bucketName, login)
        );
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<FileStatus> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(fileFlowService.getFileStatus(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> downloadResult(@PathVariable UUID id) throws Exception {
        String presignedUrl = fileFlowService.presignedDownloadUrl(id, bucketName);
        return ResponseEntity.ok(presignedUrl);
    }
}
