const API_BASE = import.meta.env.VITE_API_BASE_URL;

export function getToken() {
  return localStorage.getItem("token");
}

export function setAuth(token: string, username: string, role: string, fullName: string) {
  localStorage.setItem("token", token);
  localStorage.setItem("username", username);
  localStorage.setItem("role", role);
  localStorage.setItem("fullName", fullName);
}

export function clearAuth() {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("role");
  localStorage.removeItem("fullName");
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers = new Headers(options.headers || {});
  if (token) headers.set("Authorization", `Bearer ${token}`);
  if (!(options.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  if (res.status === 401) {
    clearAuth();
    window.location.href = "/login";
    throw new Error("Unauthorized");
  }
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message || "Request failed");
  }
  return res.json();
}