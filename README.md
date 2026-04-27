# Smart Expense Manager

Full-stack expense tracker: **Spring Boot 3** (REST + JWT + OpenAPI), **MySQL**, **React (Vite + TypeScript)** with dashboards (Recharts).

## Prerequisites

- JDK 17+
- Apache Maven 3.9+
- MySQL 8+ (server running locally or remote)
- Node.js 20+ (for the frontend)

## Database

Create is optional: the JDBC URL uses `createDatabaseIfNotExist=true` for schema `expense_manager` by default.

Override with environment variables if needed:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`

## Backend

```bash
cd backend
mvn spring-boot:run
```

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Default MySQL URL in `application.yml`: user `root`, empty password (change via `DB_PASSWORD`)

Set a strong secret in production:

- `JWT_SECRET` — any string (internally hashed to a signing key)

CORS for the SPA:

- `APP_CORS_ORIGINS` — comma-separated list, default `http://localhost:5173`

## Frontend

```bash
cd frontend
npm install
npm run dev
```

- Dev server: `http://localhost:5173`
- API URL: set `VITE_API_URL` in `frontend/.env` (default `http://localhost:8080`)

## Features

- Register / login with JWT
- Categories (seeded on first run: Food, Transport, Shopping, Bills, Entertainment, Health, Other)
- Expenses: CRUD, filters (date range, category), pagination
- Budgets: per category and month (`yyyy-MM`), spent vs limit
- Dashboard: totals for a selected month, spend by category, six-month trend

## Project layout

- `backend/` — Spring Boot application (`com.smartexpense`)
- `frontend/` — Vite React SPA

Docker was intentionally omitted from this version; use local MySQL or a managed instance.
"# Smart-Expense-Manager" 
