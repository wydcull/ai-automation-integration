import { Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./components/ProtectedRoute";
import AppLayout from "./components/AppLayout";
import LoginPage from "./pages/LoginPage";
import InboxPage from "./pages/InboxPage";
import EmailDetailPage from "./pages/EmailDetailPage";
import GmailPage from "./pages/GmailPage";

function Shell({ children }: { children: React.ReactNode }) {
  return (
    <ProtectedRoute>
      <AppLayout>{children}</AppLayout>
    </ProtectedRoute>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <Shell>
            <InboxPage />
          </Shell>
        }
      />
      <Route
        path="/gmail"
        element={
          <Shell>
            <GmailPage />
          </Shell>
        }
      />
      <Route
        path="/emails/:id"
        element={
          <Shell>
            <EmailDetailPage />
          </Shell>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
