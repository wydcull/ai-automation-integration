import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getAllEmails, type EmailTriage } from "../api/triage";
import { getGmailConnectUrl, getGmailStatus } from "../api/gmail";

function statusLabel(email: EmailTriage): string {
  if (email.replySent) return "Sent";
  if (email.rejected) return "Rejected";
  if (email.approved) return "Approved";
  return "Pending";
}

export default function InboxPage() {
  const [emails, setEmails] = useState<EmailTriage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("ALL");
  const [priorityFilter, setPriorityFilter] = useState("ALL");
  const [gmailConnected, setGmailConnected] = useState(false);
  const [gmailEmail, setGmailEmail] = useState("");
  const [gmailBusy, setGmailBusy] = useState(false);

  useEffect(() => {
    getAllEmails()
      .then(setEmails)
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load"))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    getGmailStatus()
      .then((info) => {
        setGmailConnected(info.status === "connected");
        setGmailEmail(info.email || "");
      })
      .catch(() => {
        setGmailConnected(false);
      });
  }, []);

  async function connectGmail() {
    setGmailBusy(true);
    try {
      const { authUrl } = await getGmailConnectUrl();
      window.location.href = authUrl;
    } catch (err) {
      setGmailBusy(false);
      setError(err instanceof Error ? err.message : "Failed to start Gmail connect");
    }
  }

  const categories = useMemo(
    () => ["ALL", ...Array.from(new Set(emails.map((e) => e.category).filter(Boolean)))],
    [emails]
  );

  const filtered = emails.filter((e) => {
    const catOk = categoryFilter === "ALL" || e.category === categoryFilter;
    const priOk = priorityFilter === "ALL" || e.priority === priorityFilter;
    return catOk && priOk;
  });

  if (loading) return <p className="muted">Loading inbox...</p>;

  return (
    <div>
      <h1 className="page-title">Inbox</h1>
      <p className="muted">{filtered.length} conversations</p>

      {error && <p className="error" style={{ marginTop: 12 }}>{error}</p>}

      <div className="toolbar">
        <select
          className="select"
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
        >
          {categories.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>
        <select
          className="select"
          value={priorityFilter}
          onChange={(e) => setPriorityFilter(e.target.value)}
        >
          <option value="ALL">ALL</option>
          <option value="HIGH">HIGH</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="LOW">LOW</option>
        </select>
        {gmailConnected ? (
          <span className="muted">Gmail · {gmailEmail}</span>
        ) : (
          <button className="btn btn-gold" disabled={gmailBusy} onClick={connectGmail}>
            {gmailBusy ? "Opening Google..." : "Connect Gmail"}
          </button>
        )}
      </div>

      {filtered.length === 0 ? (
        <div className="card card-pad">
          <p className="muted">No emails found. Connect Gmail and fetch unread mail, or process a message from the detail flow.</p>
        </div>
      ) : (
        <div className="card">
          {filtered.map((email) => {
            const status = statusLabel(email);
            return (
              <Link key={email.id} to={`/emails/${email.id}`} className="email-row">
                <div>
                  <div className="subject">{email.subject}</div>
                  <div className="summary">{email.summary}</div>
                </div>
                <div className="muted">{email.senderEmail}</div>
                <span className="badge badge-pending">{email.category}</span>
                <span className={`badge badge-${(email.priority || "low").toLowerCase()}`}>
                  {email.priority}
                </span>
                <span className={`badge badge-${status.toLowerCase()}`}>{status}</span>
                <div className="muted">
                  {email.processedAt ? new Date(email.processedAt).toLocaleString() : "—"}
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
