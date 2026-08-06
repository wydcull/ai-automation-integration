import { api } from "./client";

export function approveReply(id: number) {
  return api<{ status: string; message: string }>(`/api/replies/approve/${id}`, {
    method: "POST",
  });
}

export function rejectReply(id: number, reason?: string) {
  const q = reason ? `?reason=${encodeURIComponent(reason)}` : "";
  return api<{ status: string; message: string }>(`/api/replies/reject/${id}${q}`, {
    method: "POST",
  });
}

export function editDraftReply(id: number, draftReply: string) {
  return api<{ status: string; message: string }>(`/api/replies/edit/${id}`, {
    method: "PUT",
    body: JSON.stringify({ draftReply }),
  });
}

export function approveAndSend(id: number) {
  return api<{ status: string; message: string; gmailMessageId?: string }>(
    `/api/replies/approve-and-send/${id}`,
    { method: "POST" }
  );
}

export function sendReply(id: number) {
  return api<{ status: string; message: string }>(`/api/replies/send/${id}`, {
    method: "POST",
  });
}