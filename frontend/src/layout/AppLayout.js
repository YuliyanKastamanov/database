import { Link, NavLink } from "react-router-dom";
import "./AppLayout.css";
import bg from "../assets/lufthansa_aircraft.jpg"; // същото изображение

export default function AppLayout({ user, onLogout, children, useCreamBg = false }) {
  const displayName = user?.name || user?.uNumber || "User";

  return (
    <div className={`app-shell ${useCreamBg ? "cream" : ""}`} style={!useCreamBg ? {
      backgroundImage: `url(${bg})`,
      backgroundSize: "cover",
      backgroundPosition: "center",
      backgroundRepeat: "no-repeat"
    } : undefined}>
      <header className="app-header">
        <div className="brand">
          <Link to="/">Jobcard Database App</Link>
        </div>
        <div className="header-right">
          <span className="user-name">{displayName}</span>
          <button className="logout-btn" onClick={onLogout}>Logout</button>
        </div>
      </header>

      <aside className="app-sidebar">
        {/* Общи бутони */}
        <NavLink to="/user/status" className="nav-item">Status</NavLink>
        <NavLink to="/user/tasks" className="nav-item">My Tasks</NavLink>
        <NavLink to="/user/reports" className="nav-item">Reports</NavLink>

        {/* Админски бутони (ако има роля) */}
        {user?.roles?.includes("ADMIN") && (
          <>
            <div className="nav-sep">Admin</div>
            <NavLink to="/admin" className="nav-item">Admin Panel</NavLink>
            <NavLink to="/admin/tasks" className="nav-item">All Tasks</NavLink>
            <NavLink to="/admin/users" className="nav-item">Users</NavLink>
            <NavLink to="/admin/revisions" className="nav-item">Revisions</NavLink>
            <NavLink to="/admin/generator" className="nav-item">Generator</NavLink>
          </>
        )}
      </aside>

      <main className="app-content">
        {children}
      </main>
    </div>
  );
}
