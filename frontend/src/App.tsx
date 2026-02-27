import { useState } from "react";
import "./App.css";
import Tabs from "./components/Tabs";
import OrdersView from "./components/OrdersView";
import CreateView from "./components/CreateView";
import ImportCsvView from "./components/ImportCsvView";

type Tab = "orders" | "create" | "import";

export default function App() {
  const [tab, setTab] = useState<Tab>("orders");

  return (
    <div className="container">
      <div className="shell">
        <div className="top">
          <div>
            <div className="titleRow">
              <h1 className="h1">BetterMe: Instant Wellness Kits</h1>
              <span className="badge">Admin (MVP)</span>
            </div>
            <p className="sub">Orders • Manual create • Import CSV</p>
          </div>
        </div>

        <div className="card" style={{ paddingBottom: 10 }}>
          <Tabs tab={tab} onChange={setTab} />
        </div>

        {tab === "orders" && <OrdersView />}
        {tab === "create" && <CreateView />}
        {tab === "import" && <ImportCsvView />}
      </div>
    </div>
  );
}