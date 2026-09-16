import { Link, Outlet, useLocation } from "react-router-dom";

function MainLayout() {
  const location = useLocation();

  function isActive(path: string): boolean {
    if (path === "/") {
      return location.pathname === "/";
    }

    return location.pathname.startsWith(path);
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-title">
          <h1>StudyPlanner</h1>
        </div>

        <nav className="sidebar-navigation">
          <Link to="/" className={isActive("/") ? "active" : ""}>
            Dashboard
          </Link>

          <Link to="/tasks" className={isActive("/tasks") ? "active" : ""}>
            Aufgaben
          </Link>

          <Link to="/plan" className={isActive("/plan") ? "active" : ""}>
            Lernplan
          </Link>

          <Link
            to="/settings"
            className={isActive("/settings") ? "active" : ""}
          >
            Einstellungen
          </Link>
        </nav>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>

      <nav className="mobile-navigation">
        <Link to="/" className={isActive("/") ? "active" : ""}>
          Dashboard
        </Link>

        <Link to="/tasks" className={isActive("/tasks") ? "active" : ""}>
          Aufgaben
        </Link>

        <Link to="/plan" className={isActive("/plan") ? "active" : ""}>
          Lernplan
        </Link>

        <Link to="/settings" className={isActive("/settings") ? "active" : ""}>
          Einstellungen
        </Link>
      </nav>
    </div>
  );
}

export default MainLayout;
