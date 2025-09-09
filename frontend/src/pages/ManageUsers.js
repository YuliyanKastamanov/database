// src/pages/ManageUsers.js
import { Link, useNavigate } from "react-router-dom";
import bg from "../assets/lufthansa_aircraft.jpg";

function ManageUsers({ user, onLogout }) {
  const navigate = useNavigate();
  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // същата карта като в Dashboard
  const cardBase = {
    background: "rgba(255,255,255,0.88)",
    borderRadius: 16,
    padding: 28,
    width: 260,
    height: 200,
    margin: 18,
    cursor: "pointer",
    textAlign: "center",
    boxShadow: "0 10px 26px rgba(0,0,0,0.24)",
    transition: "transform 160ms ease, box-shadow 160ms ease",
    color: "#0A1D3D",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    textDecoration: "none",
    fontFamily: "Helvetica, Arial, sans-serif",
    userSelect: "none",
    willChange: "transform, box-shadow",
  };

  const Card = ({ title, to, tooltip }) => (
    <Link
      to={to}
      title={tooltip}
      style={cardBase}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "translateY(-4px)";
        e.currentTarget.style.boxShadow = "0 14px 30px rgba(0,0,0,0.28)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "translateY(0)";
        e.currentTarget.style.boxShadow = "0 10px 26px rgba(0,0,0,0.24)";
      }}
    >
      <div style={{ fontSize: 20, fontWeight: 800, marginBottom: 8 }}>{title}</div>
      <div style={{ fontSize: 15, opacity: 0.85 }}>{tooltip}</div>
    </Link>
  );

  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        backgroundImage: `url(${bg})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        fontFamily: "Helvetica, Arial, sans-serif",
      }}
    >
      {/* Sidebar */}
      <aside
        style={{
          width: 240,
          background: "rgba(0,0,0,0.65)",
          color: "#fff",
          padding: 20,
          display: "flex",
          flexDirection: "column",
          boxShadow: "0 0 30px rgba(0,0,0,0.35)",
        }}
      >
        <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 18 }}>
          Menu
        </div>

        {/* винаги Dashboard */}
        <NavButton to="/dashboard">Dashboard</NavButton>

        {/* админ меню – същото както в Dashboard */}
        {isAdmin ? (
          <>
            <NavButton to="/manage-users">Manage Users</NavButton>
            <NavButton to="/manage-tasks">Manage Tasks</NavButton>
            <NavButton to="/generator">Generator</NavButton>
            <NavButton to="/reports">Reports</NavButton>
          </>
        ) : null}

        <button
          onClick={async () => {
            await onLogout?.();
            navigate("/login");
          }}
          style={{
            marginTop: "auto",
            background: "#0A1D3D",
            color: "#fff",
            border: "none",
            borderRadius: 10,
            padding: "10px 14px",
            cursor: "pointer",
            fontWeight: 700,
          }}
        >
          Logout
        </button>
      </aside>

      {/* Main */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        {/* Header */}
        <header
          style={{
            background: "rgba(0,0,0,0.65)",
            color: "#fff",
            padding: "16px 28px",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            boxShadow: "0 4px 20px rgba(0,0,0,0.35)",
          }}
        >
          <div>
            <div style={{ fontWeight: 800, fontSize: 22 }}>Manage Users</div>
            <div
              style={{
                marginTop: 6,
                display: "inline-block",
                padding: "4px 12px",
                borderRadius: 999,
                fontSize: 12,
                fontWeight: 800,
                color: "#fff",
                background: "rgba(255,255,255,0.18)",
              }}
            >
              {isAdmin ? "Master View" : "Project View"}
            </div>
          </div>
          <div style={{ fontWeight: 700 }}>👤 {displayName}</div>
        </header>

        {/* Cards */}
        <main
          style={{
            flex: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexWrap: "wrap",
            padding: 24,
            gap: 10,
          }}
        >
          <Card
            title="Register User"
            to="/manage-users/register"
            tooltip="Create a new application user"
          />
          <Card
            title="Update User Role"
            to="/manage-users/update-role"
            tooltip="Promote/Demote or change roles"
          />
          <Card
            title="Delete User"
            to="/manage-users/delete"
            tooltip="Remove a user from the system"
          />
        </main>
      </div>
    </div>
  );
}

// Навигационен бутон (същия стил)
function NavButton({ to, children }) {
  return (
    <Link
      to={to}
      style={{
        background: "transparent",
        border: "1px solid rgba(255,255,255,0.25)",
        padding: "10px 12px",
        marginBottom: 10,
        borderRadius: 10,
        color: "#fff",
        cursor: "pointer",
        textDecoration: "none",
        fontWeight: 700,
        display: "block",
        transition: "background 140ms ease, border-color 140ms ease",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.background = "rgba(255,255,255,0.15)";
        e.currentTarget.style.borderColor = "rgba(255,255,255,0.35)";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.background = "transparent";
        e.currentTarget.style.borderColor = "rgba(255,255,255,0.25)";
      }}
    >
      {children}
    </Link>
  );
}

export default ManageUsers;
