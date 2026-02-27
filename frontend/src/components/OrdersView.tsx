import { useEffect, useMemo, useState } from "react";
import type { Order } from "../types/order";
import { getOrders } from "../lib/api";

function money(n: number) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(n);
}

export default function OrdersView() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [search, setSearch] = useState("");
  const [minSubtotal, setMinSubtotal] = useState("");
  const [maxSubtotal, setMaxSubtotal] = useState("");

  const [page, setPage] = useState(1);
  const pageSize = 10;

  useEffect(() => {
    setLoading(true);
    setError(null);
    getOrders()
      .then((data) => {
        setOrders(data);
        setLoading(false);
      })
      .catch((e) => {
        console.error(e);
        setError("Не вдалося завантажити /api/orders.");
        setLoading(false);
      });
  }, []);

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    const min = minSubtotal.trim() === "" ? null : Number(minSubtotal);
    const max = maxSubtotal.trim() === "" ? null : Number(maxSubtotal);

    return orders.filter((o) => {
      const matches =
        q === "" ||
        String(o.id).includes(q) ||
        String(o.latitude).includes(q) ||
        String(o.longitude).includes(q);

      const okMin = min === null || (!Number.isNaN(min) && o.subtotal >= min);
      const okMax = max === null || (!Number.isNaN(max) && o.subtotal <= max);

      return matches && okMin && okMax;
    });
  }, [orders, search, minSubtotal, maxSubtotal]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const safePage = Math.min(Math.max(page, 1), totalPages);

  const pageData = useMemo(() => {
    const start = (safePage - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, safePage]);

  useEffect(() => setPage(1), [search, minSubtotal, maxSubtotal]);

  const resetFilters = () => {
    setSearch("");
    setMinSubtotal("");
    setMaxSubtotal("");
  };

  return (
    <div className="card">
      <div className="filters">
        <label className="label">
          <span>Search (id / lat / lon)</span>
          <input className="input" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Напр. 1 або 40.7" />
        </label>

        <label className="label">
          <span>Min subtotal</span>
          <input className="input mono" value={minSubtotal} onChange={(e) => setMinSubtotal(e.target.value)} type="number" min="0" step="0.01" placeholder="0" />
        </label>

        <label className="label">
          <span>Max subtotal</span>
          <input className="input mono" value={maxSubtotal} onChange={(e) => setMaxSubtotal(e.target.value)} type="number" min="0" step="0.01" placeholder="500" />
        </label>

        <button className="btn" onClick={resetFilters}>Reset</button>
      </div>

      {loading && <p className="sub">Loading…</p>}
      {!loading && error && <div className="error">{error}</div>}

      {!loading && !error && (
        <>
          <div className="tableWrap">
            <table className="table">
              <thead>
                <tr>
                  <th>ID</th><th>Lat</th><th>Lon</th><th>Subtotal</th><th>Tax rate</th><th>Tax</th><th>Total</th>
                </tr>
              </thead>
              <tbody>
                {pageData.length === 0 ? (
                  <tr><td colSpan={7}>Немає даних за фільтрами.</td></tr>
                ) : (
                  pageData.map((o) => (
                    <tr key={o.id}>
                      <td className="mono"><b>{o.id}</b></td>
                      <td className="mono">{o.latitude}</td>
                      <td className="mono">{o.longitude}</td>
                      <td className="mono">{money(o.subtotal)}</td>
                      <td className="mono">{(o.composite_tax_rate * 100).toFixed(3)}%</td>
                      <td className="mono">{money(o.tax_amount)}</td>
                      <td className="mono"><b>{money(o.total_amount)}</b></td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          <div className="footerRow">
            <span>Page {safePage} / {totalPages} • {filtered.length} orders</span>
            <div className="pager">
              <button className="btn" onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={safePage === 1}>Prev</button>
              <button className="btn" onClick={() => setPage((p) => Math.min(totalPages, p + 1))} disabled={safePage === totalPages}>Next</button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}