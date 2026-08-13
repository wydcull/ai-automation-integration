import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { getAllEmails, type EmailTriage } from "../api/triage";
import { getGmailConnectUrl, getGmailStatus } from "../api/gmail";

function statusLabel(email: EmailTriage): string {
  if (email.replySent) return "Sent";
  if (email.rejected) return "Rejected";
  if (email.approved) return "Approved";
  return "Pending";
}

function statusColor(status: string): string {
  switch (status) {
    case "Sent":
      return "#166534";
    case "Approved":
      return "#1d4ed8";
    case "Rejected":
      return "#b91c1c";
    default:
      return "#a16207";
  }
}

export default function InboxPage() {
  const navigate = useNavigate();
  const [emails, setEmails] = useState<EmailTriage[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("ALL");
  const [priorityFilter, setPriorityFilter] = useState("ALL");
  const [gmailConnected, setGmailConnected] = useState(false);
  const [gmailEmail, setGmailEmail] = useState("");
  const [gmailBusy, setGmailBusy] = useState(false);

  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");

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

  function logout() {
    localStorage.clear();
    window.location.href = "/login";
  }

  if (loading) return <p style={{ padding: 24 }}>Loading inbox...</p>;
  if (error) return <p style={{ padding: 24, color: "crimson" }}>{error}</p>;

  return (
    <div style={{ padding: 24, fontFamily: "sans-serif" }}>
      <header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: 20,
        }}
      >
        <div>
          <h1 style={{ margin: 0 }}>Inbox</h1>
          <p style={{ margin: "4px 0 0", color: "#555" }}>
            {username} ({role}) · {filtered.length} emails
          </p>
        </div>
       <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
  {gmailConnected ? (
    <>
      <span style={{ color: "#166534", fontSize: 14 }}>
        Gmail: {gmailEmail || "connected"}
      </span>
      <Link to="/gmail">Gmail settings</Link>
    </>
  ) : (
    <button disabled={gmailBusy} onClick={connectGmail}>
      {gmailBusy ? "Opening Google..." : "Connect Gmail"}
    </button>
  )}
  <button onClick={logout}>Logout</button>
</div>
      </header>

      <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
        <label>
          Category{" "}
          <select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
            {categories.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </label>
        <label>
          Priority{" "}
          <select value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)}>
            <option value="ALL">ALL</option>
            <option value="HIGH">HIGH</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="LOW">LOW</option>
          </select>
        </label>
      </div>

      {filtered.length === 0 ? (
        <p>No emails found. Process some via Gmail fetch or manual triage.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ textAlign: "left", borderBottom: "2px solid #ddd" }}>
              <th style={{ padding: 8 }}>Subject</th>
              <th style={{ padding: 8 }}>From</th>
              <th style={{ padding: 8 }}>Category</th>
              <th style={{ padding: 8 }}>Priority</th>
              <th style={{ padding: 8 }}>Status</th>
              <th style={{ padding: 8 }}>Processed</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((email) => {
              const status = statusLabel(email);
              return (
                <tr
                  key={email.id}
                  onClick={() => navigate(`/emails/${email.id}`)}
                  style={{ borderBottom: "1px solid #eee", cursor: "pointer" }}
                >
                  <td style={{ padding: 8 }}>
                    <Link
                      to={`/emails/${email.id}`}
                      onClick={(e) => e.stopPropagation()}
                      style={{ color: "inherit", textDecoration: "none", fontWeight: 600 }}
                    >
                      {email.subject}
                    </Link>
                    <div style={{ fontSize: 12, color: "#666" }}>{email.summary}</div>
                  </td>
                  <td style={{ padding: 8 }}>{email.senderEmail}</td>
                  <td style={{ padding: 8 }}>{email.category}</td>
                  <td style={{ padding: 8 }}>{email.priority}</td>
                  <td style={{ padding: 8, color: statusColor(status), fontWeight: 600 }}>
                    {status}
                  </td>
                  <td style={{ padding: 8 }}>
                    {email.processedAt
                      ? new Date(email.processedAt).toLocaleString()
                      : "-"}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}