import { useState } from "react";

import Tabs from "../components/Tabs";
import OrdersView from "../components/OrdersView";
import CreateView from "../components/CreateView";
import ImportCsvView from "../components/ImportCsvView";

type TabKey = "orders" | "create" | "import";

export default function AdminPage() {
    const [tab, setTab] = useState<TabKey>("orders");

    return (
        <div className="container">
            <div className="shell">
                <div className="top">
                    <div>
                        <div className="titleRow">
                            <h1 className="h1">BetterMe: Instant Wellness Kits</h1>
                            <span className="badge">Admin</span>
                        </div>
                        <p className="sub">Orders • Manual create • Import CSV</p>
                    </div>
                </div>

                <div className="card">
                    <Tabs value={tab} onChange={setTab} />

                    <div style={{ marginTop: 12 }}>
                        {tab === "orders" && <OrdersView />}
                        {tab === "create" && <CreateView />}
                        {tab === "import" && <ImportCsvView />}
                    </div>
                </div>
            </div>
        </div>
    );
}