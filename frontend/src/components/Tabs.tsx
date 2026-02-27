type Tab = "orders" | "create" | "import";

export default function Tabs({
  tab,
  onChange,
}: {
  tab: Tab;
  onChange: (t: Tab) => void;
}) {
  return (
    <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
      <button className="btn" onClick={() => onChange("orders")} disabled={tab === "orders"}>
        Orders
      </button>
      <button className="btn" onClick={() => onChange("create")} disabled={tab === "create"}>
        Create
      </button>
      <button className="btn" onClick={() => onChange("import")} disabled={tab === "import"}>
        Import CSV
      </button>
    </div>
  );
}