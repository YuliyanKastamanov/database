// src/App.js
import { useState, useEffect, useRef } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import TaskStatus from "./pages/TaskStatus";
import Generator from "./pages/Generator";
import Reports from "./pages/Reports";
import ManageTasks from "./pages/ManageTasks";


const LOGOUT_BCAST_KEY = "dbapp:logout";
const IDLE_MS = 20 * 60 * 1000; // 20m

function AppShell() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const idleTimerRef = useRef(null);
  const lastActivityRef = useRef(Date.now());

  // ---- helpers ----
  const clearIdleTimer = () => {
    if (idleTimerRef.current) {
      clearTimeout(idleTimerRef.current);
      idleTimerRef.current = null;
    }
  };

  const scheduleIdleTimer = () => {
    clearIdleTimer();
    idleTimerRef.current = setTimeout(() => {
      // Клиентски idle logout (без бекенд повикване)
      doLocalLogout(true);
    }, IDLE_MS);
  };

  const markActivity = () => {
    lastActivityRef.current = Date.now();
    scheduleIdleTimer();
  };

  const attachActivityListeners = () => {
    const events = ["mousemove", "keydown", "click", "scroll", "touchstart"];
    events.forEach((ev) => window.addEventListener(ev, markActivity, { passive: true }));
    return () => events.forEach((ev) => window.removeEventListener(ev, markActivity));
  };

  // Единен logout – използва се и от бутона, и от idle, и от други табове
  const doLocalLogout = (broadcast = false) => {
    setUser(null);
    navigate("/login");
    if (broadcast) {
      try {
        localStorage.setItem(LOGOUT_BCAST_KEY, String(Date.now()));
      } catch {}
    }
  };

  const logout = async () => {
    try {
      await fetch("http://localhost:8080/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch (err) {
      // дори и да фейлне заявката, продължаваме с локален logout
      console.error(err);
    } finally {
      doLocalLogout(true); // broadcast към другите табове
    }
  };

  // Зареждаме текущия user при стартиране
  useEffect(() => {
    let cancelled = false;

    const fetchMe = async () => {
      try {
        const res = await fetch("http://localhost:8080/auth/me", {
          credentials: "include",
        });
        if (!cancelled && res.ok) {
          const data = await res.json();
          setUser(data);
        } else if (!cancelled) {
          setUser(null);
        }
      } catch {
        if (!cancelled) setUser(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchMe();

    // Слушане на cross-tab logout
    const onStorage = (e) => {
      if (e.key === LOGOUT_BCAST_KEY) {
        doLocalLogout(false); // не ребродкастваме
      }
    };
    window.addEventListener("storage", onStorage);

    // Idle детекция
    const detach = attachActivityListeners();
    scheduleIdleTimer(); // стартираме таймера от старта

    // На фокус проверяваме статус (еднократно) – не пингваме периодично!
    const onVisibility = async () => {
      if (document.visibilityState !== "visible") return;
      try {
        const res = await fetch("http://localhost:8080/auth/me", { credentials: "include" });
        if (!res.ok) doLocalLogout(true);
      } catch {
        doLocalLogout(true);
      }
    };
    document.addEventListener("visibilitychange", onVisibility);

    return () => {
      cancelled = true;
      window.removeEventListener("storage", onStorage);
      document.removeEventListener("visibilitychange", onVisibility);
      detach();
      clearIdleTimer();
    };
  }, []); // eslint-disable-line

  if (loading) return <p>Loading...</p>;

  return (
    <Routes>
      <Route path="/login" element={<Login onLogin={setUser} />} />

      <Route
        path="/dashboard"
        element={user ? <Dashboard user={user} onLogout={logout} /> : <Navigate to="/login" />}
      />
      <Route
        path="/manage-tasks"
        element={
          user && user.roles?.includes("ADMIN")
            ? <ManageTasks user={user} onLogout={logout} />
            : <Navigate to="/dashboard" />
        }
      />

      <Route
        path="/task-status"
        element={user ? <TaskStatus user={user} onLogout={logout} /> : <Navigate to="/login" />}
      />

      <Route
        path="/generator"
        element={user ? <Generator user={user} /> : <Navigate to="/login" />}
      />

      <Route
        path="/reports"
        element={
          user && user.roles?.includes("ADMIN") ? <Reports user={user} /> : <Navigate to="/dashboard" />
        }
      />

      <Route path="*" element={<Navigate to={user ? "/dashboard" : "/login"} />} />
    </Routes>
  );
}

export default function App() {
  return (
    <Router>
      <AppShell />
    </Router>
  );
}
