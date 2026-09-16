import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  createSubtask,
  createTask,
  type CreateTaskRequest,
} from "../services/api";

function TaskCreatePage() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [type, setType] = useState<CreateTaskRequest["type"]>("EXAM");
  const [deadline, setDeadline] = useState("");
  const [description, setDescription] = useState("");
  const [subject, setSubject] = useState("");
  const [estimatedHours, setEstimatedHours] = useState(0);
  const [weight, setWeight] = useState(1);

  const [subtasks, setSubtasks] = useState<string[]>([]);
  const [newSubtask, setNewSubtask] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function addSubtask() {
    const trimmedTitle = newSubtask.trim();

    if (!trimmedTitle) {
      return;
    }

    setSubtasks((current) => [...current, trimmedTitle]);
    setNewSubtask("");
  }

  function removeSubtask(index: number) {
    setSubtasks((current) =>
      current.filter((_, currentIndex) => currentIndex !== index),
    );
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

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

    setLoading(true);

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
      const task = await createTask(request);

      if (type === "GOAL") {
        await Promise.all(
          subtasks.map((subtaskTitle, index) =>
            createSubtask(task.id, subtaskTitle, false, index),
          ),
        );
      }

      navigate(`/tasks/${task.id}`);
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Aufgabe konnte nicht erstellt werden.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>Aufgabe erstellen</h1>
          <p>Erstelle eine neue Aufgabe für deinen StudyPlanner.</p>
        </div>

        <Link to="/tasks" className="back-button">
          Abbrechen
        </Link>
      </header>

      <section className="tasks-section">
        <form className="create-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Typ</label>

            <div className="form-row">
              <button
                type="button"
                className={type === "EXAM" ? "create-button" : "back-button"}
                onClick={() => setType("EXAM")}
              >
                Prüfung
              </button>

              <button
                type="button"
                className={
                  type === "ASSIGNMENT" ? "create-button" : "back-button"
                }
                onClick={() => setType("ASSIGNMENT")}
              >
                Aufgabe
              </button>

              <button
                type="button"
                className={type === "GOAL" ? "create-button" : "back-button"}
                onClick={() => setType("GOAL")}
              >
                Lernziel
              </button>
            </div>
          </div>

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

            {deadline &&
              new Date(`${deadline}T00:00:00`).getTime() <
                new Date().setHours(0, 0, 0, 0) && (
                <p className="error-message">
                  Die Deadline liegt in der Vergangenheit.
                </p>
              )}
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

          {type === "GOAL" && (
            <div className="form-group">
              <label htmlFor="new-subtask">Subtasks</label>

              <div className="subtask-form">
                <input
                  id="new-subtask"
                  type="text"
                  value={newSubtask}
                  onChange={(event) => setNewSubtask(event.target.value)}
                  placeholder="Subtask eingeben"
                />

                <button
                  type="button"
                  onClick={addSubtask}
                  disabled={!newSubtask.trim()}
                >
                  Hinzufügen
                </button>
              </div>

              {subtasks.length > 0 && (
                <div className="subtask-list">
                  {subtasks.map((subtask, index) => (
                    <div className="subtask-item" key={`${subtask}-${index}`}>
                      <span>{subtask}</span>

                      <button
                        type="button"
                        className="delete-subtask"
                        onClick={() => removeSubtask(index)}
                      >
                        Entfernen
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {error && <p className="error-message">{error}</p>}

          <button type="submit" disabled={loading}>
            {loading ? "Wird gespeichert..." : "Aufgabe speichern"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default TaskCreatePage;
