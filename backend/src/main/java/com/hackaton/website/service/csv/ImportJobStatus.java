package com.hackaton.website.service.csv;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ImportJobStatus {

    public enum State { PENDING, RUNNING, DONE, FAILED }

    private final UUID jobId;
    private volatile State state = State.PENDING;

    private volatile int totalRows = 0;
    private volatile int successCount = 0;
    private volatile int failedCount = 0;

    private volatile String message = null;

    private final Instant createdAt = Instant.now();
    private volatile Instant startedAt = null;
    private volatile Instant finishedAt = null;

    private final List<String> errorsSample = new ArrayList<>();

    public ImportJobStatus(UUID jobId) { this.jobId = jobId; }

    public UUID getJobId() { return jobId; }
    public State getState() { return state; }
    public int getTotalRows() { return totalRows; }
    public int getSuccessCount() { return successCount; }
    public int getFailedCount() { return failedCount; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public List<String> getErrorsSample() { return errorsSample; }

    public void markRunning() { this.state = State.RUNNING; this.startedAt = Instant.now(); }
    public void markDone() { this.state = State.DONE; this.finishedAt = Instant.now(); }
    public void markFailed(String msg) { this.state = State.FAILED; this.message = msg; this.finishedAt = Instant.now(); }

    public void incTotal() { totalRows++; }
    public void incSuccess() { successCount++; }
    public void incFailed(String err) {
        failedCount++;
        if (errorsSample.size() < 20) errorsSample.add(err);
    }
}