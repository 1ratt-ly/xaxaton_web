import { useState } from "react";

export default function ImportCsvView() {
  const [csvFile, setCsvFile] = useState<File | null>(null);
  const [msg, setMsg] = useState<string | null>(null);

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
              onChange={(e) => {
                const f = e.target.files?.[0] ?? null;
                setCsvFile(f);
                setMsg(null);
              }}
            />

            <label className="fileBtn" htmlFor="csvFile">
              Choose file
            </label>

            <span className="fileName">{csvFile ? csvFile.name : "No file chosen"}</span>
          </div>
        </div>

        <button className="btn" onClick={() => setMsg("Потрібен бекенд: POST /api/orders/import")}>
          Upload
        </button>
      </div>

      {msg && <div className="error" style={{ borderColor: "rgba(255,255,255,0.16)", color: "rgba(255,255,255,0.75)" }}>{msg}</div>}
    </div>
  );
}