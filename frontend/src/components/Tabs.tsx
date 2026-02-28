type TabKey = "orders" | "create" | "import";

type TabsProps = {
    value: TabKey;
    onChange: (value: TabKey) => void;
};

export default function Tabs({ value, onChange }: TabsProps) {
    return (
        <div className="tabs">
            <button
                type="button"
                className={`tabBtn ${value === "orders" ? "active" : ""}`}
                onClick={() => onChange("orders")}
            >
                Orders
            </button>

            <button
                type="button"
                className={`tabBtn ${value === "create" ? "active" : ""}`}
                onClick={() => onChange("create")}
            >
                Create
            </button>

            <button
                type="button"
                className={`tabBtn ${value === "import" ? "active" : ""}`}
                onClick={() => onChange("import")}
            >
                Import CSV
            </button>
        </div>
    );
}