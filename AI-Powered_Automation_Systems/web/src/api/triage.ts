import { api } from "./client";

export type EmailTriageListItem = {
  id: number;
  senderEmail: string;
  subject: string;
  category: string;
  priority: string;
  summary: string;
  processedAt: string;
  approved?: boolean | null;
  rejected?: boolean | null;
  replySent?: boolean | null;
};

export type EmailTriage = {
  id: number;
  senderEmail: string;
  subject: string;
  body?: string;
  category: string;
  priority: string;
  summary: string;
  draftReply: string;
  extractedData?: Record<string, string>;
  documentFileName?: string;
  documentExtractedData?: Record<string, unknown>;
  processedAt: string;
  approved?: boolean | null;
  rejected?: boolean | null;
  replySent?: boolean | null;
  approvedBy?: string | null;
  approvedAt?: string | null;
  rejectionReason?: string | null;
};

export type EmailTriagePage = {
  content: EmailTriageListItem[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
};

export function getEmails(params: {
  page: number;
  size?: number;
  category?: string;
  priority?: string;
}) {
  const q = new URLSearchParams();
  q.set("page", String(params.page));
  q.set("size", String(params.size ?? 20));

  if (params.category && params.category !== "ALL") {
    q.set("category", params.category);
  }
  if (params.priority && params.priority !== "ALL") {
    q.set("priority", params.priority);
  }

  return api<EmailTriagePage>(`/api/automation/email-triage?${q.toString()}`);
}

export function getEmailById(id: number) {
  return api<EmailTriage>(`/api/automation/email-triage/${id}`);
}