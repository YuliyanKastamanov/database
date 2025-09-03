import { useState } from "react";
import "./Login.css";
import bg from "./assets/lufthansa_aircraft.jpg";

function Login({ onLogin }) {
  const [uNumber, setUNumber] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setFieldErrors({});

    try {
      const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ uNumber, password }),
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        const errors = {};
        if (data.uNumber) errors.uNumber = data.uNumber;
        if (data.password) errors.password = data.password;
        if (data.message) errors.general = data.message;
        setFieldErrors(errors);
        return;
      }

      onLogin(data);
    } catch (err) {
      setFieldErrors({ general: err.message });
    } finally {
      setLoading(false);
    }
  };

  const inputClass = (name, val) =>
    `login-input ${fieldErrors[name] ? "is-invalid" : val ? "is-valid" : ""}`;

  return (
    <div
      className="login-page"
      style={{
        backgroundImage: `url(${bg})`,
        backgroundSize: "cover",       // разпъва по ширина/височина
        backgroundRepeat: "no-repeat", // маха повтаряне
        backgroundPosition: "center",  // центрира
        minHeight: "100vh",            // цялата височина
        width: "100vw",                // цялата ширина
      }}
    >
      <div className="overlay" />
      <div className="container glass">
        <img
          src="/lufthansa_logo.png" // от public/
          alt="Company Logo"
          width="120"
          className="logo"
        />
        <h2 className="title">Jobcard Database App</h2>

        <form onSubmit={handleSubmit} className="form">
          <label className="label">U-Number</label>
          <input
            type="text"
            placeholder="Enter your U-Number"
            value={uNumber}
            onChange={(e) => setUNumber(e.target.value)}
            className={inputClass("uNumber", uNumber)}
            required
          />
          {fieldErrors.uNumber && (
            <p className="error">{fieldErrors.uNumber}</p>
          )}

          <label className="label">Password</label>
          <input
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={inputClass("password", password)}
            required
          />
          {fieldErrors.password && (
            <p className="error">{fieldErrors.password}</p>
          )}

          <button type="submit" disabled={loading} className="btn">
            {loading ? "Logging in..." : "Login"}
          </button>

          {fieldErrors.general && (
            <p className="error general">{fieldErrors.general}</p>
          )}
        </form>
      </div>
    </div>
  );
}

export default Login;
