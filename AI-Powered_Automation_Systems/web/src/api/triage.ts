import { api } from "./client";

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

export function getAllEmails() {
  return api<EmailTriage[]>("/api/automation/email-triage");
}

export function getEmailById(id: number) {
  return api<EmailTriage>(`/api/automation/email-triage/${id}`);
}