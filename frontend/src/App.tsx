import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import MfaVerifyPage from "./pages/MfaVerifyPage";
import DashboardPage from "./pages/DashboardPage";
import UsersPage from "./pages/UsersPage";
import DevicesPage from "./pages/DevicesPage";
import BehaviorPage from "./pages/BehaviorPage";
import RiskPage from "./pages/RiskPage";
import PoliciesPage from "./pages/PoliciesPage";
import AuditPage from "./pages/AuditPage";
import ReportsPage from "./pages/ReportsPage";
import SettingsPage from "./pages/SettingsPage";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/mfa-verify" element={<MfaVerifyPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/users" element={<UsersPage />} />
      <Route path="/devices" element={<DevicesPage />} />
      <Route path="/behavior" element={<BehaviorPage />} />
      <Route path="/risk" element={<RiskPage />} />
      <Route path="/policies" element={<PoliciesPage />} />
      <Route path="/audit" element={<AuditPage />} />
      <Route path="/reports" element={<ReportsPage />} />
      <Route path="/settings" element={<SettingsPage />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
