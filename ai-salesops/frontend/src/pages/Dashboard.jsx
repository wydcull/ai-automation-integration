import { useEffect, useState } from 'react';
import {
  fetchDashboardSummary,
  fetchEmailStats,
  fetchPendingReview,
  fetchSalesSummary,
  fetchUserSummary,
} from '../api/dashboard';
import '../Dashboard.css';

const NAV = [
  { id: 'dashboard', label: 'Dashboard' },
  { id: 'leads', label: 'Leads' },
  { id: 'tasks', label: 'Tasks' },
  { id: 'users', label: 'Users' },
  { id: 'products', label: 'Products' },
  { id: 'rules', label: 'Rules' },
  { id: 'ai-logs', label: 'AI Logs' },
];

function NavIcon({ id }) {
  const common = {
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 2,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    className: 'nav-icon',
    'aria-hidden': true,
  };

  switch (id) {
    case 'dashboard':
      return (
        <svg {...common}>
          <rect x="3" y="3" width="7" height="9" rx="1" />
          <rect x="14" y="3" width="7" height="5" rx="1" />
          <rect x="14" y="12" width="7" height="9" rx="1" />
          <rect x="3" y="16" width="7" height="5" rx="1" />
        </svg>
      );
    case 'leads':
      return (
        <svg {...common}>
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        </svg>
      );
    case 'tasks':
      return (
        <svg {...common}>
          <path d="M9 11l3 3L22 4" />
          <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
        </svg>
      );
    case 'users':
      return (
        <svg {...common}>
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
          <circle cx="12" cy="7" r="4" />
        </svg>
      );
    case 'products':
      return (
        <svg {...common}>
          <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
          <path d="M3.3 7L12 12l8.7-5" />
          <path d="M12 22V12" />
        </svg>
      );
    case 'rules':
      return (
        <svg {...common}>
          <path d="M12 3v18" />
          <path d="M5 8h14" />
          <path d="M5 16h10" />
        </svg>
      );
    default:
      return (
        <svg {...common}>
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <path d="M14 2v6h6" />
          <path d="M16 13H8" />
          <path d="M16 17H8" />
        </svg>
      );
  }
}

function formatConfidence(value) {
  if (value == null) return '—';
  const n = Number(value);
  if (Number.isNaN(n)) return String(value);
  return n <= 1 ? `${Math.round(n * 100)}%` : `${Math.round(n)}%`;
}

export default function Dashboard() {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const [activeNav, setActiveNav] = useState('dashboard');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [summary, setSummary] = useState(null);
  const [users, setUsers] = useState(null);
  const [emailStats, setEmailStats] = useState(null);
  const [sales, setSales] = useState(null);
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError('');
      try {
        const [summaryData, userData, emailData, salesData, reviewData] =
          await Promise.all([
            fetchDashboardSummary(),
            fetchUserSummary(),
            fetchEmailStats(),
            fetchSalesSummary(),
            fetchPendingReview(),
          ]);
        if (cancelled) return;
        setSummary(summaryData);
        setUsers(userData);
        setEmailStats(emailData);
        setSales(salesData);
        setReviews(Array.isArray(reviewData) ? reviewData : []);
      } catch {
        if (!cancelled) {
          setError('Could not load dashboard data. Is the backend running?');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  function handleLogout() {
    localStorage.removeItem('user');
    window.location.href = '/login';
  }

  const initial = (user.fullName || user.email || 'A').charAt(0).toUpperCase();

  const pipeline = [
    { key: 'received', label: 'Received', count: emailStats?.received ?? 0 },
    { key: 'processing', label: 'Processing', count: emailStats?.processing ?? 0 },
    { key: 'processed', label: 'Processed', count: emailStats?.processed ?? 0 },
    {
      key: 'failed',
      label: 'Failed',
      count: emailStats?.failed ?? summary?.aiFailed ?? 0,
      fail: true,
    },
  ];

  return (
    <div className="dash">
      <aside className="dash-sidebar">
        <p className="dash-brand">AI SalesOps</p>
        <nav className="dash-nav" aria-label="Main">
          {NAV.map((item) => (
            <button
              key={item.id}
              type="button"
              className={activeNav === item.id ? 'active' : ''}
              onClick={() => setActiveNav(item.id)}
            >
              <NavIcon id={item.id} />
              {item.label}
            </button>
          ))}
        </nav>
        <button type="button" className="dash-logout" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <main className="dash-main">
        <header className="dash-header">
          <div>
            <h1>Overview</h1>
            <p className="welcome">
              Welcome back, {user.fullName || user.email || 'Admin'}
              {user.role ? ` · ${user.role}` : ''}
            </p>
          </div>
          <div className="dash-avatar" title={user.fullName || user.email}>
            {initial}
          </div>
        </header>

        {error && <p className="dash-status error">{error}</p>}

        {loading ? (
          <div className="kpi-grid">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="skeleton" />
            ))}
          </div>
        ) : (
          <>
            <section className="kpi-grid" aria-label="Key metrics">
              <article className="kpi-card tone-ok">
                <p className="kpi-label">Active users</p>
                <p className="kpi-value">{summary?.activeUsers ?? 0}</p>
                <p className="kpi-meta">
                  {users?.managers ?? 0} mgr · {users?.representatives ?? 0} rep ·{' '}
                  {users?.admins ?? 0} admin
                </p>
              </article>
              <article className="kpi-card tone-ok">
                <p className="kpi-label">All leads</p>
                <p className="kpi-value">{summary?.allLeads ?? 0}</p>
              </article>
              <article className="kpi-card tone-ok">
                <p className="kpi-label">Products</p>
                <p className="kpi-value">{summary?.products ?? 0}</p>
              </article>
              <article className="kpi-card tone-ok">
                <p className="kpi-label">Processed emails</p>
                <p className="kpi-value">{summary?.processedEmails ?? 0}</p>
              </article>
              <article className="kpi-card tone-danger">
                <p className="kpi-label">AI failed</p>
                <p className="kpi-value">{summary?.aiFailed ?? 0}</p>
              </article>
              <article className="kpi-card tone-warn">
                <p className="kpi-label">Pending review</p>
                <p className="kpi-value">{summary?.pendingReview ?? 0}</p>
              </article>
            </section>

            <section className="panel pipeline-panel" aria-label="Email pipeline">
              <div className="pipeline-header">
                <h2 className="panel-title">AI / email pipeline health</h2>
                <div className="pipeline-actions">
                  <button type="button" className="btn btn-primary">
                    Retry all
                  </button>
                  <button type="button" className="btn btn-ghost">
                    Open AI Logs
                  </button>
                </div>
              </div>
              <div className="pipeline-steps">
                {pipeline.map((step) => (
                  <div
                    key={step.key}
                    className={`pipeline-step${step.fail ? ' is-fail' : ''}`}
                  >
                    <span className="step-label">{step.label}</span>
                    <span className="step-count">{step.count}</span>
                  </div>
                ))}
              </div>
              {(emailStats?.failed ?? 0) > 0 && (
                <p className="pipeline-fail-note">
                  FAILED: {emailStats.failed} — needs attention
                </p>
              )}
            </section>

            <div className="dash-bottom">
              <section className="panel" aria-label="Manual review">
                <h2 className="panel-title">Needs manual review</h2>
                {reviews.length === 0 ? (
                  <p className="empty-state">No items waiting for review.</p>
                ) : (
                  <div className="review-table-wrap">
                    <table className="review-table">
                      <thead>
                        <tr>
                          <th>Type</th>
                          <th>Item</th>
                          <th>Reason</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {reviews.map((row) => {
                          const conf = Number(row.confidence);
                          const low =
                            !Number.isNaN(conf) &&
                            (conf <= 1 ? conf < 0.8 : conf < 80);
                          return (
                            <tr key={`${row.leadId}-${row.fieldName}`}>
                              <td>
                                <span
                                  className={`type-badge${low ? ' low' : ''}`}
                                >
                                  {low ? 'Low confidence' : 'Review'}
                                </span>
                              </td>
                              <td>
                                {row.leadCode || `Lead #${row.leadId}`}
                                {row.companyName ? ` · ${row.companyName}` : ''}
                                {row.fieldName ? ` · ${row.fieldName}` : ''}
                              </td>
                              <td>{formatConfidence(row.confidence)}</td>
                              <td>
                                <button type="button" className="link-action">
                                  Review
                                </button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                )}
              </section>

              <div className="side-stack">
                <section className="panel">
                  <h2 className="panel-title">Config shortcuts</h2>
                  <ul className="shortcut-list">
                    <li>
                      <span className="label">Score bands</span>
                      <span className="value">HOT ≥ 80 · WARM ≥ 60 · COLD ≥ 40</span>
                    </li>
                    <li>
                      <span className="label">SLA</span>
                      <span className="value">HOT 4h · WARM 1d · COLD 3d</span>
                    </li>
                    <li>
                      <span className="label">Routing</span>
                      <span className="value">MH → Team A</span>
                    </li>
                  </ul>
                </section>

                <section className="panel">
                  <h2 className="panel-title">Sales snapshot</h2>
                  <ul className="snapshot-list">
                    <li>
                      <span className="label">Hot leads</span>
                      <span className="value">{sales?.hotLeads ?? 0}</span>
                    </li>
                    <li>
                      <span className="label">Open follow-ups</span>
                      <span className="value">{sales?.openFollowUps ?? 0}</span>
                    </li>
                    <li className={(sales?.unassignedLeads ?? 0) > 0 ? 'alert' : ''}>
                      <span className="label">Unassigned</span>
                      <span className="value">{sales?.unassignedLeads ?? 0}</span>
                    </li>
                    <li className={(sales?.overdueFollowUps ?? 0) > 0 ? 'alert' : ''}>
                      <span className="label">Overdue follow-ups</span>
                      <span className="value">{sales?.overdueFollowUps ?? 0}</span>
                    </li>
                  </ul>
                </section>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
