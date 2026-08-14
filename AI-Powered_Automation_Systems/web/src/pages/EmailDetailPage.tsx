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

  if (loading) return <p className="muted">Loading email...</p>;
  if (error && !email) {
    return (
      <div>
        <p className="error">{error}</p>
        <Link to="/" className="muted">← Back to Inbox</Link>
      </div>
    );
  }
  if (!email) return null;

  const status = statusLabel(email);
  const alreadySent = !!email.replySent;
  const alreadyRejected = !!email.rejected;

  return (
    <div>
      <p className="muted" style={{ marginBottom: 12 }}>
        <Link to="/">← Inbox</Link>
      </p>

      <h1 className="page-title">{email.subject}</h1>
      <p className="muted" style={{ marginBottom: 16 }}>
        {email.senderEmail}
        {" · "}
        <span className="badge badge-pending">{email.category}</span>{" "}
        <span className={`badge badge-${(email.priority || "low").toLowerCase()}`}>
          {email.priority}
        </span>{" "}
        <span className={`badge badge-${status.toLowerCase()}`}>{status}</span>
      </p>

      {message && <p className="ok">{message}</p>}
      {error && <p className="error">{error}</p>}

      <section className="card card-pad">
        <h3>Summary</h3>
        <p>{email.summary || "—"}</p>
      </section>

      <section className="card card-pad">
        <h3>Original email</h3>
        <pre className="body-pre">{email.body || "(no body)"}</pre>
      </section>

      {email.extractedData && Object.keys(email.extractedData).length > 0 && (
        <section className="card card-pad">
          <h3>Extracted from email</h3>
          <ul className="kv-list">
            {Object.entries(email.extractedData).map(([k, v]) => (
              <li key={k}>
                <strong>{k}:</strong> {String(v)}
              </li>
            ))}
          </ul>
        </section>
      )}

      {email.documentFileName && (
        <section className="card card-pad">
          <h3>Document: {email.documentFileName}</h3>
          {email.documentExtractedData &&
          Object.keys(email.documentExtractedData).length > 0 ? (
            <ul className="kv-list">
              {Object.entries(email.documentExtractedData).map(([k, v]) => (
                <li key={k}>
                  <strong>{k}:</strong> {String(v)}
                </li>
              ))}
            </ul>
          ) : (
            <p className="muted">No document fields extracted.</p>
          )}
        </section>
      )}

      <section className="card card-pad">
        <h3>Draft reply</h3>
        <textarea
          className="textarea"
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          readOnly={!approver || alreadySent || alreadyRejected}
          rows={10}
          style={{ width: "100%", minHeight: 180 }}
        />
      </section>

      {approver && !alreadySent && !alreadyRejected && (
        <div className="actions">
          <button
            className="btn btn-ghost"
            disabled={busy || !draft.trim()}
            onClick={() =>
              runAction(() => editDraftReply(email.id, draft), "Draft saved")
            }
          >
            Save draft
          </button>
          <button
            className="btn"
            disabled={busy}
            onClick={() => runAction(() => approveReply(email.id), "Approved")}
          >
            Approve
          </button>
          <button
            className="btn btn-danger"
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
              className="btn btn-gold"
              disabled={busy}
              onClick={() => runAction(() => sendReply(email.id), "Reply sent")}
            >
              Send
            </button>
          )}
          <button
            className="btn btn-gold"
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
        <p className="muted">View-only (your role cannot approve/send).</p>
      )}

      {email.approvedBy && (
        <p className="muted" style={{ marginTop: 16 }}>
          Approved by {email.approvedBy}
          {email.approvedAt ? ` at ${new Date(email.approvedAt).toLocaleString()}` : ""}
        </p>
      )}
      {email.rejectionReason && (
        <p className="error">Rejection reason: {email.rejectionReason}</p>
      )}
    </div>
  );
}
