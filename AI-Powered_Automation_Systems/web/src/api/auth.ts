import { api, setAuth } from "./client";

export async function login(username: string, password: string) {
  const data = await api<{
    token: string;
    username: string;
    role: string;
    fullName: string;
  }>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
  setAuth(data.token, data.username, data.role, data.fullName);
  return data;
}