import { Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import InboxPage from "./pages/InboxPage";
import EmailDetailPage from "./pages/EmailDetailPage";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <InboxPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/emails/:id"
        element={
          <ProtectedRoute>
            <EmailDetailPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}