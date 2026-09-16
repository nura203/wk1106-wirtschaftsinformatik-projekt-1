const API_URL = "http://localhost:8080/api/v1";

export interface Task {
  id: string;
  title: string;
  description: string | null;
  type: "EXAM" | "ASSIGNMENT" | "GOAL";
  subject: string | null;
  deadline: string;
  estimatedHours: number;
  actualHours: number;
  progressPercent: number;
  status: "OPEN" | "IN_PROGRESS" | "DONE";
  weight: number;
  urgency: "RED" | "YELLOW" | "GREEN";
  subtasks: Subtask[];
  createdAt: string;
  updatedAt: string;
}

export interface Subtask {
  id: string;
  title: string;
  done: boolean;
  sortOrder: number;
}

export interface CreateTaskRequest {
  title: string;
  type: "EXAM" | "ASSIGNMENT" | "GOAL";
  deadline: string;
  description?: string;
  subject?: string;
  estimatedHours?: number;
  weight?: number;
}

export interface UpdateProgressRequest {
  progressPercent: number;
  sessionDurationMinutes?: number;
  notes?: string;
}

export interface LearningSession {
  id: string;
  durationMinutes: number;
  notes: string | null;
  recordedAt: string;
}

export interface PlanTask {
  taskId: string;
  title: string;
  recommendedMinutes: number;
  urgency: "RED" | "YELLOW" | "GREEN";
}

export interface WeekEntry {
  date: string;
  tasks: PlanTask[];
}

export interface LearningPlan {
  generatedAt: string;
  weekEntries: WeekEntry[];
}

export interface AuthResponse {
  token: string;
  userId?: string;
  username?: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface Reminder {
  id: string;
  taskId: string;
  daysBefore: number;
  channel: "IN_APP" | "EMAIL" | "BOTH";
  active: boolean;
  lastSentAt: string | null;
}

export interface ApiError {
  status?: number;
  error?: string;
  message?: string;
  timestamp?: string;
}

async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const token = localStorage.getItem("token");

  const headers = new Headers(options.headers);

  if (options.body) {
    headers.set("Content-Type", "application/json");
  }

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");

    if (window.location.pathname !== "/login") {
      window.location.href = "/login";
    }

    throw new Error("Authentifizierung erforderlich");
  }

  if (!response.ok) {
    let errorMessage = `Serverfehler (${response.status})`;

    try {
      const data = (await response.json()) as ApiError;

      if (typeof data.message === "string") {
        errorMessage = data.message;
      } else if (typeof data.error === "string") {
        errorMessage = data.error;
      }
    } catch {
      // Keine JSON-Fehlermeldung vorhanden.
    }

    throw new Error(errorMessage);
  }

  if (response.status === 204) {
    return null as T;
  }

  return (await response.json()) as T;
}

export async function register(
  request: RegisterRequest,
): Promise<AuthResponse> {
  return apiFetch<AuthResponse>("/auth/register", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function login(request: LoginRequest): Promise<AuthResponse> {
  return apiFetch<AuthResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function changePassword(password: string): Promise<void> {
  return apiFetch<void>("/auth/password", {
    method: "PUT",
    body: JSON.stringify({
      password,
    }),
  });
}

export async function getTasks(): Promise<Task[]> {
  return apiFetch<Task[]>("/tasks");
}

export async function getTask(taskId: string): Promise<Task> {
  return apiFetch<Task>(`/tasks/${taskId}`);
}

export async function createTask(request: CreateTaskRequest): Promise<Task> {
  return apiFetch<Task>("/tasks", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function updateTask(
  taskId: string,
  request: CreateTaskRequest,
): Promise<Task> {
  return apiFetch<Task>(`/tasks/${taskId}`, {
    method: "PUT",
    body: JSON.stringify(request),
  });
}

export async function deleteTask(taskId: string): Promise<void> {
  return apiFetch<void>(`/tasks/${taskId}`, {
    method: "DELETE",
  });
}

export async function updateProgress(
  taskId: string,
  request: UpdateProgressRequest,
): Promise<Task> {
  return apiFetch<Task>(`/tasks/${taskId}/progress`, {
    method: "PATCH",
    body: JSON.stringify(request),
  });
}

export async function getLearningSessions(
  taskId: string,
): Promise<LearningSession[]> {
  return apiFetch<LearningSession[]>(`/tasks/${taskId}/learning-sessions`);
}

export async function getLearningPlan(): Promise<LearningPlan> {
  return apiFetch<LearningPlan>("/plan");
}

export async function recalculateLearningPlan(): Promise<LearningPlan> {
  return apiFetch<LearningPlan>("/plan/recalculate", {
    method: "POST",
  });
}

export async function getSubtasks(taskId: string): Promise<Subtask[]> {
  return apiFetch<Subtask[]>(`/tasks/${taskId}/subtasks`);
}

export async function createSubtask(
  taskId: string,
  title: string,
  done = false,
  sortOrder = 0,
): Promise<Subtask> {
  return apiFetch<Subtask>(`/tasks/${taskId}/subtasks`, {
    method: "POST",
    body: JSON.stringify({
      title,
      done,
      sortOrder,
    }),
  });
}

export async function updateSubtask(
  taskId: string,
  subtaskId: string,
  title: string,
  done: boolean,
  sortOrder: number,
): Promise<Subtask> {
  return apiFetch<Subtask>(`/tasks/${taskId}/subtasks/${subtaskId}`, {
    method: "PUT",
    body: JSON.stringify({
      title,
      done,
      sortOrder,
    }),
  });
}

export async function deleteSubtask(
  taskId: string,
  subtaskId: string,
): Promise<void> {
  return apiFetch<void>(`/tasks/${taskId}/subtasks/${subtaskId}`, {
    method: "DELETE",
  });
}

export async function getReminders(taskId: string): Promise<Reminder[]> {
  return apiFetch<Reminder[]>(`/tasks/${taskId}/reminders`);
}

export async function createReminder(
  taskId: string,
  daysBefore: number,
  channel: Reminder["channel"],
): Promise<Reminder> {
  const params = new URLSearchParams({
    daysBefore: String(daysBefore),
    channel,
  });

  return apiFetch<Reminder>(`/tasks/${taskId}/reminders?${params.toString()}`, {
    method: "POST",
  });
}

export async function updateReminder(
  taskId: string,
  reminderId: string,
  daysBefore: number,
  channel: Reminder["channel"],
  active: boolean,
): Promise<Reminder> {
  const params = new URLSearchParams({
    daysBefore: String(daysBefore),
    channel,
    active: String(active),
  });

  return apiFetch<Reminder>(
    `/tasks/${taskId}/reminders/${reminderId}?${params.toString()}`,
    {
      method: "PUT",
    },
  );
}

export async function deleteReminder(
  taskId: string,
  reminderId: string,
): Promise<void> {
  return apiFetch<void>(`/tasks/${taskId}/reminders/${reminderId}`, {
    method: "DELETE",
  });
}

export async function downloadCalendarExport(): Promise<void> {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error("Authentifizierung erforderlich");
  }

  const response = await fetch(`${API_URL}/export/ical`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");

    if (window.location.pathname !== "/login") {
      window.location.href = "/login";
    }

    throw new Error("Authentifizierung erforderlich");
  }

  if (!response.ok) {
    throw new Error(`Kalenderexport fehlgeschlagen (${response.status})`);
  }

  const blob = await response.blob();

  const url = window.URL.createObjectURL(blob);

  const link = document.createElement("a");
  link.href = url;
  link.download = "study-planer.ics";

  document.body.appendChild(link);
  link.click();
  link.remove();

  window.URL.revokeObjectURL(url);
}
