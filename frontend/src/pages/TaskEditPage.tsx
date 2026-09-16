import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  getTask,
  updateTask,
  type CreateTaskRequest,
  type Task,
} from "../services/api";

function TaskEditPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [task, setTask] = useState<Task | null>(null);

  const [title, setTitle] = useState("");
  const [type, setType] = useState<CreateTaskRequest["type"]>("EXAM");
  const [deadline, setDeadline] = useState("");
  const [description, setDescription] = useState("");
  const [subject, setSubject] = useState("");
  const [estimatedHours, setEstimatedHours] = useState(0);
  const [weight, setWeight] = useState(1);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) {
      setError("Aufgabe wurde nicht gefunden.");
      setLoading(false);
      return;
    }

    async function loadTask() {
      try {
        const data = await getTask(id);

        setTask(data);
        setTitle(data.title);
        setType(data.type);
        setDeadline(data.deadline);
        setDescription(data.description ?? "");
        setSubject(data.subject ?? "");
        setEstimatedHours(data.estimatedHours);
        setWeight(data.weight);
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

    void loadTask();
  }, [id]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!id) {
      setError("Aufgabe wurde nicht gefunden.");
      return;
    }

    setError("");

    if (!title.trim()) {
      setError("Titel darf nicht leer sein.");
      return;
    }

    if (!deadline) {
      setError("Deadline muss angegeben werden.");
      return;
    }

    if (estimatedHours < 0) {
      setError("Geschätzte Stunden dürfen nicht negativ sein.");
      return;
    }

    if (weight < 1 || weight > 10) {
      setError("Gewichtung muss zwischen 1 und 10 liegen.");
      return;
    }

    setSaving(true);

    const request: CreateTaskRequest = {
      title: title.trim(),
      type,
      deadline,
      description: description.trim() || undefined,
      subject: subject.trim() || undefined,
      estimatedHours,
      weight,
    };

    try {
      await updateTask(id, request);
      navigate(`/tasks/${id}`);
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgabe konnte nicht gespeichert werden.",
      );
    } finally {
      setSaving(false);
    }
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

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>Aufgabe bearbeiten</h1>
          <p>{task.title}</p>
        </div>

        <Link to={`/tasks/${task.id}`} className="back-button">
          Abbrechen
        </Link>
      </header>

      <section className="tasks-section">
        <form className="create-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="title">Titel</label>

            <input
              id="title"
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="type">Typ</label>

            <select
              id="type"
              value={type}
              onChange={(event) =>
                setType(event.target.value as CreateTaskRequest["type"])
              }
            >
              <option value="EXAM">Prüfung</option>

              <option value="ASSIGNMENT">Aufgabe</option>

              <option value="GOAL">Lernziel</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="subject">Fach</label>

            <input
              id="subject"
              type="text"
              value={subject}
              onChange={(event) => setSubject(event.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="deadline">Deadline</label>

            <input
              id="deadline"
              type="date"
              value={deadline}
              onChange={(event) => setDeadline(event.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Beschreibung</label>

            <textarea
              id="description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              maxLength={5000}
            />
          </div>

          <div className="form-group">
            <label htmlFor="estimatedHours">
              Geschätzter Aufwand in Stunden
            </label>

            <input
              id="estimatedHours"
              type="number"
              min="0"
              value={estimatedHours}
              onChange={(event) =>
                setEstimatedHours(Number(event.target.value))
              }
            />
          </div>

          <div className="form-group">
            <label htmlFor="weight">Gewichtung</label>

            <input
              id="weight"
              type="number"
              min="1"
              max="10"
              value={weight}
              onChange={(event) => setWeight(Number(event.target.value))}
            />
          </div>

          {error && <p className="error-message">{error}</p>}

          <button type="submit" disabled={saving}>
            {saving ? "Wird gespeichert..." : "Änderungen speichern"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default TaskEditPage;
