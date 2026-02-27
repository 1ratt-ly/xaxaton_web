import { useState } from "react";

function round2(n: number) {
  return Math.round(n * 100) / 100;
}

export default function CreateView() {
  const [lat, setLat] = useState("");
  const [lon, setLon] = useState("");
  const [subtotal, setSubtotal] = useState("");

  const [rate, setRate] = useState<number | null>(null);
  const [tax, setTax] = useState<number | null>(null);
  const [total, setTotal] = useState<number | null>(null);

  const calculate = () => {
    if (!subtotal) return;

    // підключити що б разхувало ставку
    const mockRate = 0.08875;

    const sub = Number(subtotal);
    const taxAmount = round2(sub * mockRate);
    const totalAmount = round2(sub + taxAmount);

    setRate(mockRate);
    setTax(taxAmount);
    setTotal(totalAmount);
  };

  return (
    <div className="card">
      <div className="filters" style={{ gridTemplateColumns: "1fr 1fr 1fr auto auto" }}>
        <label className="label">
          <span>Latitude</span>
          <input
            className="input mono"
            value={lat}
            onChange={(e) => setLat(e.target.value)}
            placeholder="40.7128"
          />
        </label>

        <label className="label">
          <span>Longitude</span>
          <input
            className="input mono"
            value={lon}
            onChange={(e) => setLon(e.target.value)}
            placeholder="-74.0060"
          />
        </label>

        <label className="label">
          <span>Subtotal</span>
          <input
            className="input mono"
            value={subtotal}
            onChange={(e) => setSubtotal(e.target.value)}
            type="number"
            min="0"
            step="0.01"
            placeholder="100"
          />
        </label>

        <button className="btn" onClick={calculate}>
          Calculate
        </button>

        <button className="btn">
          Create
        </button>
      </div>

      {rate !== null && (
        <div style={{ marginTop: 14, fontSize: 14 }}>
          <div>Tax rate: {(rate * 100).toFixed(3)}%</div>
          <div>Tax amount: ${tax?.toFixed(2)}</div>
          <div><b>Total: ${total?.toFixed(2)}</b></div>
        </div>
      )}
    </div>
  );
}