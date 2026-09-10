export default function Dashboard() {
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  function handleLogout() {
    localStorage.removeItem('user');
    window.location.href = '/login';
  }

  return (
    <div>
      <h1>Dashboard</h1>
      <p>Welcome, {user.fullName || user.username}</p>
      <p>Role: {user.role}</p>
      <button type="button" onClick={handleLogout}>
        Logout
      </button>
    </div>
  );
}