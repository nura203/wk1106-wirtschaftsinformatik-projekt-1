import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";

import DashboardPage from "./pages/DashboardPage";
import LearningPlanPage from "./pages/LearningPlanPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import SettingsPage from "./pages/SettingsPage";
import TaskCreatePage from "./pages/TaskCreatePage";
import TaskDetailPage from "./pages/TaskDetailPage";
import TaskEditPage from "./pages/TaskEditPage";
import TasksPage from "./pages/TasksPage";
import MainLayout from "./layouts/MainLayout";

function ProtectedLayout() {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return <MainLayout />;
}

function App() {
  const token = localStorage.getItem("token");

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={token ? <Navigate to="/" replace /> : <LoginPage />}
        />

        <Route
          path="/register"
          element={token ? <Navigate to="/" replace /> : <RegisterPage />}
        />

        <Route element={<ProtectedLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/tasks" element={<TasksPage />} />
          <Route path="/tasks/new" element={<TaskCreatePage />} />
          <Route path="/tasks/:id" element={<TaskDetailPage />} />
          <Route path="/tasks/:id/edit" element={<TaskEditPage />} />
          <Route path="/plan" element={<LearningPlanPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>

        <Route
          path="*"
          element={<Navigate to={token ? "/" : "/login"} replace />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;