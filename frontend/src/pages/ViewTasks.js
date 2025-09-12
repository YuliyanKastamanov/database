// src/pages/ViewTasks.js
import { useEffect, useMemo, useState, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import bg from "../assets/lufthansa_aircraft.jpg";

function ViewTasks({ user, onLogout }) {
  const navigate = useNavigate();

  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [loading, setLoading] = useState(false);
  const [infoMsg, setInfoMsg] = useState(""); // съобщение при липса на dbRevision

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // ---- helpers ----
  const refreshTaskTypes = useCallback(async () => {
    try {
      const res = await fetch("http://localhost:8080/task-types", { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        setTaskTypes(data);
      } else {
        setTaskTypes([]);
      }
    } catch (err) {
      console.error(err);
      setTaskTypes([]);
    }
  }, []);

  useEffect(() => {
    refreshTaskTypes();
  }, [refreshTaskTypes]);

  const selectedTypeObj = useMemo(
    () => taskTypes.find((t) => t.type === selectedType),
    [taskTypes, selectedType]
  );

  // Ако за избрания тип няма dbRevision → няма таскове
  useEffect(() => {
    if (!selectedType) {
      setInfoMsg(""); // при "All" не показваме съобщение
      return;
    }
    if (selectedTypeObj && !selectedTypeObj.dbRevision) {
      setInfoMsg("No tasks exist for this aircraft type/client yet.");
    } else {
      setInfoMsg("");
    }
  }, [selectedType, selectedTypeObj]);

  const exportExcel = async () => {
    setLoading(true);
    try {
      const url = selectedType
        ? `http://localhost:8080/tasks?type=${encodeURIComponent(selectedType)}`
        : "http://localhost:8080/tasks";

      const res = await fetch(url, { credentials: "include" });
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Failed to fetch tasks");
      }
      const data = await res.json();
      const rows = Array.isArray(data) ? data : [];

      // Подреждаме колоните както при TaskStatus/ReturnTaskDTO
      const worksheetData = rows.map((r) => ({
        "Task Number": r.taskNumber ?? "",
        Revision: r.revision ?? "",
        "SOC Status": r.socStatus ?? "",
        "SOC Description": r.socDescription ?? "",
        Comment: r.comment ?? "",
        "JCE Name": r.jceName ?? "",
        "Coversheet SAP": r.coversheetSap ?? "",
        "Coversheet Status": r.coversheetStatus ?? "",
        "Created MJob": r.createdMJob ?? "",
        "MJob Status": r.statusMJob ?? "",
        CRI: r.cri ?? "",
        "Last Update": r.lastUpdate ?? "",
        "Has History": r.hasHistory ?? "",
        "Current Update": r.currentUpdate ?? "",
        "SB Reference": r.sbReference ?? "",
        ID: r.id ?? "",
        Exists: r.exists ? "Yes" : "No",
        "Status Info": r.statusInfo ?? "",
      }));

      const ws = XLSX.utils.json_to_sheet(worksheetData);
      // Лека ширина на колоните
      const colCount = worksheetData.length ? Object.keys(worksheetData[0]).length : 17;
      ws["!cols"] = Array.from({ length: colCount }, () => ({ wch: 18 }));

      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, selectedType || "All Tasks");

      const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
      const blob = new Blob([excelBuffer], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      const today = new Date().toISOString().split("T")[0];
      const name = selectedType ? `tasks-${selectedType}-${today}.xlsx` : `tasks-all-${today}.xlsx`;
      saveAs(blob, name);
    } catch (err) {
      console.error(err);
      alert("Failed to export tasks. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // ---- Layout / Styles ----
  const SHELL = {
    display: "flex",
    minHeight: "100vh",
    backgroundImage: `url(${bg})`,
    backgroundSize: "cover",
    backgroundPosition: "center",
    fontFamily: "Helvetica, Arial, sans-serif",
  };
  const SIDEBAR = {
    width: 240,
    background: "rgba(0,0,0,0.65)",
    color: "#fff",
    padding: 20,
    display: "flex",
    flexDirection: "column",
    boxShadow: "0 0 30px rgba(0,0,0,0.35)",
  };
  const HEADER = {
    background: "rgba(0,0,0,0.65)",
    color: "#fff",
    padding: "16px 28px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    boxShadow: "0 4px 20px rgba(0,0,0,0.35)",
  };
  const CONTENT_WRAP = { flex: 1, display: "flex", flexDirection: "column" };
  const INNER = {
    flex: 1,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    padding: 10,
    gap: 8,
    overflow: "hidden",
  };

  const cardWidth = "min(1400px, 96vw)";
  const CARD = {
    background: "rgba(255,255,255,0.88)",
    borderRadius: 16,
    padding: 18,
    width: cardWidth,
    height: "calc((99vh - 80px - 24px) / 2)",
    boxShadow: "0 10px 26px rgba(0,0,0,0.24)",
    color: "#0A1D3D",
    display: "flex",
    flexDirection: "column",
  };
  const INPUT = {
    width: "100%",
    padding: "10px 12px",
    borderRadius: 10,
    border: "1px solid rgba(10,29,61,0.25)",
    fontSize: 14,
    outline: "none",
    background: "#fff",
    color: "#0A1D3D",
  };
  const LABEL = { fontWeight: 800, marginBottom: 6 };

  // Banner вдясно
  const INFO_BANNER = {
    flex: "1 1 auto",
    minWidth: 240,
    marginLeft: "auto",
    alignSelf: "flex-start",
    padding: "10px 12px",
    borderRadius: 10,
    border: "1px solid rgba(176,0,32,0.25)",
    background: "#FDECEA",
    color: "#932338",
    fontWeight: 700,
  };

  const canDownload = !selectedType || !!selectedTypeObj?.dbRevision;

  return (
    <div style={SHELL}>
      {/* Sidebar */}
      <aside style={SIDEBAR}>
        <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 18 }}>Menu</div>
        {/* Винаги Dashboard */}
        <NavButton to="/dashboard">Dashboard</NavButton>

        {isAdmin ? (
          <>
            <NavButton to="/manage-users">Manage Users</NavButton>
            <NavButton to="/manage-tasks">Manage Tasks</NavButton>
            <NavButton to="/generator">Generator</NavButton>
            <NavButton to="/reports">Reports</NavButton>
          </>
        ) : (
          <>
            <NavButton to="/task-status">Task Status</NavButton>
            <NavButton to="/generator">Generator</NavButton>
          </>
        )}

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

      <div style={CONTENT_WRAP}>
        {/* Header */}
        <header style={HEADER}>
          <div>
            <div style={{ fontWeight: 800, fontSize: 22 }}>Jobcard Database App</div>
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

        {/* Single Card */}
        <main style={INNER}>
          <div style={CARD}>
            <h2 style={{ margin: 0, marginBottom: 10 }}>View Tasks</h2>

            {/* Ред: избор на тип + банер вдясно */}
            <div style={{ display: "flex", gap: 16, marginBottom: 12, alignItems: "flex-end", flexWrap: "wrap" }}>
              <div style={{ flex: "0 1 320px", minWidth: 220 }}>
                <div style={LABEL}>Aircraft / Task Type</div>
                <select
                  style={INPUT}
                  value={selectedType}
                  onChange={(e) => setSelectedType(e.target.value)}
                >
                  <option value="">All (no filter)</option>
                  {taskTypes.map((t) => (
                    <option key={t.id ?? t.type} value={t.type}>
                      {t.type}
                    </option>
                  ))}
                </select>
              </div>

              {infoMsg && (
                <div style={INFO_BANNER}>
                  {infoMsg}
                </div>
              )}
            </div>

            {/* Бутон за експорт — долу вдясно */}
            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: "auto" }}>
              <button
                type="button"
                onClick={exportExcel}
                disabled={!canDownload || loading}
                style={{
                  background: canDownload ? "#0A1D3D" : "rgba(10,29,61,0.35)",
                  color: "#fff",
                  border: "none",
                  borderRadius: 10,
                  padding: "10px 16px",
                  cursor: canDownload && !loading ? "pointer" : "not-allowed",
                  fontWeight: 700,
                  opacity: loading ? 0.8 : 1,
                }}
              >
                {loading ? "Preparing..." : selectedType ? "Download Selected Type" : "Download All"}
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}

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

export default ViewTasks;
