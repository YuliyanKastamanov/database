import { useState, useEffect } from "react";
import Login from "./Login";

function App() {
  const [user, setUser] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Функция за зареждане на задачите
  const fetchTasks = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch("http://localhost:8080/tasks", {
        credentials: "include",
      });
      if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
      const data = await res.json();
      setTasks(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Logout функция
  const logout = async () => {
    try {
      const res = await fetch("http://localhost:8080/auth/logout", {
        credentials: "include",
      });
      if (!res.ok) throw new Error("Logout failed");
      setUser(null);
      setTasks([]);
    } catch (err) {
      alert(err.message);
    }
  };

  // Автоматично зареждане на tasks при логнат user
  useEffect(() => {
    if (user) {
      fetchTasks();
    }
  }, [user]);

  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      {!user ? (
        <Login onLogin={setUser} />
      ) : (
        <>
          <h2>Welcome {user.uNumber}</h2>
          <p>Email: {user.email}</p>
          <p>Roles: {user.roles.join(", ")}</p>
          <button onClick={logout}>Logout</button>

          {user.roles.includes("ADMIN") ? (
            <>
              <h3>Admin Panel</h3>
              <button onClick={() => alert("Create new Task!")}>
                Create Task
              </button>
            </>
          ) : (
            <h3>User Dashboard</h3>
          )}

          <h3>Tasks</h3>
          {loading ? (
            <p>Loading tasks...</p>
          ) : error ? (
            <p style={{ color: "red" }}>Error: {error}</p>
          ) : tasks.length > 0 ? (
            <ul>
              {tasks.map((task) => (
                <li key={task.id}>
                  {task.name} - {task.status}
                </li>
              ))}
            </ul>
          ) : (
            <p>No tasks available.</p>
          )}
        </>
      )}
    </div>
  );
}

export default App;
