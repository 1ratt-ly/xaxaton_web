package com.hackaton.website.dto;

import com.hackaton.website.service.csv.ImportJobStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ImportJobResponse(
        UUID jobId,
        ImportJobStatus.State state,
        int totalRows,
        int successCount,
        int failedCount,
        String message,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        List<String> errorsSample
) {
    public static ImportJobResponse from(ImportJobStatus s) {
        return new ImportJobResponse(
                s.getJobId(),
                s.getState(),
                s.getTotalRows(),
                s.getSuccessCount(),
                s.getFailedCount(),
                s.getMessage(),
                s.getCreatedAt(),
                s.getStartedAt(),
                s.getFinishedAt(),
                s.getErrorsSample()
        );
    }
}