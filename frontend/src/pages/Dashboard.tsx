import { useEffect, useMemo, useState } from 'react'
import {
  Bar,
  BarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { fetchDashboard } from '../api/endpoints'
import type { Dashboard } from '../types'

function monthInputValue(d: Date) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

export function Dashboard() {
  const [month, setMonth] = useState(monthInputValue(new Date()))
  const [data, setData] = useState<Dashboard | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      try {
        const dash = await fetchDashboard(month)
        if (!cancelled) {
          setData(dash)
          setError(null)
        }
      } catch (e: unknown) {
        if (!cancelled) {
          const ax = e as {
            response?: { data?: { error?: string } }
            message?: string
          }
          const serverMsg = ax.response?.data?.error
          const networkMsg =
            ax.message && !ax.response ? ax.message : undefined
          setError(serverMsg ?? networkMsg ?? 'Could not load dashboard')
        }
      }
    })()
    return () => {
      cancelled = true
    }
  }, [month])

  const barData = useMemo(
    () =>
      (data?.spendByCategory ?? []).map((c) => ({
        name: c.categoryName,
        amount: Number(c.amount),
      })),
    [data],
  )

  const lineData = useMemo(
    () =>
      (data?.lastMonthsTrend ?? []).map((r) => ({
        month: r.month,
        total: Number(r.total),
      })),
    [data],
  )

  return (
    <div className="page">
      <div className="page-header">
        <h1>Dashboard</h1>
        <label className="inline-field">
          Month
          <input
            type="month"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
          />
        </label>
      </div>

      {error && <p className="error">{error}</p>}

      {data && (
        <>
          <div className="grid stats">
            <div className="card stat">
              <div className="muted small">Total spend</div>
              <div className="stat-value">
                {data.referenceMonth}:{' '}
                <strong>{Number(data.totalForMonth).toFixed(2)}</strong>
              </div>
            </div>
          </div>

          <div className="grid two">
            <div className="card chart-card">
              <h2>By category</h2>
              <div className="chart-wrap">
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={barData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} />
                    <Tooltip
                      formatter={(value) =>
                        Number(value ?? 0).toLocaleString(undefined, {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })
                      }
                    />
                    <Bar dataKey="amount" fill="#2563eb" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="card chart-card">
              <h2>Last six months</h2>
              <div className="chart-wrap">
                <ResponsiveContainer width="100%" height={280}>
                  <LineChart data={lineData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis dataKey="month" tick={{ fontSize: 11 }} />
                    <YAxis tick={{ fontSize: 11 }} />
                    <Tooltip
                      formatter={(value) =>
                        Number(value ?? 0).toLocaleString(undefined, {
                          minimumFractionDigits: 2,
                          maximumFractionDigits: 2,
                        })
                      }
                    />
                    <Line
                      type="monotone"
                      dataKey="total"
                      stroke="#059669"
                      strokeWidth={2}
                      dot={{ r: 3 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
