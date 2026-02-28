import { useState } from "react";
import { createOrder } from "../lib/api";

export default function CreateView() {
  const [lat, setLat] = useState("");
  const [lon, setLon] = useState("");
  const [subtotal, setSubtotal] = useState("");

  const[rate, setRate] = useState<number | null>(null);
  const [tax, setTax] = useState<number | null>(null);
  const [total, setTotal] = useState<number | null>(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleCreate = async () => {
    if (!lat || !lon || !subtotal) {
      setError("Заповніть всі поля");
      return;
    }
    const latNum = Number(lat);
    const lonNum = Number(lon);

    if (latNum < -90 || latNum > 90) {
      setError("Помилка: Latitude (Широта) має бути в межах від -90 до 90.");
      return;
    }
    if (lonNum < -180 || lonNum > 180) {
      setError("Помилка: Longitude (Довгота) має бути в межах від -180 до 180.");
      return;
    }
    if (Number(subtotal) < 0) {
      setError("Помилка: Сума (Subtotal) не може бути від'ємною.");
      return;
    }
    setLoading(true);
    setError(null);
    setRate(null);

    try {
      const response = await createOrder({
        latitude: Number(lat),
        longitude: Number(lon),
        subtotal: Number(subtotal)
      });

      // Беремо дані з відповіді бекенду
      setRate(response.compositeTaxRate);
      setTax(response.taxAmount);
      setTotal(response.totalAmount);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="card">
        <div className="filters" style={{ gridTemplateColumns: "1fr 1fr 1fr auto" }}>
          <label className="label">
            <span>Latitude</span>
            <input className="input mono" value={lat} onChange={(e) => setLat(e.target.value)} placeholder="40.7128" />
          </label>

          <label className="label">
            <span>Longitude</span>
            <input className="input mono" value={lon} onChange={(e) => setLon(e.target.value)} placeholder="-74.0060" />
          </label>

          <label className="label">
            <span>Subtotal</span>
            <input className="input mono" value={subtotal} onChange={(e) => setSubtotal(e.target.value)} type="number" min="0" step="0.01" placeholder="100" />
          </label>

          <button className="btn" onClick={handleCreate} disabled={loading} style={{ alignSelf: "end" }}>
            {loading ? "Creating..." : "Create & Calculate"}
          </button>
        </div>

        {error && <div className="error" style={{ marginTop: 14 }}>{error}</div>}

        {rate !== null && !error && (
            <div style={{ marginTop: 14, fontSize: 14 }}>
              <div>Tax rate: {(rate * 100).toFixed(3)}%</div>
              <div>Tax amount: ${tax?.toFixed(2)}</div>
              <div><b>Total: ${total?.toFixed(2)}</b></div>
              <div style={{ color: "green", marginTop: 8 }}>✅ Order created successfully!</div>
            </div>
        )}
      </div>
  );
}