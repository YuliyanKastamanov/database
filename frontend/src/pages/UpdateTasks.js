// src/pages/UpdateTasks.js
import { useEffect, useMemo, useState, useCallback, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import bg from "../assets/lufthansa_aircraft.jpg";

function UpdateTasks({ user, onLogout }) {
  const navigate = useNavigate();

  // ---------- State ----------
  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [currentRevision, setCurrentRevision] = useState(""); // dbRevision (readonly)

  const [rows, setRows] = useState(() => [emptyRow()]);
  const [excelUploaded, setExcelUploaded] = useState(false);
  const [uploadedFileName, setUploadedFileName] = useState("");
  const [isDragOver, setIsDragOver] = useState(false);
  const fileInputRef = useRef(null);

  const [submitting, setSubmitting] = useState(false);

  const [result, setResult] = useState([]);
  const [hasResults, setHasResults] = useState(false);

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // ---------- Columns ----------
  const inputColumns = useMemo(
    () => [
      { key: "taskNumber", label: "Task Number", required: true },
      { key: "revision", label: "Revision" },
      { key: "socStatus", label: "SOC Status" },
      { key: "socDescription", label: "SOC Description" },
      { key: "comment", label: "Comment" },
      { key: "coversheetSap", label: "Coversheet SAP", required: true },
      { key: "coversheetStatus", label: "Coversheet Status", required: true },
      { key: "createdMJob", label: "Created MJob", required: true },
      { key: "statusMJob", label: "MJob Status", required: true },
      { key: "sbReference", label: "SB Reference" },
    ],
    []
  );

  const resultColumns = useMemo(
    () => [
      { key: "taskNumber", label: "Task Number" },
      { key: "revision", label: "Revision" },
      { key: "socStatus", label: "SOC Status" },
      { key: "socDescription", label: "SOC Description" },
      { key: "comment", label: "Comment" },
      { key: "coversheetSap", label: "Coversheet SAP" },
      { key: "coversheetStatus", label: "Coversheet Status" },
      { key: "createdMJob", label: "Created MJob" },
      { key: "statusMJob", label: "MJob Status" },
      { key: "sbReference", label: "SB Reference" },
      { key: "statusInfo", label: "Status Info" },
      { key: "exists", label: "Exists" },
      { key: "cri", label: "CRI" },
      { key: "lastUpdate", label: "Last Update" },
      { key: "currentUpdate", label: "Current Update" },
      { key: "jceName", label: "JCE Name" },
      { key: "id", label: "ID" },
      { key: "type", label: "Type" },
    ],
    []
  );

  // ---------- Helpers ----------
  function emptyRow() {
    return {
      taskNumber: "",
      revision: "",
      socStatus: "",
      socDescription: "",
      comment: "",
      coversheetSap: "",
      coversheetStatus: "",
      createdMJob: "",
      statusMJob: "",
      sbReference: "",
    };
  }

  const refreshTaskTypes = useCallback(async () => {
    try {
      const res = await fetch("http://localhost:8080/task-types", { credentials: "include" });
      if (!res.ok) throw new Error("Failed to load task types");
      const data = await res.json();
      setTaskTypes(data);
    } catch (e) {
      console.error(e);
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

  useEffect(() => {
    setCurrentRevision(selectedTypeObj?.dbRevision ?? "");
  }, [selectedTypeObj]);

  useEffect(() => {
    // смяна на тип -> скрий долната карта
    setHasResults(false);
  }, [selectedType]);

  // ---------- Excel template (FIX: define downloadTemplate) ----------
  const downloadTemplate = () => {
    const headers = inputColumns.map((c) => c.label);
    const ws = XLSX.utils.aoa_to_sheet([headers]); // само заглавен ред
    ws["!cols"] = inputColumns.map(() => ({ wch: 18 }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Template");
    const buf = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    saveAs(
      new Blob([buf], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      }),
      "update-tasks-template.xlsx"
    );
  };

  // ---------- Excel parsing ----------
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

        setRows([...normalized, emptyRow()]);
        setExcelUploaded(true);
        setUploadedFileName(file.name);
        setHasResults(false);
      } catch (err) {
        console.error(err);
        setRows([emptyRow()]);
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
    // позволи качване на СЪЩИЯ файл отново
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  // ---------- Drag & Drop ----------
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

  // ---------- Manual grid ----------
  const ensureTrailingEmptyRow = (rowIdx) => {
    if (rowIdx === rows.length - 1) {
      setRows((prev) => [...prev, emptyRow()]);
    }
  };

  const handleCellChange = (rowIdx, key, value) => {
    setRows((prev) => {
      const next = [...prev];
      next[rowIdx] = { ...next[rowIdx], [key]: value };
      return next;
    });
  };

  // ---------- Clear All ----------
  const clearAllRows = () => {
    setRows([emptyRow()]);
    setExcelUploaded(false);
    setUploadedFileName("");
    setHasResults(false);
  };

  // ---------- Submit ----------
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedType) {
      alert("Please select a Task Type.");
      return;
    }

    const trimmed = rows
      .map((r) => ({
        taskNumber: (r.taskNumber || "").trim(),
        revision: (r.revision || "").trim(),
        socStatus: (r.socStatus || "").trim(),
        socDescription: (r.socDescription || "").trim(),
        comment: (r.comment || "").trim(),
        coversheetSap: (r.coversheetSap || "").trim(),
        coversheetStatus: (r.coversheetStatus || "").trim(),
        createdMJob: (r.createdMJob || "").trim(),
        statusMJob: (r.statusMJob || "").trim(),
        sbReference: (r.sbReference || "").trim(),
      }))
      .filter((r) => Object.values(r).some((v) => v && v.length > 0));

    const requiredKeys = ["taskNumber", "coversheetSap", "coversheetStatus", "createdMJob", "statusMJob"];
    const invalid = trimmed.filter((r) => requiredKeys.some((k) => !r[k]));
    if (invalid.length > 0) {
      alert(
        "Some rows are missing required fields: Task Number, Coversheet SAP, Coversheet Status, Created MJob, MJob Status."
      );
      return;
    }

    if (trimmed.length === 0) {
      alert("Please add at least one row or upload an Excel file.");
      return;
    }

    const payload = trimmed.map((r) => ({
      ...r,
      type: selectedType,
    }));

    try {
      setSubmitting(true);
      const res = await fetch("http://localhost:8080/tasks/update", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const txt = await res.text();
        throw new Error(txt || "Update failed");
      }

      const data = await res.json();
      setResult(Array.isArray(data) ? data : []);
      setHasResults(true);

      // изчисти горната таблица
      setRows([emptyRow()]);
      setExcelUploaded(false);
      setUploadedFileName("");
    } catch (err) {
      console.error(err);
      alert("Update failed. Please check your input and try again.");
    } finally {
      setSubmitting(false);
    }
  };

  // ---------- Export / Copy ----------
  const exportResultsExcel = () => {
    if (!result || result.length === 0) return;

    const worksheetData = result.map((r) => ({
      "Task Number": r.taskNumber ?? "",
      Revision: r.revision ?? "",
      "SOC Status": r.socStatus ?? "",
      "SOC Description": r.socDescription ?? "",
      Comment: r.comment ?? "",
      "Coversheet SAP": r.coversheetSap ?? "",
      "Coversheet Status": r.coversheetStatus ?? "",
      "Created MJob": r.createdMJob ?? "",
      "MJob Status": r.statusMJob ?? "",
      "SB Reference": r.sbReference ?? "",
      "Status Info": r.statusInfo ?? "",
      Exists: r.exists ? "Yes" : "No",
      CRI: r.cri ?? "",
      "Last Update": r.lastUpdate ?? "",
      "Current Update": r.currentUpdate ?? "",
      "JCE Name": r.jceName ?? "",
      ID: r.id ?? "",
      Type: r.type ?? "",
    }));

    const ws = XLSX.utils.json_to_sheet(worksheetData);
    ws["!cols"] = Object.keys(worksheetData[0] || { A: "" }).map(() => ({ wch: 18 }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Updated Tasks");
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    saveAs(blob, `updated-tasks-${new Date().toISOString().split("T")[0]}.xlsx`);
  };

  const copyResultsToClipboard = async () => {
    if (!result || result.length === 0) return;
    const headers = resultColumns.map((c) => c.label);
    const lines = [headers.join("\t")];
    result.forEach((r) => {
      const row = resultColumns.map((c) => {
        const v = r[c.key];
        return v == null ? "" : String(v).replace(/\t/g, " ").replace(/\r?\n/g, " ");
      });
      lines.push(row.join("\t"));
    });
    try {
      await navigator.clipboard.writeText(lines.join("\n"));
      alert("Results copied to clipboard (TSV).");
    } catch {
      alert("Could not copy to clipboard.");
    }
  };

  // ---------- Styles (взети от AddTasks) ----------
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
    height:
      (rows && rows.some((r) => Object.values(r).some((v) => v)) || excelUploaded)
        ? "calc(99vh - 80px - 24px)"
        : "calc((99vh - 80px - 24px) / 2)",
    boxShadow: "0 10px 26px rgba(0,0,0,0.24)",
    color: "#0A1D3D",
    display: "flex",
    flexDirection: "column",
  };
  const HALF_CARD = {
    ...CARD,
    height: "calc((99vh - 80px - 24px) / 2)",
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

  // ---------- Render ----------
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

        <main style={INNER}>
          {/* Горна карта: вход (визуално като AddTasks) */}
          <div style={CARD}>
            <h2 style={{ margin: 0, marginBottom: 10 }}>Update Tasks</h2>

            <form onSubmit={handleSubmit} style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
              {/* Task Type + Current DB Revision */}
              <div style={{ display: "flex", gap: 16, marginBottom: 12, flexWrap: "wrap" }}>
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

                <div style={{ flex: "1 1 260px", minWidth: 220 }}>
                  <div style={LABEL}>Current DB Revision</div>
                  <input style={INPUT} value={currentRevision || ""} readOnly placeholder="None" />
                </div>
              </div>

              {/* Controls: Template + Dropzone + Clear All (позициониране като AddTasks) */}
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

                {/* Dropzone – като в AddTasks */}
                <label
                  style={DROPZONE}
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
                  <span style={{ opacity: 0.8 }}>{excelUploaded ? uploadedFileName : "(XLSX / XLS)"}</span>
                </label>

                {excelUploaded && <span style={{ fontWeight: 700, color: "green" }}>Excel uploaded ✔</span>}

                <button
                  type="button"
                  onClick={clearAllRows}
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
                  Clear All
                </button>

                <span style={{ marginLeft: "auto", fontSize: 12, opacity: 0.8 }}>
                  You can type rows or upload Excel. Required fields are marked *.
                </span>
              </div>

              {/* Editable table (scroll inside) */}
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
                <table style={{ width: "100%", borderCollapse: "separate", borderSpacing: 0, minWidth: 1100 }}>
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
                          {col.required ? <span style={{ color: "#B00020", marginLeft: 4 }}>*</span> : null}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {rows.length === 0 ? (
                      <tr>
                        <td colSpan={inputColumns.length} style={{ padding: 12, textAlign: "center", color: "#666" }}>
                          Start typing or upload an Excel file.
                        </td>
                      </tr>
                    ) : (
                      rows.map((r, rowIdx) => (
                        <tr key={rowIdx}>
                          {inputColumns.map((col) => (
                            <td
                              key={col.key}
                              style={{
                                padding: "8px 12px",
                                fontSize: 13,
                                borderBottom: "1px solid rgba(10,29,61,0.1)",
                                whiteSpace: "nowrap",
                              }}
                              onMouseDown={() => ensureTrailingEmptyRow(rowIdx)}
                            >
                              <input
                                value={r[col.key] ?? ""}
                                onChange={(e) => handleCellChange(rowIdx, col.key, e.target.value)}
                                onFocus={() => ensureTrailingEmptyRow(rowIdx)}
                                placeholder={col.label}
                                style={{
                                  width: "100%",
                                  padding: "6px 8px",
                                  borderRadius: 8,
                                  border: "1px solid rgba(10,29,61,0.25)",
                                  fontSize: 12.5,
                                  outline: "none",
                                  background: "#fff",
                                  color: "#0A1D3D",
                                }}
                              />
                            </td>
                          ))}
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              {/* Submit – вътре в картата */}
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
                  {submitting ? "Updating..." : "Submit"}
                </button>
              </div>
            </form>
          </div>

          {/* Долна карта: резултат – таблично, с Copy/Export */}
          {hasResults && (
            <div style={HALF_CARD}>
              <div style={{ display: "flex", alignItems: "center", marginBottom: 8 }}>
                <h3 style={{ margin: 0, flex: 1 }}>Results</h3>
                <button
                  onClick={copyResultsToClipboard}
                  style={{
                    background: "transparent",
                    color: "#0A1D3D",
                    border: "1px solid #0A1D3D",
                    padding: "8px 12px",
                    borderRadius: 10,
                    cursor: result.length ? "pointer" : "not-allowed",
                    fontWeight: 700,
                    marginRight: 8,
                  }}
                  disabled={!result.length}
                >
                  Copy to Clipboard
                </button>
                <button
                  onClick={exportResultsExcel}
                  style={{
                    background: "transparent",
                    color: "#0A1D3D",
                    border: "1px solid #0A1D3D",
                    padding: "8px 12px",
                    borderRadius: 10,
                    cursor: result.length ? "pointer" : "not-allowed",
                    fontWeight: 700,
                  }}
                  disabled={!result.length}
                >
                  Export Excel
                </button>
              </div>

              <div
                style={{
                  flex: 1,
                  overflow: "auto",
                  borderRadius: 12,
                  border: "1px solid rgba(10,29,61,0.15)",
                  background: "#fff",
                }}
              >
                <table
                  style={{
                    width: "100%",
                    borderCollapse: "separate",
                    borderSpacing: 0,
                    minWidth: 1200,
                    color: "#0A1D3D",
                  }}
                >
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
                      {resultColumns.map((col) => (
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
                    {result.length === 0 ? (
                      <tr>
                        <td colSpan={resultColumns.length} style={{ padding: 16, textAlign: "center", color: "#666" }}>
                          No results.
                        </td>
                      </tr>
                    ) : (
                      result.map((r, idx) => (
                        <tr key={idx} style={{ borderBottom: "1px solid rgba(10,29,61,0.1)" }}>
                          {resultColumns.map((col) => (
                            <td
                              key={col.key}
                              style={{
                                padding: "8px 12px",
                                fontSize: 13,
                                borderBottom: "1px solid rgba(10,29,61,0.08)",
                                whiteSpace: "nowrap",
                              }}
                            >
                              {formatCell(r[col.key])}
                            </td>
                          ))}
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}

function formatCell(v) {
  if (v == null) return "";
  if (typeof v === "boolean") return v ? "Yes" : "No";
  return String(v);
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
    >
      {children}
    </Link>
  );
}

export default UpdateTasks;
