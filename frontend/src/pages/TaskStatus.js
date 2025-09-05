import { useState, useEffect } from "react";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

function TaskStatus() {
  const [taskNumbers, setTaskNumbers] = useState("");
  const [taskTypes, setTaskTypes] = useState([]);
  const [selectedType, setSelectedType] = useState("");
  const [revision, setRevision] = useState("");
  const [availableRevisions, setAvailableRevisions] = useState([]);
  const [result, setResult] = useState(null);

  // Зареждаме всички TaskType от бекенда
  useEffect(() => {
    fetch("http://localhost:8080/task-types", { credentials: "include" })
      .then((res) => res.json())
      .then((data) => setTaskTypes(data))
      .catch((err) => console.error(err));
  }, []);

  // Когато изберем task type → показваме текущата ревизия + 3 назад
  useEffect(() => {
    if (selectedType) {
      const type = taskTypes.find((t) => t.type === selectedType);
      if (type) {
        const currentRev = parseInt(type.dbRevision, 10);
        const revs = [currentRev];
        for (let i = 1; i <= 3; i++) {
          revs.push(`${currentRev - i}`);
        }
        setAvailableRevisions(revs);
        setRevision(currentRev);
      }
    }
  }, [selectedType, taskTypes]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      taskNumbers: taskNumbers.split("\n").map((t) => t.trim()),
      taskType: selectedType,
      revision,
      projectType: "COVERSHEET", // TODO: може да стане dropdown ако имаш повече
    };

    const res = await fetch("http://localhost:8080/tasks/status", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify(payload),
    });

    const data = await res.json();
    setResult(data);
  };

  // Export to Excel
const exportExcel = () => {
  if (!Array.isArray(result) || result.length === 0) return;

  const worksheetData = result.map((r) => ({
    "Task Number": r.taskNumber || "",
    "Revision": r.revision || "",
    "Status Info": r.statusInfo || "",
    "SOC Status": r.socStatus || "",
    "Coversheet Status": r.coversheetStatus || "",
    "MJob Status": r.statusMJob || "",
    "CRI": r.cri || "",
    "Exists": r.exists ? "Yes" : "No",
    "Last Update": r.lastUpdate
      ? new Date(r.lastUpdate).toLocaleDateString()
      : "",
  }));

  const worksheet = XLSX.utils.json_to_sheet(worksheetData);

  // autofit ширина на колоните
  worksheet["!cols"] = [
    { wch: 15 },
    { wch: 10 },
    { wch: 25 },
    { wch: 15 },
    { wch: 20 },
    { wch: 15 },
    { wch: 20 },
    { wch: 10 },
    { wch: 15 },
  ];

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "Task Status");

  const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
  const blob = new Blob([excelBuffer], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });

  const today = new Date().toISOString().split("T")[0];
  saveAs(blob, `task-status-${today}.xlsx`);
};


  return (
    <div style={{ padding: "20px", color: "#0A1D3D" }}>
      <h2>Check Task Status</h2>
      <form onSubmit={handleSubmit} style={{ maxWidth: "600px" }}>
        <label>Task Numbers (one per line):</label>
        <textarea
          rows="6"
          style={{ width: "100%", marginBottom: "10px" }}
          value={taskNumbers}
          onChange={(e) => setTaskNumbers(e.target.value)}
          required
        />

        <label>Task Type:</label>
        <select
          style={{ width: "100%", marginBottom: "10px" }}
          value={selectedType}
          onChange={(e) => setSelectedType(e.target.value)}
          required
        >
          <option value="">-- Select --</option>
          {taskTypes.map((t) => (
            <option key={t.id} value={t.type}>
              {t.type}
            </option>
          ))}
        </select>

        {availableRevisions.length > 0 && (
          <>
            <label>Revision:</label>
            <select
              style={{ width: "100%", marginBottom: "10px" }}
              value={revision}
              onChange={(e) => setRevision(e.target.value)}
              required
            >
              {availableRevisions.map((r, idx) => (
                <option key={idx} value={r}>
                  {r}
                </option>
              ))}
            </select>
          </>
        )}

        <button type="submit">Check</button>
      </form>

      {result && (
        <div style={{ marginTop: "20px" }}>
          <h3>Results</h3>
          <ul>
            {result.map((r, idx) => (
              <li key={idx}>
                {r.taskNumber} → {r.statusInfo}
              </li>
            ))}
          </ul>

          <button
            onClick={exportExcel}
            style={{
              marginTop: "12px",
              background: "#0A1D3D",
              color: "#fff",
              border: "none",
              padding: "10px 16px",
              borderRadius: 8,
              cursor: "pointer",
              fontWeight: 600,
            }}
          >
            Export Excel
          </button>
        </div>
      )}
    </div>
  );
}

export default TaskStatus;
