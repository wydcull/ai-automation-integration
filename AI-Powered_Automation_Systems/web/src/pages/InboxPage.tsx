import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getEmails, type EmailTriageListItem } from "../api/triage";
import { getGmailConnectUrl, getGmailStatus } from "../api/gmail";

function statusLabel(email: EmailTriageListItem): string {
  if (email.replySent) return "Sent";
  if (email.rejected) return "Rejected";
  if (email.approved) return "Approved";
  return "Pending";
}

const CATEGORIES = [
  "ALL",
  "BILLING",
  "TECHNICAL",
  "SALES",
  "COMPLAINT",
  "ORDER_STATUS",
  "GENERAL",
  "ENQUIRY",
  "JOB_APPLICATION",
];

const PAGE_SIZE = 20;

export default function InboxPage() {
  const [emails, setEmails] = useState<EmailTriageListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("ALL");
  const [priorityFilter, setPriorityFilter] = useState("ALL");
  const [gmailConnected, setGmailConnected] = useState(false);
  const [gmailEmail, setGmailEmail] = useState("");
  const [gmailBusy, setGmailBusy] = useState(false);

  // Spring uses 0-based page numbers
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Fetch one page from backend whenever page/filters change
  useEffect(() => {
    setLoading(true);
    setError("");

    getEmails({
      page,
      size: PAGE_SIZE,
      category: categoryFilter,
      priority: priorityFilter,
    })
      .then((data) => {
        setEmails(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      })
      .catch((err) =>
        setError(err instanceof Error ? err.message : "Failed to load")
      )
      .finally(() => setLoading(false));
  }, [page, categoryFilter, priorityFilter]);

  // Reset to first page when filters change
  // useEffect(() => {
  //   setPage(0);
  // }, [categoryFilter, priorityFilter]);

  useEffect(() => {
    getGmailStatus()
      .then((info) => {
        setGmailConnected(info.status === "connected");
        setGmailEmail(info.email || "");
      })
      .catch(() => setGmailConnected(false));
  }, []);

  async function connectGmail() {
    setGmailBusy(true);
    try {
      const { authUrl } = await getGmailConnectUrl();
      window.location.href = authUrl;
    } catch (err) {
      setGmailBusy(false);
      setError(
        err instanceof Error ? err.message : "Failed to start Gmail connect"
      );
    }
  }

  if (loading) return <p className="muted">Loading inbox...</p>;

  return (
    <div>
      <h1 className="page-title">Inbox</h1>
      <p className="muted">
        {totalElements} conversations
        {totalPages > 0 && ` · page ${page + 1} of ${totalPages}`}
      </p>

      {error && (
        <p className="error" style={{ marginTop: 12 }}>
          {error}
        </p>
      )}

      <div className="toolbar">
        <select
          className="select"
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
        >
          {CATEGORIES.map((c) => (
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
          <button
            className="btn btn-gold"
            disabled={gmailBusy}
            onClick={connectGmail}
          >
            {gmailBusy ? "Opening Google..." : "Connect Gmail"}
          </button>
        )}
      </div>

      {emails.length === 0 ? (
        <div className="card card-pad">
          <p className="muted">
            No emails found. Connect Gmail and fetch unread mail, or process a
            message from the detail flow.
          </p>
        </div>
      ) : (
        <div className="card">
          {emails.map((email) => {
            const status = statusLabel(email);
            return (
              <Link
                key={email.id}
                to={`/emails/${email.id}`}
                className="email-row"
              >
                <div>
                  <div className="subject">{email.subject}</div>
                  <div className="summary">{email.summary}</div>
                </div>
                <div className="muted">{email.senderEmail}</div>
                <span className="badge badge-pending">{email.category}</span>
                <span
                  className={`badge badge-${(email.priority || "low").toLowerCase()}`}
                >
                  {email.priority}
                </span>
                <span className={`badge badge-${status.toLowerCase()}`}>
                  {status}
                </span>
                <div className="muted">
                  {email.processedAt
                    ? new Date(email.processedAt).toLocaleString()
                    : "—"}
                </div>
              </Link>
            );
          })}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pager">
          <button
            className="btn btn-ghost"
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            Previous
          </button>

          <span className="muted" style={{ alignSelf: "center" }}>
            Page {page + 1} of {totalPages}
          </span>

          <button
            className="btn btn-ghost"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}