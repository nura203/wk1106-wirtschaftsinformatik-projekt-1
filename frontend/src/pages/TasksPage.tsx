import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getTasks, type Task } from "../services/api";

function getTaskTypeLabel(type: Task["type"]): string {
  switch (type) {
    case "ASSIGNMENT":
      return "Aufgabe";
    case "EXAM":
      return "Prüfung";
    case "GOAL":
      return "Ziel";
    default:
      return type;
  }
}

function getStatusLabel(status: Task["status"]): string {
  switch (status) {
    case "OPEN":
      return "Offen";
    case "IN_PROGRESS":
      return "In Bearbeitung";
    case "DONE":
      return "Erledigt";
    default:
      return status;
  }
}

function getUrgencyLabel(urgency: Task["urgency"]): string {
  switch (urgency) {
    case "RED":
      return "Dringend";
    case "YELLOW":
      return "Mittel";
    case "GREEN":
      return "Nicht dringend";
    default:
      return urgency;
  }
}

function formatDate(date: string): string {
  const parsedDate = new Date(`${date}T00:00:00`);

  if (Number.isNaN(parsedDate.getTime())) {
    return date;
  }

  return new Intl.DateTimeFormat("de-DE").format(parsedDate);
}

function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>Meine Aufgaben</h1>
          <p>Alle deine Aufgaben im Überblick.</p>
        </div>

        <Link to="/tasks/new" className="create-button">
          + Aufgabe erstellen
        </Link>
      </header>

      <section className="tasks-section">
        {error && <p className="error-message">{error}</p>}

        {loading ? (
          <p>Aufgaben werden geladen...</p>
        ) : tasks.length === 0 ? (
          <div className="task-card">
            <h2>Noch keine Aufgaben</h2>
            <p>Erstelle deine erste Aufgabe.</p>
          </div>
        ) : (
          <div className="task-list">
            {tasks.map((task) => (
              <article className="task-card" key={task.id}>
                <h2>{task.title}</h2>

                <div className="task-info">
                  <span>
                    Typ: {getTaskTypeLabel(task.type)}
                  </span>

                  <span>
                    Deadline: {formatDate(task.deadline)}
                  </span>

                  <span>
                    Fortschritt: {task.progressPercent}%
                  </span>

                  <span>
                    Status: {getStatusLabel(task.status)}
                  </span>

                  <span>
                    Dringlichkeit: {getUrgencyLabel(task.urgency)}
                  </span>
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
                  <Link to={`/tasks/${task.id}`}>
                    Details öffnen
                  </Link>
                  {" · "}
                  <Link to={`/tasks/${task.id}/edit`}>
                    Bearbeiten
                  </Link>
                </p>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default TasksPage;