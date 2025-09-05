import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import TaskStatus from "./pages/TaskStatus";
import Generator from "./pages/Generator";
import Reports from "./pages/Reports";

function App() {
  const [user, setUser] = useState(null);

  const logout = async () => {
    try {
      const res = await fetch("http://localhost:8080/auth/logout", {
        method: "POST",            // ако бекендът е на POST
        credentials: "include",
      });
      if (!res.ok) throw new Error("Logout failed");
      setUser(null);
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <Router>
      <Routes>
        {/* / -> към /login или /dashboard според това дали има user */}
        <Route
          path="/"
          element={<Navigate to={user ? "/dashboard" : "/login"} replace />}
        />

        {/* Login: ако вече има user, не показвай Login, а пренасочи към /dashboard */}
        <Route
          path="/login"
          element={
            user ? <Navigate to="/dashboard" replace /> : <Login onLogin={setUser} />
          }
        />

        <Route
          path="/dashboard"
          element={user ? <Dashboard user={user} onLogout={logout} /> : <Navigate to="/login" replace />}
        />

        <Route
          path="/task-status"
          element={user ? <TaskStatus user={user} /> : <Navigate to="/login" replace />}
        />

        <Route
          path="/generator"
          element={user ? <Generator user={user} /> : <Navigate to="/login" replace />}
        />

        <Route
          path="/reports"
          element={
            user && user.roles?.includes("ADMIN") ? (
              <Reports user={user} />
            ) : (
              <Navigate to="/dashboard" replace />
            )
          }
        />

        {/* хваща всичко друго */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
