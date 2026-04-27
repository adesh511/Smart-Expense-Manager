import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="app-shell">
      <header className="top-bar">
        <div className="brand">Smart Expense Manager</div>
        <nav className="nav">
          <NavLink to="/" end>
            Dashboard
          </NavLink>
          <NavLink to="/expenses">Expenses</NavLink>
          <NavLink to="/budgets">Budgets</NavLink>
        </nav>
        <div className="user-area">
          <span className="muted">{user?.name}</span>
          <button type="button" className="btn ghost" onClick={logout}>
            Log out
          </button>
        </div>
      </header>
      <main className="main">
        <Outlet />
      </main>
    </div>
  )
}
