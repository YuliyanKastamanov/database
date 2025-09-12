// src/pages/AddTasks.js
import { useEffect, useMemo, useState, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import bg from "../assets/lufthansa_aircraft.jpg";

function AddTasks({ user, onLogout }) {
  const navigate = useNavigate();

  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [revision, setRevision] = useState("");
  const [availableRevision, setAvailableRevision] = useState(null);

  const [excelRows, setExcelRows] = useState([]);
  const [excelUploaded, setExcelUploaded] = useState(false);
  const [uploadedFileName, setUploadedFileName] = useState("");
  const [isDragOver, setIsDragOver] = useState(false);

  const [result, setResult] = useState([]);
  const [hasQueried, setHasQueried] = useState(false);

  const isAdmin = Array.isArray(user?.roles) && user.roles.includes("ADMIN");
  const displayName = user?.name || user?.uNumber || "User";

  // --- Refresh TaskTypes (използва се и при submit) ---
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

  // Начално зареждане на типовете
  useEffect(() => {
    refreshTaskTypes();
  }, [refreshTaskTypes]);

  // Избран Task Type обект
  const selectedTypeObj = useMemo(
    () => taskTypes.find((t) => t.type === selectedType),
    [taskTypes, selectedType]
  );

  // Ревизия: ако има dbRevision -> readonly; ако няма -> задължително поле
  useEffect(() => {
    if (!selectedTypeObj) {
      setAvailableRevision(null);
      setRevision("");
      return;
    }
    if (selectedTypeObj.dbRevision) {
      setAvailableRevision(selectedTypeObj.dbRevision);
      setRevision(selectedTypeObj.dbRevision);
    } else {
      setAvailableRevision(null);
      setRevision("");
    }
    // ВАЖНО: Тук НЕ зануляваме hasQueried, за да не изчезва долната плочка след submit.
  }, [selectedTypeObj]);

  // Скрий резултатите при СМЯНА на типа
  useEffect(() => {
    setHasQueried(false);
  }, [selectedType]);

  // Колони за вход (Excel) и изход (резултати)
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

  const resultColumns = useMemo(
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
      { key: "revision", label: "Revision" },
      { key: "status", label: "Result Status" },
    ],
    []
  );

  // Четене на Excel
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
        setExcelUploaded(true);                 // <<< ще разшири горната карта
        setUploadedFileName(file.name);
        setHasQueried(false);                   // скрий долната карта при нов файл
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

  // File input
  const handleFileUpload = (e) => {
    const file = e.target.files?.[0];
    if (file) parseExcelFile(file);
    // Позволи повторен upload на СЪЩИЯ файл
    e.target.value = "";
  };

  // Drag & drop
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

  // Template download
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
      "add-tasks-template.xlsx"
    );
  };

  // Submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedType) return;

    // Ако няма текуща DB ревизия -> задължително въвеждане на нова
    if (!availableRevision && !revision) {
      alert("Please enter a Revision (no current DB revision exists for this Task Type).");
      return;
    }

    // Само Excel вход: филтрираме празни taskNumber
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

    const finalRevision = availableRevision ? String(availableRevision) : String(revision);

    // Payload за бекенда (AddTaskDTOs wrapper)
    const payload = {
      taskNumbers: cleanRows,
      type: selectedType,
      revision: finalRevision,
    };

    try {
      const res = await fetch("http://localhost:8080/tasks", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      const data = await res.json();
      setResult(Array.isArray(data) ? data : []);
      setHasQueried(true);      // покажи долната плочка

      // Изчистваме входа след успех => горната плочка се свива обратно
      setExcelRows([]);
      setExcelUploaded(false);  // <<< ще върне височината на половин екран
      setUploadedFileName("");

      // Рефрешваме типовете (за динамична ревизия)
      await refreshTaskTypes();
    } catch (err) {
      console.error(err);
    }
  };

  // Export на резултата
  const exportExcel = () => {
    if (!result || result.length === 0) return;
    const worksheetData = result.map((r) => {
      const base = r?.addTaskDTO || r || {};
      return {
        "Task Number": base.taskNumber ?? "",
        "SOC Status": base.socStatus ?? "",
        "SOC Description": base.socDescription ?? "",
        "Comment": base.comment ?? "",
        "Coversheet SAP": base.coversheetSap ?? "",
        "Coversheet Status": base.coversheetStatus ?? "",
        "Created MJob": base.createdMJob ?? "",
        "MJob Status": base.statusMJob ?? "",
        "SB Reference": base.sbReference ?? "",
        Revision: base.revision ?? "",
        "Result Status": r?.status ?? base.status ?? "",
      };
    });
    const ws = XLSX.utils.json_to_sheet(worksheetData);
    ws["!cols"] = resultColumns.map(() => ({ wch: 18 }));
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Added Tasks");
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    saveAs(blob, `added-tasks-${new Date().toISOString().split("T")[0]}.xlsx`);
  };

  // ---- Styles (консистентни с TaskStatus) ----
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
  // >>> променяме височината според excelUploaded (разширяване/свиване)
  const cardHeight = excelUploaded
    ? "calc(99vh - 80px - 24px)"                 // максимално (като при AddRevision)
    : "calc((99vh - 80px - 24px) / 2)";          // половин екран (по подразб.)
  const CARD = {
    background: "rgba(255,255,255,0.88)",
    borderRadius: 16,
    padding: 18,
    width: cardWidth,
    height: cardHeight,
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

        {/* Main */}
        <main style={INNER}>
          {/* Горна карта: вход */}
          <div style={CARD}>
            <h2 style={{ margin: 0, marginBottom: 10 }}>Add Tasks</h2>

            <form onSubmit={handleSubmit} style={{ flex: 1, display: "flex", flexDirection: "column", minHeight: 0 }}>
              {/* Task Type + Revision */}
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
                  <div style={LABEL}>Revision</div>
                  {availableRevision ? (
                    <input style={INPUT} value={availableRevision} readOnly />
                  ) : (
                    <input
                      style={INPUT}
                      value={revision}
                      onChange={(e) => setRevision(e.target.value)}
                      required
                      placeholder="Enter new revision"
                    />
                  )}
                  {!!selectedTypeObj && (
                    <div style={{ marginTop: 6, fontSize: 12, fontWeight: 700, opacity: 0.85 }}>
                      {availableRevision ? (
                        <>
                          Current DB revision:{" "}
                          <span style={{ fontWeight: 900 }}>{String(availableRevision)}</span>
                        </>
                      ) : (
                        <>No current DB revision — please enter a new one.</>
                      )}
                    </div>
                  )}
                </div>
              </div>

              {/* Controls: Template + Dropzone + file input */}
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

                {/* Dropzone (drag & drop или клик) */}
                <label
                  style={DROPZONE}
                  onDragOver={onDragOver}
                  onDragLeave={onDragLeave}
                  onDrop={onDrop}
                  title="Drop an Excel file here or click to choose"
                >
                  <input type="file" accept=".xlsx,.xls" onChange={handleFileUpload} style={{ display: "none" }} />
                  <span style={{ fontWeight: 700 }}>
                    {excelUploaded ? "File selected:" : "Drop / Click to upload Excel"}
                  </span>
                  <span style={{ opacity: 0.8 }}>{excelUploaded ? uploadedFileName : "(XLSX / XLS)"}</span>
                </label>

                {excelUploaded && <span style={{ fontWeight: 700, color: "green" }}>Excel uploaded ✔</span>}

                <span style={{ marginLeft: "auto", fontSize: 12, opacity: 0.8 }}>
                  Only Excel input is used. Each row must have a Task Number.
                </span>
              </div>

              {/* Таблица (скрол вътре) */}
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

              {/* Submit – винаги видим */}
              <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 10 }}>
                <button
                  type="submit"
                  style={{
                    background: "#0A1D3D",
                    color: "#fff",
                    border: "none",
                    borderRadius: 10,
                    padding: "10px 16px",
                    cursor: "pointer",
                    fontWeight: 700,
                  }}
                >
                  Submit
                </button>
              </div>
            </form>
          </div>

          {/* Долна карта: резултат */}
          {hasQueried && (
            <div style={CARD}>
              <div style={{ display: "flex", alignItems: "center", marginBottom: 8 }}>
                <h3 style={{ margin: 0, flex: 1 }}>Results</h3>
                <button
                  onClick={exportExcel}
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
                    minWidth: 1100,
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
                        <td style={{ padding: 16 }} colSpan={resultColumns.length}>
                          No results.
                        </td>
                      </tr>
                    ) : (
                      result.map((r, idx) => {
                        const dto = r?.addTaskDTO || {};
                        return (
                          <tr key={idx} style={{ borderBottom: "1px solid rgba(10,29,61,0.1)" }}>
                            {resultColumns.map((col) => {
                              const val =
                                dto[col.key] ??
                                r[col.key] ??
                                (col.key === "revision" ? dto.revision ?? revision : "");
                              return (
                                <td
                                  key={col.key}
                                  style={{
                                    padding: "10px 12px",
                                    fontSize: 13,
                                    borderBottom: "1px solid rgba(10,29,61,0.08)",
                                    whiteSpace: "nowrap",
                                  }}
                                >
                                  {val ?? ""}
                                </td>
                              );
                            })}
                          </tr>
                        );
                      })
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

export default AddTasks;