import React, { useMemo, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import "./App.css";
import AdminPage from "./pages/AdminPage";

function isAuthed() {
  return localStorage.getItem("auth") === "1";
}

function setAuthed(v: boolean) {
  localStorage.setItem("auth", v ? "1" : "0");
}

function LoginPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [pass, setPass] = useState("");

  const canSubmit = useMemo(() => email.trim() !== "" && pass.trim() !== "", [email, pass]);

  const onLogin = () => {
    // пускаем любого, кто ввёл email+password
    setAuthed(true);
    nav("/admin", { replace: true });
  };

  const onRegister = () => {
    nav("/in-progress", { replace: true });
  };

  return (
    <div className="container">
      <div className="shell">
        <div className="top">
          <div>
            <div className="titleRow">
              <h1 className="h1">BetterMe: Instant Wellness Kits</h1>
              <span className="badge">Login</span>
            </div>
            <p className="sub">Login to continue</p>
          </div>
        </div>

        <div className="card">
          <div className="filters" style={{ gridTemplateColumns: "1fr 1fr auto auto", borderBottom: "none" }}>
            <label className="label">
              <span>Email</span>
              <input
                className="input"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@example.com"
              />
            </label>

            <label className="label">
              <span>Password</span>
              <input
                className="input"
                type="password"
                value={pass}
                onChange={(e) => setPass(e.target.value)}
                placeholder="••••••••"
              />
            </label>

            <button className="btn" type="button" onClick={onLogin} disabled={!canSubmit}>
              Login
            </button>

            <button className="btn" type="button" onClick={onRegister}>
              Register
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function InProgressPage() {
  const nav = useNavigate();

  return (
    <div className="container">
      <div className="shell">
        <div className="top">
          <div>
            <div className="titleRow">
              <h1 className="h1">В розробці</h1>
              <span className="badge">Coming soon</span>
            </div>
            <p className="sub">Registration буде додана пізніше</p>
          </div>
        </div>

        <div className="card">
          <p style={{ marginTop: 0, color: "var(--muted)" }}>Поки що доступна тільки адмін-частина</p>

          <button className="btn" onClick={() => nav("/login", { replace: true })}>
            Back to login
          </button>
        </div>
      </div>
    </div>
  );
}

function RequireAuth({ children }: { children: React.ReactElement }) {
  if (!isAuthed()) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route path="/login" element={<LoginPage />} />
      <Route path="/in-progress" element={<InProgressPage />} />

      <Route
        path="/admin"
        element={
          <RequireAuth>
            <AdminPage />
          </RequireAuth>
        }
      />

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}