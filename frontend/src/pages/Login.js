import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";
import bg from "../assets/lufthansa_aircraft.jpg";
import logo from "../assets/lufthansa_logo.png"; // <-- добави този импорт

function Login({ onLogin }) {
  const navigate = useNavigate();

  const [uNumber, setUNumber] = useState("");
  const [password, setPassword] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const validate = () => {
    const errors = {};
    const u = uNumber.trim();
    const p = password.trim();

    if (!u) {
      errors.uNumber = "Please enter your U-Number.";
    } else if (!/^U\d{6}$/i.test(u)) {
      errors.uNumber = "U-Number must be 'U' followed by 6 digits (e.g., U123456).";
    }
    if (!p) {
      errors.password = "Please enter your password.";
    } else if (p.length < 5 || p.length > 20) {
      errors.password = "Password must be between 5 and 20 characters.";
    }
    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errors = validate();
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors);
      return;
    }

    setLoading(true);
    setFieldErrors({});

    try {
      const payload = {
        uNumber: uNumber.trim().toUpperCase(),
        password: password.trim(),
      };

      const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const data = await res.json().catch(() => ({}));

      if (!res.ok) {
        const be = {};
        if (data.uNumber) be.uNumber = String(data.uNumber);
        if (data.password) be.password = String(data.password);
        if (data.message) be.general = String(data.message);
        if (Object.keys(be).length === 0) {
          be.general = "Login failed. Please check your credentials and try again.";
        }
        setFieldErrors(be);
        return;
      }

      onLogin(data);            // съхрани user-a в App
      navigate("/dashboard");   // и пренасочи към dashboard
    } catch (err) {
      setFieldErrors({ general: err.message || "Unexpected error. Please try again." });
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
        backgroundSize: "cover",
        backgroundRepeat: "no-repeat",
        backgroundPosition: "center",
        minHeight: "100vh",
        width: "100vw",
      }}
    >
      <div className="overlay" />
      <div className="container glass">
        <img
          src={logo} // увери се, че е във frontend/public/
          alt="Company Logo"
          width="120"
          className="logo"
        />
        <h2 className="title">Jobcard Database App</h2>

        <form onSubmit={handleSubmit} className="form" noValidate>
          <label className="label">U-Number</label>
          <input
            type="text"
            placeholder="U-Number (e.g., U123456)"
            value={uNumber}
            onChange={(e) => setUNumber(e.target.value)}
            className={inputClass("uNumber", uNumber)}
            aria-invalid={!!fieldErrors.uNumber}
            aria-describedby={fieldErrors.uNumber ? "uNumber-error" : undefined}
          />
          {fieldErrors.uNumber && (
            <p id="uNumber-error" className="error" role="alert">
              {fieldErrors.uNumber}
            </p>
          )}

          <label className="label">Password</label>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={inputClass("password", password)}
            aria-invalid={!!fieldErrors.password}
            aria-describedby={fieldErrors.password ? "password-error" : undefined}
          />
          {fieldErrors.password && (
            <p id="password-error" className="error" role="alert">
              {fieldErrors.password}
            </p>
          )}

          <button type="submit" disabled={loading} className="btn">
            {loading ? "Logging in..." : "Login"}
          </button>

          {fieldErrors.general && (
            <p className="error general" role="alert" style={{ marginTop: 10 }}>
              {fieldErrors.general}
            </p>
          )}
        </form>
      </div>
    </div>
  );
}

export default Login;
