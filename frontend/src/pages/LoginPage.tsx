import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../services/api";

function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await login({
        email,
        password,
      });

      localStorage.setItem("token", response.token);

      if (response.userId) {
        localStorage.setItem("userId", response.userId);
      }

      if (response.username) {
        localStorage.setItem("username", response.username);
      }

      navigate("/", { replace: true });
    } catch (error: unknown) {
      setError(
        error instanceof Error ? error.message : "Login fehlgeschlagen.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-card">
        <h1>StudyPlanner</h1>

        <p className="subtitle">Melde dich an, um fortzufahren</p>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">E-Mail</label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Passwort</label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              minLength={8}
              required
            />
          </div>

          {error && <p className="error-message">{error}</p>}

          <button type="submit" disabled={loading}>
            {loading ? "Bitte warten..." : "Einloggen"}
          </button>
        </form>

        <Link to="/register" className="switch-button">
          Noch kein Konto? Registrieren
        </Link>
      </div>
    </main>
  );
}

export default LoginPage;
