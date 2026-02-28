import React, { useMemo, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import "./App.css";
import AdminPage from "./pages/AdminPage";

function isAuthed() {
  return localStorage.getItem("auth") === "1";
}

function setAuthed(v: boolean) {
  localStorage.setItem("auth", v ? "1" : "0");
  if (!v) {
    localStorage.removeItem("authToken");
  }
}

function LoginPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const[pass, setPass] = useState("");
  const [error, setError] = useState<string | null>(null);

  const canSubmit = useMemo(() => email.trim() !== "" && pass.trim() !== "",[email, pass]);

  const onLogin = () => {
    if ((email === "admin" || email === "admin@example.com") && pass === "admin123") {
      setAuthed(true);
      localStorage.setItem("authToken", btoa("admin:admin123"));
      nav("/admin", { replace: true });
    } else {
      setError("Невірний логін або пароль. Доступ тільки для адміністратора.");
    }
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
                <span>Email or Login</span>
                <input
                    className="input"
                    value={email}
                    onChange={(e) => { setEmail(e.target.value); setError(null); }}
                    placeholder="admin"
                />
              </label>

              <label className="label">
                <span>Password</span>
                <input
                    className="input"
                    type="password"
                    value={pass}
                    onChange={(e) => { setPass(e.target.value); setError(null); }}
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

            {}
            {error && <div className="error" style={{ marginTop: "14px", borderTop: "none" }}>{error}</div>}
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