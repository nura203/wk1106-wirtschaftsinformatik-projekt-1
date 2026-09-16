import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  changePassword,
  createReminder,
  deleteReminder,
  downloadCalendarExport,
  getReminders,
  getTasks,
  updateReminder,
  type Reminder,
  type Task,
} from "../services/api";

function SettingsPage() {
  const navigate = useNavigate();

  const [tasks, setTasks] = useState<Task[]>([]);
  const [reminders, setReminders] = useState<Record<string, Reminder[]>>({});
  const [selectedTaskId, setSelectedTaskId] = useState("");
  const [daysBefore, setDaysBefore] = useState(1);
  const [channel, setChannel] = useState<Reminder["channel"]>("IN_APP");

  const [password, setPassword] = useState("");
  const [passwordConfirmation, setPasswordConfirmation] = useState("");
  const [changingPassword, setChangingPassword] = useState(false);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  async function loadData() {
    setLoading(true);
    setError("");

    try {
      const taskData = await getTasks();
      setTasks(taskData);

      const reminderEntries = await Promise.all(
        taskData.map(async (task) => {
          const taskReminders = await getReminders(task.id);

          return [task.id, taskReminders] as const;
        }),
      );

      setReminders(Object.fromEntries(reminderEntries));
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Erinnerungen konnten nicht geladen werden.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, []);

  async function handleCreateReminder() {
    if (!selectedTaskId) {
      setError("Bitte wähle eine Aufgabe aus.");
      return;
    }

    if (daysBefore < 1) {
      setError("Tage vor Deadline muss mindestens 1 sein.");
      return;
    }

    setSaving(true);
    setError("");
    setMessage("");

    try {
      const reminder = await createReminder(
        selectedTaskId,
        daysBefore,
        channel,
      );

      setReminders((current) => ({
        ...current,
        [selectedTaskId]: [...(current[selectedTaskId] ?? []), reminder],
      }));

      setMessage("Erinnerung wurde gespeichert.");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Erinnerung konnte nicht gespeichert werden.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleToggleReminder(reminder: Reminder) {
    setError("");
    setMessage("");

    try {
      const updatedReminder = await updateReminder(
        reminder.taskId,
        reminder.id,
        reminder.daysBefore,
        reminder.channel,
        !reminder.active,
      );

      setReminders((current) => ({
        ...current,
        [reminder.taskId]: (current[reminder.taskId] ?? []).map((item) =>
          item.id === reminder.id ? updatedReminder : item,
        ),
      }));

      setMessage("Erinnerung wurde aktualisiert.");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Erinnerung konnte nicht aktualisiert werden.",
      );
    }
  }

  async function handleDeleteReminder(reminder: Reminder) {
    setError("");
    setMessage("");

    try {
      await deleteReminder(reminder.taskId, reminder.id);

      setReminders((current) => ({
        ...current,
        [reminder.taskId]: (current[reminder.taskId] ?? []).filter(
          (item) => item.id !== reminder.id,
        ),
      }));

      setMessage("Erinnerung wurde gelöscht.");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Erinnerung konnte nicht gelöscht werden.",
      );
    }
  }

  async function handleChangePassword() {
    setError("");
    setMessage("");

    if (!password) {
      setError("Bitte gib ein neues Passwort ein.");
      return;
    }

    if (password.length < 8) {
      setError("Passwort muss mindestens 8 Zeichen lang sein.");
      return;
    }

    if (password !== passwordConfirmation) {
      setError("Die Passwörter stimmen nicht überein.");
      return;
    }

    setChangingPassword(true);

    try {
      await changePassword(password);

      setPassword("");
      setPasswordConfirmation("");
      setMessage("Passwort wurde erfolgreich geändert.");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Passwort konnte nicht geändert werden.",
      );
    } finally {
      setChangingPassword(false);
    }
  }

  async function handleCalendarExport() {
    setExporting(true);
    setError("");
    setMessage("");

    try {
      await downloadCalendarExport();
      setMessage("Kalender wurde exportiert.");
    } catch (error: unknown) {
      setError(
        error instanceof Error
          ? error.message
          : "Kalenderexport fehlgeschlagen.",
      );
    } finally {
      setExporting(false);
    }
  }

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");

    navigate("/login", { replace: true });
  }

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>Einstellungen</h1>
          <p>Verwalte deine StudyPlanner-Einstellungen.</p>
        </div>

        <button type="button" className="logout-button" onClick={handleLogout}>
          Abmelden
        </button>
      </header>

      <section className="tasks-section">
        <div className="task-card">
          <h2>Profil</h2>

          <p>
            Benutzername: {localStorage.getItem("username") || "Studierende/r"}
          </p>
        </div>

        <div className="task-card">
          <h2>Passwort ändern</h2>

          <div className="create-form">
            <div className="form-group">
              <label htmlFor="new-password">Neues Passwort</label>

              <input
                id="new-password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                minLength={8}
                autoComplete="new-password"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password-confirmation">
                Neues Passwort bestätigen
              </label>

              <input
                id="password-confirmation"
                type="password"
                value={passwordConfirmation}
                onChange={(event) =>
                  setPasswordConfirmation(event.target.value)
                }
                minLength={8}
                autoComplete="new-password"
              />
            </div>

            <button
              type="button"
              onClick={() => void handleChangePassword()}
              disabled={changingPassword}
            >
              {changingPassword ? "Wird gespeichert..." : "Passwort speichern"}
            </button>
          </div>
        </div>

        <div className="task-card">
          <h2>Erinnerungen</h2>

          <p>Konfiguriere Erinnerungen für deine Aufgaben.</p>

          <div className="create-form">
            <div className="form-group">
              <label htmlFor="reminder-task">Aufgabe</label>

              <select
                id="reminder-task"
                value={selectedTaskId}
                onChange={(event) => setSelectedTaskId(event.target.value)}
              >
                <option value="">Aufgabe auswählen</option>

                {tasks.map((task) => (
                  <option key={task.id} value={task.id}>
                    {task.title}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="days-before">Tage vor Deadline</label>

                <input
                  id="days-before"
                  type="number"
                  min="1"
                  value={daysBefore}
                  onChange={(event) =>
                    setDaysBefore(Number(event.target.value))
                  }
                />
              </div>

              <div className="form-group">
                <label htmlFor="reminder-channel">Kanal</label>

                <select
                  id="reminder-channel"
                  value={channel}
                  onChange={(event) =>
                    setChannel(event.target.value as Reminder["channel"])
                  }
                >
                  <option value="IN_APP">In-App</option>
                  <option value="EMAIL">E-Mail</option>
                  <option value="BOTH">In-App + E-Mail</option>
                </select>
              </div>
            </div>

            <button
              type="button"
              onClick={() => void handleCreateReminder()}
              disabled={saving}
            >
              {saving ? "Wird gespeichert..." : "Erinnerung speichern"}
            </button>
          </div>

          {loading ? (
            <p>Erinnerungen werden geladen...</p>
          ) : (
            tasks.map((task) => {
              const taskReminders = reminders[task.id] ?? [];

              if (taskReminders.length === 0) {
                return null;
              }

              return (
                <div key={task.id} className="subtasks-section">
                  <div className="subtasks-header">
                    <h3>{task.title}</h3>
                  </div>

                  <div className="subtask-list">
                    {taskReminders.map((reminder) => (
                      <div className="subtask-item" key={reminder.id}>
                        <div>
                          <strong>{reminder.daysBefore} Tag(e) vorher</strong>

                          <div>Kanal: {reminder.channel}</div>

                          <div>
                            Status: {reminder.active ? "Aktiv" : "Inaktiv"}
                          </div>
                        </div>

                        <div>
                          <button
                            type="button"
                            className="delete-subtask"
                            onClick={() => void handleToggleReminder(reminder)}
                          >
                            {reminder.active ? "Deaktivieren" : "Aktivieren"}
                          </button>

                          <button
                            type="button"
                            className="delete-subtask"
                            onClick={() => void handleDeleteReminder(reminder)}
                          >
                            Löschen
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })
          )}
        </div>

        <div className="task-card">
          <h2>Kalenderexport</h2>

          <p>Exportiere deine offenen Aufgaben als iCal-Kalenderdatei.</p>

          <button
            type="button"
            className="create-button"
            onClick={() => void handleCalendarExport()}
            disabled={exporting}
          >
            {exporting ? "Wird exportiert..." : "Kalender exportieren"}
          </button>
        </div>

        {error && <p className="error-message">{error}</p>}

        {message && <p className="message">{message}</p>}
      </section>
    </main>
  );
}

export default SettingsPage;
