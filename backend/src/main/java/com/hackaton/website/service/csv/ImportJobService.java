package com.hackaton.website.service.csv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class ImportJobService {

    private final CsvImportService csvImportService;
    private final ExecutorService importExecutor;

    private final Map<UUID, ImportJobStatus> jobs = new ConcurrentHashMap<>();

    public ImportJobService(CsvImportService csvImportService, ExecutorService importExecutor) {
        this.csvImportService = csvImportService;
        this.importExecutor = importExecutor;
    }

    public ImportJobStatus startAsync(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            UUID id = UUID.randomUUID();
            ImportJobStatus status = new ImportJobStatus(id);
            jobs.put(id, status);

            importExecutor.submit(() -> runJob(status, bytes));
            return status;

        } catch (Exception e) {
            throw new RuntimeException("Cannot read uploaded file: " + e.getMessage(), e);
        }
    }

    public ImportJobStatus get(UUID id) {
        return jobs.get(id);
    }

    private void runJob(ImportJobStatus status, byte[] bytes) {
        status.markRunning();
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            csvImportService.importCsv(in, status);
            status.markDone();
        } catch (Exception e) {
            status.markFailed(e.getMessage());
        }
    }
}