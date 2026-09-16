import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { deleteTask, getTasks, type Task } from "../services/api";

function DashboardPage() {
  const navigate = useNavigate();

  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const username = localStorage.getItem("username") || "Studierende/r";

  async function loadTasks() {
    setLoading(true);
    setError("");

    try {
      const data = await getTasks();
      setTasks(data);
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgaben konnten nicht geladen werden.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadTasks();
  }, []);

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");

    navigate("/login", { replace: true });
  }

  async function handleDeleteTask(taskId: string) {
    const confirmed = window.confirm(
      "Möchtest du diese Aufgabe wirklich löschen?",
    );

    if (!confirmed) {
      return;
    }

    try {
      await deleteTask(taskId);
      await loadTasks();
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgabe konnte nicht gelöscht werden.",
      );
    }
  }

  function getUrgencyLabel(urgency: Task["urgency"]): string {
    if (urgency === "RED") {
      return "Dringend";
    }

    if (urgency === "YELLOW") {
      return "Bald fällig";
    }

    return "Nicht dringend";
  }

  function getDaysUntilDeadline(deadline: string): number {
    const today = new Date();
    const deadlineDate = new Date(`${deadline}T00:00:00`);

    today.setHours(0, 0, 0, 0);

    const difference = deadlineDate.getTime() - today.getTime();

    return Math.ceil(difference / (1000 * 60 * 60 * 24));
  }

  function getDeadlineText(deadline: string): string {
    const days = getDaysUntilDeadline(deadline);

    if (days < 0) {
      return `${Math.abs(days)} Tage überfällig`;
    }

    if (days === 0) {
      return "Heute fällig";
    }

    if (days === 1) {
      return "Morgen fällig";
    }

    return `In ${days} Tagen`;
  }

  const weeklyTaskCount = tasks.filter((task) => task.status !== "DONE").length;

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>StudyPlanner</h1>
          <p>Willkommen, {username}!</p>
        </div>

        <button type="button" className="logout-button" onClick={handleLogout}>
          Abmelden
        </button>
      </header>

      <section className="tasks-section">
        <div className="section-header">
          <div>
            <h2>Dashboard</h2>
            <p>Deine offenen Aufgaben und deren Dringlichkeit.</p>
          </div>

          <Link to="/tasks/new" className="create-button">
            + Aufgabe erstellen
          </Link>
        </div>

        <div className="task-card">
          <h3>Wochenübersicht</h3>
          <p>Offene Aufgaben: {weeklyTaskCount}</p>
        </div>

        {error && <p className="error-message">{error}</p>}

        {loading ? (
          <p>Aufgaben werden geladen...</p>
        ) : tasks.length === 0 ? (
          <div className="task-card">
            <h3>Noch keine Aufgaben</h3>
            <p>Erstelle deine erste Aufgabe.</p>
          </div>
        ) : (
          <div className="task-list">
            {tasks.map((task) => (
              <article className="task-card" key={task.id}>
                <div className="section-header">
                  <div>
                    <h3>{task.title}</h3>

                    {task.description && <p>{task.description}</p>}
                  </div>

                  <button
                    type="button"
                    onClick={() => void handleDeleteTask(task.id)}
                  >
                    Löschen
                  </button>
                </div>

                <div className="task-info">
                  <span>Typ: {task.type}</span>

                  <span>Fach: {task.subject || "Kein Fach"}</span>

                  <span>Deadline: {task.deadline}</span>

                  <span>{getDeadlineText(task.deadline)}</span>

                  <span>Urgency: {getUrgencyLabel(task.urgency)}</span>

                  <span>Fortschritt: {task.progressPercent}%</span>

                  <span>Status: {task.status}</span>
                </div>

                <div className="progress-bar">
                  <div
                    className="progress-value"
                    style={{
                      width: `${Math.min(
                        Math.max(task.progressPercent, 0),
                        100,
                      )}%`,
                    }}
                  />
                </div>

                <p className="task-hint">
                  <Link to={`/tasks/${task.id}`}>Aufgabe öffnen</Link>
                  {" · "}
                  <Link to={`/tasks/${task.id}/edit`}>Bearbeiten</Link>
                </p>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default DashboardPage;
