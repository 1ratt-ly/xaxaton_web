import { useEffect, useRef, useState } from "react";
import { uploadCsv, getImportStatus } from "../lib/api";

type ImportState = "PENDING" | "RUNNING" | "DONE" | "FAILED";

type ImportJobCamel = {
    jobId: string;
    state: ImportState;
    totalRows: number;
    successCount: number;
    failedCount: number;
    message?: string | null;
    errorsSample?: string[];
};

type ImportJobSnake = {
    job_id: string;
    state: ImportState;
    total_rows: number;
    success_count: number;
    failed_count: number;
    message?: string | null;
    errors_sample?: string[];
};

type ImportJob = ImportJobCamel | ImportJobSnake;

function normalizeJob(j: ImportJobCamel | ImportJobSnake): ImportJobCamel {
    // camelCase бек (Spring record) :contentReference[oaicite:3]{index=3}
    if ("jobId" in j) {
        return {
            jobId: j.jobId,
            state: j.state,
            totalRows: j.totalRows ?? 0,
            successCount: j.successCount ?? 0,
            failedCount: j.failedCount ?? 0,
            message: j.message ?? null,
            errorsSample: j.errorsSample ?? [],
        };
    }

    // snake_case (если у тебя ещё старый бек)
    return {
        jobId: j.job_id,
        state: j.state,
        totalRows: j.total_rows ?? 0,
        successCount: j.success_count ?? 0,
        failedCount: j.failed_count ?? 0,
        message: j.message ?? null,
        errorsSample: j.errors_sample ?? [],
    };
}

export default function ImportCsvView() {
    const [csvFile, setCsvFile] = useState<File | null>(null);
    const [msg, setMsg] = useState<string | null>(null);

    const [jobStatus, setJobStatus] = useState<ImportJobCamel | null>(null);
    const pollingRef = useRef<number | null>(null);

    const stopPolling = () => {
        if (pollingRef.current !== null) {
            clearInterval(pollingRef.current);
            pollingRef.current = null;
        }
    };

    useEffect(() => {
        return () => stopPolling(); // чистим интервал при размонтировании
    }, []);

    const startPolling = (jobId: string) => {
        stopPolling();

        pollingRef.current = window.setInterval(async () => {
            try {
                const statusRaw = (await getImportStatus(jobId)) as ImportJob;
                const status = normalizeJob(statusRaw);
                setJobStatus(status);

                if (status.state === "DONE" || status.state === "FAILED") {
                    stopPolling();
                }
            } catch {
                stopPolling();
            }
        }, 1000);
    };

    const handleUpload = async () => {
        if (!csvFile) {
            setMsg("Please select a file first");
            return;
        }

        setMsg(null);
        setJobStatus(null);

        try {
            const jobRaw = (await uploadCsv(csvFile)) as ImportJob;
            const job = normalizeJob(jobRaw);

            setJobStatus(job);
            startPolling(job.jobId);
        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : "Upload failed";
            setMsg(message);
        }
    };

    const isWorking = jobStatus && jobStatus.state !== "DONE" && jobStatus.state !== "FAILED";

    return (
        <div className="card">
            <div className="filters" style={{ gridTemplateColumns: "1fr auto" }}>
                <div className="label">
                    <span>CSV file</span>
                    <div className="fileRow">
                        <input
                            id="csvFile"
                            className="fileInput"
                            type="file"
                            accept=".csv"
                            style={{ display: "none" }}
                            onChange={(e) => {
                                const f = e.target.files?.[0] ?? null;
                                setCsvFile(f);
                                setMsg(null);
                            }}
                        />

                        <label className="btn" htmlFor="csvFile" style={{ cursor: "pointer", marginRight: 10 }}>
                            Choose file
                        </label>

                        <span className="fileName">{csvFile ? csvFile.name : "No file chosen"}</span>
                    </div>
                </div>

                <button className="btn" onClick={handleUpload} disabled={!csvFile || Boolean(isWorking)} style={{ alignSelf: "end" }}>
                    Upload & Import
                </button>
            </div>

            {msg && <div className="error" style={{ marginTop: 14 }}>{msg}</div>}

            {jobStatus && (
                <div style={{ marginTop: 20, padding: 10, background: "rgba(255,255,255,0.05)", borderRadius: 8 }}>
                    <h4>Import Status: {jobStatus.state}</h4>
                    <p>Processed Rows: {jobStatus.totalRows}</p>
                    <p style={{ color: "lightgreen" }}>Success: {jobStatus.successCount}</p>
                    <p style={{ color: "salmon" }}>Failed: {jobStatus.failedCount}</p>

                    {jobStatus.message && <p>Message: {jobStatus.message}</p>}

                    {(jobStatus.errorsSample?.length ?? 0) > 0 && (
                        <div style={{ marginTop: 10 }}>
                            <strong>Errors:</strong>
                            <ul style={{ color: "salmon", fontSize: 12, paddingLeft: 20 }}>
                                {(jobStatus.errorsSample ?? []).map((err: string, i: number) => (
                                    <li key={i}>{err}</li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}