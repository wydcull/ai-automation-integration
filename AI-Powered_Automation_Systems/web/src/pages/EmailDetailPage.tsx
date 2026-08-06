import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getEmailById, type EmailTriage } from "../api/triage";
import {
  approveAndSend,
  approveReply,
  editDraftReply,
  rejectReply,
  sendReply,
} from "../api/replies";

function canApprove() {
  const role = localStorage.getItem("role");
  return role === "ADMIN" || role === "APPROVER";
}

function statusLabel(email: EmailTriage): string {
  if (email.replySent) return "Sent";
  if (email.rejected) return "Rejected";
  if (email.approved) return "Approved";
  return "Pending";
}

export default function EmailDetailPage() {
  const { id } = useParams();
  const emailId = Number(id);

  const [email, setEmail] = useState<EmailTriage | null>(null);
  const [draft, setDraft] = useState("");
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const approver = canApprove();

  async function load() {
    setLoading(true);
    setError("");
    try {
      const data = await getEmailById(emailId);
      setEmail(data);
      setDraft(data.draftReply || "");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load email");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (!Number.isFinite(emailId)) {
      setError("Invalid email id");
      setLoading(false);
      return;
    }
    load();
  }, [emailId]);

  async function runAction(action: () => Promise<unknown>, successText: string) {
    setBusy(true);
    setMessage("");
    setError("");
    try {
      await action();
      setMessage(successText);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Action failed");
    } finally {
      setBusy(false);
    }
  }

  if (loading) return <p style={{ padding: 24 }}>Loading email...</p>;
  if (error && !email) {
    return (
      <div style={{ padding: 24, fontFamily: "sans-serif" }}>
        <p style={{ color: "crimson" }}>{error}</p>
        <Link to="/">← Back to Inbox</Link>
      </div>
    );
  }
  if (!email) return null;

  const status = statusLabel(email);
  const alreadySent = !!email.replySent;
  const alreadyRejected = !!email.rejected;

  return (
    <div style={{ padding: 24, fontFamily: "sans-serif", maxWidth: 900 }}>
      <p>
        <Link to="/">← Back to Inbox</Link>
      </p>

      <h1 style={{ marginBottom: 4 }}>{email.subject}</h1>
      <p style={{ color: "#555", marginTop: 0 }}>
        From: {email.senderEmail} · {email.category} · {email.priority} ·{" "}
        <strong>{status}</strong>
      </p>

      {message && <p style={{ color: "#166534" }}>{message}</p>}
      {error && <p style={{ color: "crimson" }}>{error}</p>}

      <section style={{ marginBottom: 20 }}>
        <h3>Summary</h3>
        <p>{email.summary || "-"}</p>
      </section>

      <section style={{ marginBottom: 20 }}>
        <h3>Original email</h3>
        <pre
          style={{
            whiteSpace: "pre-wrap",
            background: "#f6f6f6",
            padding: 12,
            borderRadius: 6,
          }}
        >
          {email.body || "(no body)"}
        </pre>
      </section>

      {email.extractedData && Object.keys(email.extractedData).length > 0 && (
        <section style={{ marginBottom: 20 }}>
          <h3>Extracted from email</h3>
          <ul>
            {Object.entries(email.extractedData).map(([k, v]) => (
              <li key={k}>
                <strong>{k}:</strong> {String(v)}
              </li>
            ))}
          </ul>
        </section>
      )}

      {email.documentFileName && (
        <section style={{ marginBottom: 20 }}>
          <h3>Document: {email.documentFileName}</h3>
          {email.documentExtractedData &&
          Object.keys(email.documentExtractedData).length > 0 ? (
            <ul>
              {Object.entries(email.documentExtractedData).map(([k, v]) => (
                <li key={k}>
                  <strong>{k}:</strong> {String(v)}
                </li>
              ))}
            </ul>
          ) : (
            <p>No document fields extracted.</p>
          )}
        </section>
      )}

      <section style={{ marginBottom: 20 }}>
        <h3>Draft reply</h3>
        <textarea
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          readOnly={!approver || alreadySent || alreadyRejected}
          rows={10}
          style={{ width: "100%", padding: 10, fontFamily: "inherit" }}
        />
      </section>

      {approver && !alreadySent && !alreadyRejected && (
        <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
          <button
            disabled={busy || !draft.trim()}
            onClick={() =>
              runAction(() => editDraftReply(email.id, draft), "Draft saved")
            }
          >
            Save draft
          </button>

          <button
            disabled={busy}
            onClick={() => runAction(() => approveReply(email.id), "Approved")}
          >
            Approve
          </button>

          <button
            disabled={busy}
            onClick={() => {
              const reason = window.prompt("Rejection reason (optional):") || "";
              runAction(() => rejectReply(email.id, reason), "Rejected");
            }}
          >
            Reject
          </button>

          {email.approved && (
            <button
              disabled={busy}
              onClick={() => runAction(() => sendReply(email.id), "Reply sent")}
            >
              Send
            </button>
          )}

          <button
            disabled={busy}
            onClick={() =>
              runAction(() => approveAndSend(email.id), "Approved and sent")
            }
          >
            Approve & Send
          </button>
        </div>
      )}

      {!approver && (
        <p style={{ color: "#666" }}>View-only (your role cannot approve/send).</p>
      )}

      {email.approvedBy && (
        <p style={{ marginTop: 16, color: "#555" }}>
          Approved by {email.approvedBy}
          {email.approvedAt ? ` at ${new Date(email.approvedAt).toLocaleString()}` : ""}
        </p>
      )}
      {email.rejectionReason && (
        <p style={{ color: "#b91c1c" }}>Rejection reason: {email.rejectionReason}</p>
      )}
    </div>
  );
}