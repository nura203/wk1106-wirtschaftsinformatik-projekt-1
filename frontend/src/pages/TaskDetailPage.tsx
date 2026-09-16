import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  createSubtask,
  deleteSubtask,
  deleteTask,
  getLearningSessions,
  getSubtasks,
  getTask,
  updateProgress,
  type LearningSession,
  type Subtask,
  type Task,
} from "../services/api";

function TaskDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [task, setTask] = useState<Task | null>(null);
  const [subtasks, setSubtasks] = useState<Subtask[]>([]);
  const [sessions, setSessions] = useState<LearningSession[]>([]);

  const [progress, setProgress] = useState(0);
  const [duration, setDuration] = useState(0);
  const [notes, setNotes] = useState("");
  const [newSubtaskTitle, setNewSubtaskTitle] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");

  async function loadData() {
    if (!id) {
      setError("Aufgabe wurde nicht gefunden.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const [taskData, subtaskData, sessionData] = await Promise.all([
        getTask(id),
        getSubtasks(id),
        getLearningSessions(id),
      ]);

      setTask(taskData);
      setSubtasks(subtaskData);
      setSessions(sessionData);
      setProgress(taskData.progressPercent);
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgabe konnte nicht geladen werden.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, [id]);

  async function handleProgressSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!id) {
      return;
    }

    if (progress < 0 || progress > 100) {
      setError("Fortschritt muss zwischen 0 und 100 liegen.");
      return;
    }

    if (duration <= 0) {
      setError("Lernzeit muss größer als 0 sein.");
      return;
    }

    setSaving(true);
    setError("");

    try {
      const updatedTask = await updateProgress(id, {
        progressPercent: progress,
        sessionDurationMinutes: duration,
        notes: notes.trim() || undefined,
      });

      const updatedSessions = await getLearningSessions(id);

      setTask(updatedTask);
      setSessions(updatedSessions);
      setDuration(0);
      setNotes("");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Fortschritt konnte nicht gespeichert werden.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleAddSubtask(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!id || !newSubtaskTitle.trim()) {
      return;
    }

    setError("");

    try {
      const subtask = await createSubtask(
        id,
        newSubtaskTitle.trim(),
        false,
        subtasks.length,
      );

      setSubtasks((current) => [...current, subtask]);

      setNewSubtaskTitle("");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Subtask konnte nicht erstellt werden.",
      );
    }
  }

  async function handleDeleteSubtask(subtaskId: string) {
    if (!id) {
      return;
    }

    try {
      await deleteSubtask(id, subtaskId);

      setSubtasks((current) =>
        current.filter((subtask) => subtask.id !== subtaskId),
      );
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Subtask konnte nicht gelöscht werden.",
      );
    }
  }

  async function handleDeleteTask() {
    if (!id) {
      return;
    }

    const confirmed = window.confirm(
      "Möchtest du diese Aufgabe wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.",
    );

    if (!confirmed) {
      return;
    }

    setDeleting(true);
    setError("");

    try {
      await deleteTask(id);
      navigate("/tasks", { replace: true });
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgabe konnte nicht gelöscht werden.",
      );
      setDeleting(false);
    }
  }

  function formatRecordedAt(recordedAt: string): string {
    const date = new Date(recordedAt);

    if (Number.isNaN(date.getTime())) {
      return recordedAt;
    }

    return new Intl.DateTimeFormat("de-DE", {
      dateStyle: "short",
      timeStyle: "short",
    }).format(date);
  }

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");

    navigate("/login", { replace: true });
  }

  if (loading) {
    return (
      <main className="dashboard">
        <section className="tasks-section">
          <p>Aufgabe wird geladen...</p>
        </section>
      </main>
    );
  }

  if (!task) {
    return (
      <main className="dashboard">
        <section className="tasks-section">
          <p className="error-message">
            {error || "Aufgabe wurde nicht gefunden."}
          </p>

          <Link to="/tasks" className="back-button">
            Zurück zu den Aufgaben
          </Link>
        </section>
      </main>
    );
  }

  const completedSubtasks = subtasks.filter((subtask) => subtask.done).length;

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>StudyPlanner</h1>
          <p>{task.title}</p>
        </div>

        <button type="button" className="logout-button" onClick={handleLogout}>
          Abmelden
        </button>
      </header>

      <section className="task-detail">
        <div className="section-header">
          <div>
            <h2>{task.title}</h2>

            {task.description && (
              <p className="detail-description">{task.description}</p>
            )}
          </div>

          <div>
            <Link to={`/tasks/${task.id}/edit`} className="back-button">
              Bearbeiten
            </Link>
          </div>
        </div>

        {error && <p className="error-message">{error}</p>}

        <div className="detail-grid">
          <div className="detail-item">
            <strong>Typ</strong>
            <span>{task.type}</span>
          </div>

          <div className="detail-item">
            <strong>Fach</strong>
            <span>{task.subject || "Kein Fach"}</span>
          </div>

          <div className="detail-item">
            <strong>Deadline</strong>
            <span>{task.deadline}</span>
          </div>

          <div className="detail-item">
            <strong>Dringlichkeit</strong>
            <span>{task.urgency}</span>
          </div>

          <div className="detail-item">
            <strong>Geschätzte Stunden</strong>
            <span>{task.estimatedHours}</span>
          </div>

          <div className="detail-item">
            <strong>Tatsächliche Stunden</strong>
            <span>{task.actualHours}</span>
          </div>

          <div className="detail-item">
            <strong>Gewichtung</strong>
            <span>{task.weight}</span>
          </div>

          <div className="detail-item">
            <strong>Status</strong>
            <span>{task.status}</span>
          </div>
        </div>

        <div className="detail-progress">
          <div className="progress-header">
            <strong>Fortschritt</strong>
            <span>{task.progressPercent}%</span>
          </div>

          <div className="progress-bar">
            <div
              className="progress-value"
              style={{
                width: `${Math.min(Math.max(task.progressPercent, 0), 100)}%`,
              }}
            />
          </div>
        </div>

        <div className="progress-form">
          <h3>Lernsession erfassen</h3>

          <form onSubmit={handleProgressSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="progress">Fortschritt (%)</label>

                <input
                  id="progress"
                  type="number"
                  min="0"
                  max="100"
                  value={progress}
                  onChange={(event) => setProgress(Number(event.target.value))}
                />
              </div>

              <div className="form-group">
                <label htmlFor="duration">Dauer (Minuten)</label>

                <input
                  id="duration"
                  type="number"
                  min="1"
                  value={duration}
                  onChange={(event) => setDuration(Number(event.target.value))}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="notes">Notizen</label>

              <textarea
                id="notes"
                value={notes}
                onChange={(event) => setNotes(event.target.value)}
              />
            </div>

            <button type="submit" disabled={saving}>
              {saving ? "Wird gespeichert..." : "Fortschritt speichern"}
            </button>
          </form>
        </div>

        <section className="subtasks-section">
          <div className="subtasks-header">
            <h3>Lernsession-Historie</h3>

            <span>{sessions.length} Session(s)</span>
          </div>

          {sessions.length === 0 ? (
            <p className="empty-plan">Noch keine Lernsessions erfasst.</p>
          ) : (
            <div className="subtask-list">
              {sessions.map((session) => (
                <div className="subtask-item" key={session.id}>
                  <div>
                    <strong>{session.durationMinutes} Minuten</strong>

                    <div>{formatRecordedAt(session.recordedAt)}</div>

                    {session.notes && <div>Notiz: {session.notes}</div>}
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>

        {task.type === "GOAL" && (
          <section className="subtasks-section">
            <div className="subtasks-header">
              <h3>Subtasks</h3>

              <span>
                {completedSubtasks} / {subtasks.length}
              </span>
            </div>

            <div className="subtask-list">
              {subtasks.map((subtask) => (
                <div className="subtask-item" key={subtask.id}>
                  <label>
                    <input type="checkbox" checked={subtask.done} readOnly />

                    <span className={subtask.done ? "subtask-done" : ""}>
                      {subtask.title}
                    </span>
                  </label>

                  <button
                    type="button"
                    className="delete-subtask"
                    onClick={() => void handleDeleteSubtask(subtask.id)}
                  >
                    Löschen
                  </button>
                </div>
              ))}
            </div>

            <form className="subtask-form" onSubmit={handleAddSubtask}>
              <input
                type="text"
                value={newSubtaskTitle}
                onChange={(event) => setNewSubtaskTitle(event.target.value)}
                placeholder="Neue Subtask"
              />

              <button type="submit" disabled={!newSubtaskTitle.trim()}>
                Hinzufügen
              </button>
            </form>
          </section>
        )}

        <div className="subtasks-section">
          <button
            type="button"
            className="delete-subtask"
            onClick={() => void handleDeleteTask()}
            disabled={deleting}
          >
            {deleting ? "Wird gelöscht..." : "Aufgabe löschen"}
          </button>
        </div>
      </section>
    </main>
  );
}

export default TaskDetailPage;
