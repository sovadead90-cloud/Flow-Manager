package com.example.flowManager.dto;

import java.util.UUID;

public record ConversionCommand(
        UUID messageId,
        UUID sagaId,
        String minioPath
) {}
