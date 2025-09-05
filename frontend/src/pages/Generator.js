function Generator({ user }) {
  return (
    <div style={{ padding: "20px" }}>
      <h2>Generator ({user.roles.includes("ADMIN") ? "Admin" : "User"})</h2>
    </div>
  );
}

export default Generator;
