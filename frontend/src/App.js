import { useEffect, useState } from "react";
import logo from './logo.svg';
import './App.css';

function App() {
  const [data, setData] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/tasks/status", {
      method: "GET",
      credentials: "include", //cookies
      headers: {
        "Content-Type": "application/json"
      }
    })
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! Status: ${res.status}`);
        }
        return res.json();
      })
      .then(data => setData(data))
      .catch(err => console.error("Error: ", err));
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h1>React + Spring Boot</h1>
        <pre>{data ? JSON.stringify(data, null, 2) : "Hello from spring boot..."}</pre>
      </header>
    </div>
  );
}

export default App;
