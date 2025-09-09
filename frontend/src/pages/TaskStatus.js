// src/pages/TaskStatus.js
import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import bg from "../assets/lufthansa_aircraft.jpg";

function TaskStatus({ user, onLogout }) {
  const navigate = useNavigate();

  const [taskNumbers, setTaskNumbers] = useState("");
  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [projectType, setProjectType] = useState("COVERSHEET");
  const [revision, setRevision] = useState("");
  const [availableRevisions, setAvailableRevisions] = useState([]);
  const [result, setResult] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasQueried, setHasQueried] = useState(false);
  const [infoMsg, setInfoMsg] = useState(""); // съобщения в долната плочка
  const [copyMsg, setCopyMsg] = useState(""); // toast за копиране

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // Колони за таблицата (всички; експортът винаги включва всички)
  const allColumns = useMemo(
    () => [
      { key: "taskNumber", label: "Task Number" },
      { key: "revision", label: "Revision" },
      { key: "statusInfo", label: "Status Info" },
      { key: "socStatus", label: "SOC Status" },
      { key: "socDescription", label: "SOC Description" },
      { key: "comment", label: "Comment" },
      { key: "jceName", label: "JCE Name" },
      { key: "coversheetSap", label: "Coversheet SAP" },
      { key: "coversheetStatus", label: "Coversheet Status" },
      { key: "createdMJob", label: "Created MJob" },
      { key: "statusMJob", label: "MJob Status" },
      { key: "cri", label: "CRI" },
      { key: "lastUpdate", label: "Last Update" },
      { key: "hasHistory", label: "Has History" },
      { key: "currentUpdate", label: "Current Update" },
      { key: "sbReference", label: "SB Reference" },
      { key: "exists", label: "Exists" },
    ],
    []
  );

  // Видимост на колоните (по подразбиране: всички)
  const [visibleCols, setVisibleCols] = useState(() =>
    Object.fromEntries(allColumns.map((c) => [c.key, true]))
  );
  useEffect(() => {
    setVisibleCols((prev) => {
      const merged = { ...prev };
      allColumns.forEach((c) => {
        if (merged[c.key] === undefined) merged[c.key] = true;
      });
      return merged;
    });
  }, [allColumns]);
  const allSelected = allColumns.every((c) => !!visibleCols[c.key]);
  const toggleSelectAll = () => {
    const next = !allSelected;
    setVisibleCols(Object.fromEntries(allColumns.map((c) => [c.key, next])));
  };

  // Task types от бекенда
  useEffect(() => {
    fetch("http://localhost:8080/task-types", { credentials: "include" })
      .then((res) => res.json())
      .then((data) => setTaskTypes(data))
      .catch((err) => console.error(err));
  }, []);

  const selectedTypeObj = useMemo(
    () => taskTypes.find((t) => t.type === selectedType),
    [taskTypes, selectedType]
  );

  // При смяна на типа – пресмятаме ревизиите и чистим съобщението/резултата
  useEffect(() => {
    setInfoMsg("");
    setHasQueried(false); // долната карта ще се покаже чак след Check

    if (!selectedTypeObj) {
      setAvailableRevisions([]);
      setRevision("");
      return;
    }

    // Списък от последна + стари ревизии (ако са подадени)
    const revs = [
      selectedTypeObj.dbRevision,
      selectedTypeObj.oldRevision1,
      selectedTypeObj.oldRevision2,
      selectedTypeObj.oldRevision3,
    ].filter(Boolean);

    setAvailableRevisions(revs);
    setRevision(selectedTypeObj.dbRevision || ""); // по дефолт последна; ако е null → ""
  }, [selectedTypeObj]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Ако за този тип няма последна ревизия => няма данни в БД → не пращаме заявка
    if (!selectedTypeObj || !selectedTypeObj.dbRevision) {
      setResult([]);
      setHasQueried(true);
      setInfoMsg("No data available for this task type yet.");
      setLoading(false);
      return;
    }

    const payload = {
      taskNumbers: taskNumbers.split("\n").map((t) => t.trim()).filter(Boolean),
      taskType: selectedType,
      revision: String(revision),
      projectType,
    };

    try {
      const res = await fetch("http://localhost:8080/tasks/status", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });
      const data = await res.json();
      setResult(Array.isArray(data) ? data : []);
      setInfoMsg(""); // изчистваме евентуално старо съобщение
      setHasQueried(true);
    } catch (err) {
      console.error(err);
      setResult([]);
      setInfoMsg("Something went wrong. Please try again.");
      setHasQueried(true);
    } finally {
      setLoading(false);
    }
  };

  const exportExcel = () => {
    if (!Array.isArray(result) || result.length === 0) return;
    const worksheetData = result.map((r) => {
      const row = {};
      allColumns.forEach(({ key, label }) => {
        let val = r?.[key];
        if (key === "exists") val = r?.exists ? "Yes" : "No";
        if (key === "lastUpdate" && r?.lastUpdate) {
          try {
            val = new Date(r.lastUpdate).toLocaleDateString();
          } catch {}
        }
        row[label] = val ?? "";
      });
      return row;
    });
    const ws = XLSX.utils.json_to_sheet(worksheetData);
    ws["!cols"] = allColumns.map(() => ({ wch: 16 }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Task Status");
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const today = new Date().toISOString().split("T")[0];
    saveAs(blob, `task-status-${today}.xlsx`);
  };

  // NEW: Copy to Clipboard (копира видимите колони, с хедър)
  const copyToClipboard = () => {
    if (!Array.isArray(result) || result.length === 0) return;

    const cols = allColumns.filter((c) => visibleCols[c.key]);
    const header = cols.map((c) => c.label).join("\t");

    const rows = result.map((r) =>
      cols
        .map((c) => {
          let val = r?.[c.key];
          if (c.key === "exists") val = r?.exists ? "Yes" : "No";
          if (c.key === "lastUpdate" && r?.lastUpdate) {
            try {
              val = new Date(r.lastUpdate).toLocaleDateString();
            } catch {}
          }
          return (val ?? "").toString().replace(/\r?\n/g, " ");
        })
        .join("\t")
    );

    const text = [header, ...rows].join("\n");
    navigator.clipboard.writeText(text).then(() => {
      setCopyMsg("✔ Results copied to clipboard");
      setTimeout(() => setCopyMsg(""), 3000);
    });
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
    backdropFilter: "blur(6px)",
    WebkitBackdropFilter: "blur(6px)",
  };
  const HEADER = {
    background: "rgba(0,0,0,0.65)",
    color: "#fff",
    padding: "16px 28px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    boxShadow: "0 4px 20px rgba(0,0,0,0.35)",
    backdropFilter: "blur(6px)",
    WebkitBackdropFilter: "blur(6px)",
  };
  const CONTENT_WRAP = { flex: 1, display: "flex", flexDirection: "column" };

  // Намалено разстояние към картите
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
  const cardHeight = "calc((99vh - 80px - 24px) / 2)";

  const CARD = {
    background: "rgba(255,255,255,0.88)",
    borderRadius: 16,
    padding: 18,
    width: cardWidth,
    height: cardHeight,
    boxShadow: "0 10px 26px rgba(0,0,0,0.24)",
    backdropFilter: "blur(8px)",
    WebkitBackdropFilter: "blur(8px)",
    color: "#0A1D3D",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
  };

  const ROW = { display: "flex", gap: 16, flexWrap: "wrap", marginBottom: 12 };
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
  const td = {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid rgba(10,29,61,0.08)",
    verticalAlign: "top",
    whiteSpace: "nowrap",
  };

  return (
    <div style={SHELL}>
      {/* Sidebar */}
      <aside style={SIDEBAR}>
        <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 18 }}>Menu</div>

        {/* Винаги показваме Dashboard */}
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

        {/* Две плочки */}
        <main style={INNER}>
          {/* ГОРНА: форма */}
          <div style={CARD}>
            <h2 style={{ margin: 0, marginBottom: 10 }}>Check Task Status</h2>

            <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", minHeight: 0 }}>
              {/* ред 1 */}
              <div style={ROW}>
                <div style={{ flex: "1 1 260px", minWidth: 220 }}>
                  <div style={LABEL}>Task Type</div>
                  <select
                    style={INPUT}
                    value={selectedType}
                    onChange={(e) => setSelectedType(e.target.value)}
                    required
                  >
                    <option value="">-- Select --</option>
                    {taskTypes.map((t) => (
                      <option key={t.id ?? t.type} value={t.type}>
                        {t.type}
                      </option>
                    ))}
                  </select>
                </div>

                <div style={{ flex: "1 1 220px", minWidth: 200 }}>
                  <div style={LABEL}>Project Type</div>
                  <select
                    style={INPUT}
                    value={projectType}
                    onChange={(e) => setProjectType(e.target.value)}
                  >
                    <option value="COVERSHEET">COVERSHEET</option>
                    <option value="AGENCY">AGENCY</option>
                  </select>
                </div>

                <div style={{ flex: "1 1 160px", minWidth: 140 }}>
                  <div style={LABEL}>Revision</div>
                  <select
                    style={INPUT}
                    value={revision}
                    onChange={(e) => setRevision(e.target.value)}
                    required
                    disabled={!availableRevisions.length}
                  >
                    {availableRevisions.length === 0 ? (
                      <option value="">-- No revisions --</option>
                    ) : (
                      availableRevisions.map((r, idx) => (
                        <option key={idx} value={r}>
                          {r}
                        </option>
                      ))
                    )}
                  </select>

                  {!!selectedTypeObj && (
                    <div style={{ marginTop: 6, fontSize: 12, fontWeight: 700, opacity: 0.85 }}>
                      {!selectedTypeObj.dbRevision ? (
                        <span style={{ color: "#b00020" }}>
                          No current DB revision available for this task type.
                        </span>
                      ) : revision === selectedTypeObj.dbRevision ? (
                        <>
                          Current DB revision:{" "}
                          <span style={{ fontWeight: 900 }}>{String(selectedTypeObj.dbRevision)}</span>
                        </>
                      ) : (
                        <>
                          You selected an older revision:{" "}
                          <span style={{ fontWeight: 900 }}>{String(revision)}</span>
                        </>
                      )}
                    </div>
                  )}
                </div>
              </div>

              {/* ред 2: textarea (NO resize) */}
              <div style={{ minHeight: 0, display: "flex", flexDirection: "column" }}>
                <div style={LABEL}>Task Numbers (one per line)</div>
                <div style={{ flex: 1, minHeight: 100, overflow: "auto" }}>
                  <textarea
                    rows={6}
                    style={{
                      ...INPUT,
                      resize: "none",
                      width: "100%",
                    }}
                    value={taskNumbers}
                    onChange={(e) => setTaskNumbers(e.target.value)}
                    placeholder={"TASK001\nTASK002\nTASK003"}
                    required
                  />
                </div>
              </div>

              <div style={{ display: "flex", gap: 12, marginTop: 10 }}>
                <button
                  type="submit"
                  disabled={loading}
                  style={{
                    background: "#0A1D3D",
                    color: "#fff",
                    border: "none",
                    padding: "10px 16px",
                    borderRadius: 10,
                    cursor: "pointer",
                    fontWeight: 700,
                  }}
                >
                  {loading ? "Checking..." : "Check"}
                </button>
              </div>
            </form>
          </div>

          {/* ДОЛНА: резултати — показва се само след Check */}
          {hasQueried && (
            <div style={CARD}>
              <div style={{ display: "flex", alignItems: "center", marginBottom: 8 }}>
                <h3 style={{ margin: 0, flex: 1 }}>Results</h3>

                <button
                  type="button"
                  onClick={exportExcel}
                  disabled={!Array.isArray(result) || result.length === 0}
                  style={{
                    background: "transparent",
                    color: "#0A1D3D",
                    border: "1px solid #0A1D3D",
                    padding: "8px 12px",
                    borderRadius: 10,
                    cursor: Array.isArray(result) && result.length ? "pointer" : "not-allowed",
                    fontWeight: 700,
                    marginRight: 8,
                  }}
                >
                  Export Excel
                </button>

                {/* NEW: Copy to Clipboard */}
                <button
                  type="button"
                  onClick={copyToClipboard}
                  disabled={!Array.isArray(result) || result.length === 0}
                  style={{
                    background: "transparent",
                    color: "#0A1D3D",
                    border: "1px solid #0A1D3D",
                    padding: "8px 12px",
                    borderRadius: 10,
                    cursor: Array.isArray(result) && result.length ? "pointer" : "not-allowed",
                    fontWeight: 700,
                    marginRight: 8,
                  }}
                >
                  Copy to Clipboard
                </button>

                <ColumnsMenu
                  allColumns={allColumns}
                  visibleCols={visibleCols}
                  setVisibleCols={setVisibleCols}
                  allSelected={allSelected}
                  toggleSelectAll={toggleSelectAll}
                />

                {/* Toast съобщение до бутоните */}
                {copyMsg && (
                  <div style={{ marginLeft: 12, fontSize: 12, fontWeight: 700, color: "green" }}>
                    {copyMsg}
                  </div>
                )}
              </div>

              <div
                style={{
                  flex: 1,
                  overflowY: "auto",
                  overflowX: "auto",
                  borderRadius: 12,
                  border: "1px solid rgba(10,29,61,0.15)",
                  background: "#fff",
                  paddingTop: 0,
                }}
              >
                {infoMsg ? (
                  <div style={{ padding: 16, fontWeight: 700 }}>{infoMsg}</div>
                ) : (
                  <ResultsTable
                    rows={result}
                    columns={allColumns.filter((c) => visibleCols[c.key])}
                    tdStyle={td}
                  />
                )}
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}

function ResultsTable({ rows, columns, tdStyle }) {
  return (
    <table
      style={{
        width: "100%",
        minWidth: 1200,
        borderCollapse: "separate",
        borderSpacing: 0,
        color: "#0A1D3D",
      }}
    >
      <thead
        style={{
          background: "#DCE3EE",   // статичен фон
          position: "sticky",
          top: 0,
          zIndex: 2,
          color: "#0A1D3D",        // фирменият син
          fontWeight: 800
        }}
      >
        <tr>
          {columns.map((col) => (
            <th
              key={col.key}
              style={{
                textAlign: "left",
                padding: "10px 12px",
                fontWeight: 800,
                fontSize: 13,
                borderBottom: "1px solid rgba(0,0,0,0.1)",
                whiteSpace: "nowrap",
                background: "#DCE3EE",
                color: "#0A1D3D"
              }}
            >
              {col.label}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {!rows || rows.length === 0 ? (
          <tr>
            <td style={{ padding: 16 }} colSpan={columns.length}>
              No data.
            </td>
          </tr>
        ) : (
          rows.map((r, idx) => (
            <tr key={idx} style={{ borderBottom: "1px solid rgba(10,29,61,0.1)" }}>
              {columns.map((col) => {
                let val = r?.[col.key];
                if (col.key === "exists") val = r?.exists ? "Yes" : "No";
                if (col.key === "lastUpdate" && r?.lastUpdate) {
                  try {
                    val = new Date(r.lastUpdate).toLocaleDateString();
                  } catch {}
                }
                return (
                  <td key={col.key} style={tdStyle}>
                    {val ?? ""}
                  </td>
                );
              })}
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
}

function ColumnsMenu({ allColumns, visibleCols, setVisibleCols, allSelected, toggleSelectAll }) {
  const [open, setOpen] = useState(false);
  const menu = { position: "relative" };
  const panel = {
    position: "absolute",
    right: 0,
    top: "100%",
    marginTop: 8,
    background: "#fff",
    color: "#0A1D3D",
    border: "1px solid rgba(10,29,61,0.2)",
    borderRadius: 10,
    boxShadow: "0 12px 28px rgba(0,0,0,0.15)",
    padding: 10,
    zIndex: 5,
    minWidth: 240,
    maxHeight: 320,
    overflowY: "auto",
  };
  const btn = {
    background: "transparent",
    color: "#0A1D3D",
    border: "1px solid #0A1D3D",
    padding: "8px 12px",
    borderRadius: 10,
    cursor: "pointer",
    fontWeight: 700,
  };

  return (
    <div style={menu} onMouseLeave={() => setOpen(false)}>
      <button type="button" onClick={() => setOpen((v) => !v)} style={btn}>
        Columns
      </button>
      {open && (
        <div style={panel}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 }}>
            <strong>Toggle columns</strong>
            <button type="button" onClick={toggleSelectAll} style={{ ...btn, padding: "4px 8px" }}>
              {allSelected ? "Deselect all" : "Select all"}
            </button>
          </div>
          {allColumns.map((c) => (
            <label
              key={c.key}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 8,
                padding: "6px 4px",
                cursor: "pointer",
                userSelect: "none",
              }}
            >
              <input
                type="checkbox"
                checked={!!visibleCols[c.key]}
                onChange={() =>
                  setVisibleCols((prev) => ({
                    ...prev,
                    [c.key]: !prev[c.key],
                  }))
                }
              />
              <span>{c.label}</span>
            </label>
          ))}
        </div>
      )}
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

export default TaskStatus;
