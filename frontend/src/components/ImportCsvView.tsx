import { useState, useRef } from "react";
import { uploadCsv, getImportStatus } from "../lib/api";

export default function ImportCsvView() {
    const[csvFile, setCsvFile] = useState<File | null>(null);
    const [msg, setMsg] = useState<string | null>(null);

    const [jobStatus, setJobStatus] = useState<any>(null);
    const pollingRef = useRef<number | null>(null);

    const startPolling = (jobId: string) => {
        pollingRef.current = window.setInterval(async () => {
            try {
                const status = await getImportStatus(jobId);
                setJobStatus(status);

                if (status.state === "DONE" || status.state === "FAILED") {
                    if (pollingRef.current) clearInterval(pollingRef.current);
                }
            } catch (e) {
                if (pollingRef.current) clearInterval(pollingRef.current);
            }
        }, 1000); // Опитуємо кожну секунду
    };

    const handleUpload = async () => {
        if (!csvFile) {
            setMsg("Please select a file first");
            return;
        }

        setMsg(null);
        setJobStatus(null);

        try {
            const job = await uploadCsv(csvFile);
            setJobStatus(job);
            startPolling(job.jobId);
        } catch (err: any) {
            setMsg(err.message || "Upload failed");
        }
    };

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
                            style={{ display: "none" }} // Ховаємо стандартний інпут
                            onChange={(e) => {
                                const f = e.target.files?.[0] ?? null;
                                setCsvFile(f);
                                setMsg(null);
                            }}
                        />

                        <label className="btn" htmlFor="csvFile" style={{ cursor: "pointer", marginRight: "10px" }}>
                            Choose file
                        </label>

                        <span className="fileName">{csvFile ? csvFile.name : "No file chosen"}</span>
                    </div>
                </div>

                <button className="btn" onClick={handleUpload} disabled={!csvFile || jobStatus?.state === "RUNNING"} style={{ alignSelf: "end" }}>
                    Upload & Import
                </button>
            </div>

            {msg && <div className="error" style={{ marginTop: 14 }}>{msg}</div>}

            {/* Відображення статусу імпорту */}
            {jobStatus && (
                <div style={{ marginTop: 20, padding: 10, background: "rgba(255,255,255,0.05)", borderRadius: 8 }}>
                    <h4>Import Status: {jobStatus.state}</h4>
                    <p>Processed Rows: {jobStatus.totalRows}</p>
                    <p style={{ color: "lightgreen" }}>Success: {jobStatus.successCount}</p>
                    <p style={{ color: "salmon" }}>Failed: {jobStatus.failedCount}</p>

                    {jobStatus.errorsSample && jobStatus.errorsSample.length > 0 && (
                        <div style={{ marginTop: 10 }}>
                            <strong>Errors:</strong>
                            <ul style={{ color: "salmon", fontSize: 12, paddingLeft: 20 }}>
                                {jobStatus.errorsSample.map((err: string, i: number) => (
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