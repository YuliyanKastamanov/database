// src/pages/AddRevision.js
import { useEffect, useMemo, useState, useCallback, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import bg from "../assets/lufthansa_aircraft.jpg";

function AddRevision({ user, onLogout }) {
  const navigate = useNavigate();

  // --- state ---
  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [currentRevision, setCurrentRevision] = useState(""); // readonly
  const [newRevision, setNewRevision] = useState("");         // user input

  const [excelRows, setExcelRows] = useState([]);
  const [excelUploaded, setExcelUploaded] = useState(false);
  const [uploadedFileName, setUploadedFileName] = useState("");
  const [isDragOver, setIsDragOver] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [successMsg, setSuccessMsg] = useState(""); // <-- показва се след успешен submit

  const fileInputRef = useRef(null); // <-- за click-to-upload

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // --- refresh task types ---
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

  // при смяна на type-обекта → само актуализираме текущата ревизия
  useEffect(() => {
    if (!selectedTypeObj) {
      setCurrentRevision("");
    } else {
      setCurrentRevision(selectedTypeObj.dbRevision ?? "");
    }
  }, [selectedTypeObj]);

  // при смяна на избрания тип (реална промяна в select) → чистим successMsg
  useEffect(() => {
    setSuccessMsg("");
  }, [selectedType]);

  // колони за вход
  const inputColumns = useMemo(
    () => [
      { key: "taskNumber", label: "Task Number" },
      { key: "socStatus", label: "SOC Status" },
      { key: "socDescription", label: "SOC Description" },
      { key: "comment", label: "Comment" },
      { key: "coversheetSap", label: "Coversheet SAP" },
      { key: "coversheetStatus", label: "Coversheet Status" },
      { key: "createdMJob", label: "Created MJob" },
      { key: "statusMJob", label: "MJob Status" },
      { key: "sbReference", label: "SB Reference" },
    ],
    []
  );

  // --- excel helpers ---
  const parseExcelFile = (file) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: "array" });
        const ws = workbook.Sheets[workbook.SheetNames[0]];
        const json = XLSX.utils.sheet_to_json(ws, { defval: "" });

        const normalized = json.map((row) => {
          const obj = {};
          inputColumns.forEach(({ key, label }) => {
            obj[key] = row[key] ?? row[label] ?? "";
          });
          obj.taskNumber = String(obj.taskNumber ?? "").trim();
          return obj;
        });

        setExcelRows(normalized);
        setExcelUploaded(true);
        setUploadedFileName(file.name);
        setSuccessMsg(""); // <-- качваме нов файл → скриваме старото success съобщение
      } catch (err) {
        console.error(err);
        setExcelRows([]);
        setExcelUploaded(false);
        setUploadedFileName("");
        alert("Could not read the Excel file. Please check the template/columns.");
      }
    };
    reader.readAsArrayBuffer(file);
  };

  const handleFileUpload = (e) => {
    const file = e.target.files?.[0];
    if (file) parseExcelFile(file);
  };

  const onDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(true);
  };
  const onDragLeave = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(false);
  };
  const onDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragOver(false);
    const file = e.dataTransfer.files?.[0];
    if (file) parseExcelFile(file);
  };

  // template download
  const downloadTemplate = () => {
    const headers = inputColumns.map((c) => c.label);
    const ws = XLSX.utils.aoa_to_sheet([headers]);
    ws["!cols"] = inputColumns.map(() => ({ wch: 18 }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Template");
    const buf = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    saveAs(
      new Blob([buf], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      }),
      "add-revision-template.xlsx"
    );
  };

  // --- submit ---
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedType) {
      alert("Please select a Task Type.");
      return;
    }
    if (!newRevision.trim()) {
      alert("Please enter the New Revision.");
      return;
    }

    // само редове от Excel; филтрираме празни taskNumber
    const cleanRows = excelRows
      .map((r) => ({
        taskNumber: (r.taskNumber || "").trim(),
        socStatus: r.socStatus || "",
        socDescription: r.socDescription || "",
        comment: r.comment || "",
        coversheetSap: r.coversheetSap || "",
        coversheetStatus: r.coversheetStatus || "",
        createdMJob: r.createdMJob || "",
        statusMJob: r.statusMJob || "",
        sbReference: r.sbReference || "",
      }))
      .filter((r) => r.taskNumber);

    if (cleanRows.length === 0) {
      alert("Please upload an Excel file with at least one valid Task Number.");
      return;
    }

    const payload = {
      taskNumbers: cleanRows,
      type: selectedType,
      revision: String(newRevision.trim()),
    };

    try {
      setSubmitting(true);
      const res = await fetch("http://localhost:8080/tasks/revisions", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Failed to submit");
      }

      const data = await res.json();

      // Генерираме и сваляме Excel (както преди)
      const worksheetData = (Array.isArray(data) ? data : []).map((r) => ({
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
      ws["!cols"] = Object.keys(worksheetData[0] || { A: "" }).map(() => ({ wch: 18 }));
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "Tasks (Before New Revision)");
      const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
      const blob = new Blob([excelBuffer], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      const today = new Date().toISOString().split("T")[0];
      saveAs(blob, `tasks-before-revision-${selectedType}-${today}.xlsx`);

      // Успех → показваме съобщение, чистим полето за нова ревизия
      setSuccessMsg(
        `New revision "${newRevision.trim()}" for task type "${selectedType}" was added successfully.`
      );
      setNewRevision("");

      // чистим файла/таблицата
      setExcelRows([]);
      setExcelUploaded(false);
      setUploadedFileName("");

      // рефреш на типовете (за да вземем новата currentRevision)
      await refreshTaskTypes();

    } catch (err) {
      console.error(err);
      alert("Submission failed. Please check your input and try again.");
    } finally {
      setSubmitting(false);
    }
  };

  // ---- стилистика ----
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
    height: excelUploaded ? "calc(99vh - 80px - 24px)" : "calc((99vh - 80px - 24px) / 2)",
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

  const DROPZONE = {
    flex: "0 0 auto",
    padding: 16,
    border: `2px dashed ${isDragOver ? "#0A1D3D" : "rgba(10,29,61,0.35)"}`,
    borderRadius: 12,
    background: isDragOver ? "rgba(10,29,61,0.06)" : "transparent",
    display: "flex",
    alignItems: "center",
    gap: 12,
    cursor: "pointer",
    userSelect: "none",
  };

  return (
    <div style={SHELL}>
      {/* Sidebar */}
      <aside style={SIDEBAR}>
        <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 18 }}>Menu</div>
        {isAdmin ? (
          <>
            <NavButton to="/dashboard">Dashboard</NavButton>
            <NavButton to="/manage-users">Manage Users</NavButton>
            <NavButton to="/manage-tasks">Manage Tasks</NavButton>
            <NavButton to="/generator">Generator</NavButton>
            <NavButton to="/reports">Reports</NavButton>
          </>
        ) : (
          <>
            <NavButton to="/dashboard">Dashboard</NavButton>
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
            <h2 style={{ margin: 0, marginBottom: 10 }}>Add New Revision</h2>

            {/* Success message (остава докато не смениш тип или не качиш нов файл) */}
            {successMsg && (
              <div
                style={{
                  marginBottom: 10,
                  padding: "10px 12px",
                  borderRadius: 10,
                  background: "#E6F4EA",
                  color: "#0B6B3A",
                  fontWeight: 700,
                  border: "1px solid rgba(11,107,58,0.25)",
                }}
              >
                {successMsg}
              </div>
            )}

            <form onSubmit={handleSubmit} style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
              {/* Row: Task Type, Current Revision (readonly), New Revision */}
              <div style={{ display: "flex", gap: 16, marginBottom: 12, flexWrap: "wrap" }}>
                <div style={{ flex: "1 1 260px", minWidth: 220 }}>
                  <div style={LABEL}>Task Type</div>
                  <select
                    style={INPUT}
                    value={selectedType}
                    onChange={(e) => setSelectedType(e.target.value)}
                    required
                    // ако искаш да чисти съобщението дори при клик без смяна — махни коментара:
                    // onMouseDown={() => setSuccessMsg("")}
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
                  <div style={LABEL}>Current DB Revision</div>
                  <input style={INPUT} value={currentRevision || ""} readOnly placeholder="None" />
                </div>

                <div style={{ flex: "1 1 220px", minWidth: 200 }}>
                  <div style={LABEL}>New Revision</div>
                  <input
                    style={INPUT}
                    value={newRevision}
                    onChange={(e) => {
                      setNewRevision(e.target.value);
                      setSuccessMsg(""); // променяш ревизията → скрий старото съобщение
                    }}
                    required
                    placeholder="Enter new revision (e.g., R02)"
                  />
                </div>
              </div>

              {/* Controls: Template + Dropzone (click + drag&drop) */}
              <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 10, flexWrap: "wrap" }}>
                <button
                  type="button"
                  onClick={downloadTemplate}
                  style={{
                    background: "transparent",
                    color: "#0A1D3D",
                    border: "1px solid #0A1D3D",
                    padding: "8px 12px",
                    borderRadius: 10,
                    cursor: "pointer",
                    fontWeight: 700,
                  }}
                >
                  Download Excel Template
                </button>

                <div
                  style={DROPZONE}
                  onClick={() => fileInputRef.current?.click()}
                  onDragOver={onDragOver}
                  onDragLeave={onDragLeave}
                  onDrop={onDrop}
                  title="Drop an Excel file here or click to choose"
                >
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept=".xlsx,.xls"
                    onChange={handleFileUpload}
                    style={{ display: "none" }}
                  />
                  <span style={{ fontWeight: 700 }}>
                    {excelUploaded ? "File selected:" : "Drop / Click to upload Excel"}
                  </span>
                  <span style={{ opacity: 0.8 }}>
                    {excelUploaded ? uploadedFileName : "(XLSX / XLS)"}
                  </span>
                </div>

                {excelUploaded && <span style={{ fontWeight: 700, color: "green" }}>Excel uploaded ✔</span>}

                <span style={{ marginLeft: "auto", fontSize: 12, opacity: 0.8 }}>
                  Only Excel input is used. Each row must have a Task Number.
                </span>
              </div>

              {/* Table (scroll inside) */}
              <div
                style={{
                  flex: 1,
                  minHeight: 120,
                  overflow: "auto",
                  border: "1px solid rgba(10,29,61,0.2)",
                  borderRadius: 10,
                  background: "#fff",
                }}
              >
                <table style={{ width: "100%", borderCollapse: "separate", borderSpacing: 0, minWidth: 1000 }}>
                  <thead
                    style={{
                      background: "#DCE3EE",
                      position: "sticky",
                      top: 0,
                      zIndex: 2,
                      color: "#0A1D3D",
                      fontWeight: 800,
                    }}
                  >
                    <tr>
                      {inputColumns.map((col) => (
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
                            color: "#0A1D3D",
                          }}
                        >
                          {col.label}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {excelRows.length === 0 ? (
                      <tr>
                        <td colSpan={inputColumns.length} style={{ padding: 12, textAlign: "center", color: "#666" }}>
                          No tasks added yet. Upload an Excel file using the template.
                        </td>
                      </tr>
                    ) : (
                      excelRows.map((r, idx) => (
                        <tr key={idx}>
                          {inputColumns.map((col) => (
                            <td
                              key={col.key}
                              style={{
                                padding: "8px 12px",
                                fontSize: 13,
                                borderBottom: "1px solid rgba(10,29,61,0.1)",
                                whiteSpace: "nowrap",
                              }}
                            >
                              {r[col.key]}
                            </td>
                          ))}
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              {/* Submit */}
              <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 10 }}>
                <button
                  type="submit"
                  disabled={submitting}
                  style={{
                    background: "#0A1D3D",
                    color: "#fff",
                    border: "none",
                    borderRadius: 10,
                    padding: "10px 16px",
                    cursor: "pointer",
                    fontWeight: 700,
                    opacity: submitting ? 0.7 : 1,
                  }}
                >
                  {submitting ? "Submitting..." : "Submit"}
                </button>
              </div>
            </form>
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

export default AddRevision;
