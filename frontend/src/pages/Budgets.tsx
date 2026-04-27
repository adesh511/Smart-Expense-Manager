import { useEffect, useState, type FormEvent } from 'react'
import {
  createBudget,
  deleteBudget,
  fetchBudgets,
  fetchCategories,
  updateBudget,
} from '../api/endpoints'
import type { Budget, Category } from '../types'

function defaultMonth() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

export function Budgets() {
  const [categories, setCategories] = useState<Category[]>([])
  const [rows, setRows] = useState<Budget[]>([])
  const [error, setError] = useState<string | null>(null)

  const [limit, setLimit] = useState('')
  const [cat, setCat] = useState<number | ''>('')
  const [budgetMonth, setBudgetMonth] = useState(defaultMonth)
  const [editingId, setEditingId] = useState<number | null>(null)

  async function reload() {
    setError(null)
    try {
      setRows(await fetchBudgets())
    } catch (e: unknown) {
      const msg =
        (e as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Failed to load budgets'
      setError(msg)
    }
  }

  useEffect(() => {
    ;(async () => {
      try {
        setCategories(await fetchCategories())
      } catch {
        setCategories([])
      }
      await reload()
    })()
  }, [])

  async function onCreate(e: FormEvent) {
    e.preventDefault()
    if (cat === '') return
    setError(null)
    try {
      await createBudget({
        categoryId: cat,
        limitAmount: Number(limit),
        budgetMonth,
      })
      setLimit('')
      await reload()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Could not create budget'
      setError(msg)
    }
  }

  function startEdit(b: Budget) {
    setEditingId(b.id)
    setCat(b.categoryId)
    setLimit(String(b.limitAmount))
    setBudgetMonth(b.budgetMonth)
  }

  async function onSave() {
    if (cat === '' || editingId == null) return
    setError(null)
    try {
      await updateBudget(editingId, {
        categoryId: cat,
        limitAmount: Number(limit),
        budgetMonth,
      })
      setEditingId(null)
      await reload()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Could not update budget'
      setError(msg)
    }
  }

  async function onDelete(id: number) {
    if (!confirm('Delete this budget?')) return
    setError(null)
    try {
      await deleteBudget(id)
      await reload()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Could not delete'
      setError(msg)
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Budgets</h1>
      </div>
      {error && <p className="error">{error}</p>}

      <div className="card">
        <h2>Add budget</h2>
        <form className="form row-form" onSubmit={onCreate}>
          <label>
            Category
            <select
              value={cat === '' ? '' : String(cat)}
              onChange={(e) =>
                setCat(e.target.value === '' ? '' : Number(e.target.value))
              }
              required
            >
              <option value="">Select…</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Month
            <input
              type="month"
              value={budgetMonth}
              onChange={(e) => setBudgetMonth(e.target.value)}
              required
            />
          </label>
          <label>
            Limit
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={limit}
              onChange={(e) => setLimit(e.target.value)}
              required
            />
          </label>
          <button type="submit" className="btn primary">
            Add
          </button>
        </form>
      </div>

      <div className="card table-card">
        <table className="table">
          <thead>
            <tr>
              <th>Month</th>
              <th>Category</th>
              <th>Limit</th>
              <th>Spent</th>
              <th>Remaining</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {rows.map((b) =>
              editingId === b.id ? (
                <tr key={b.id}>
                  <td>
                    <input
                      type="month"
                      value={budgetMonth}
                      onChange={(e) => setBudgetMonth(e.target.value)}
                    />
                  </td>
                  <td>
                    <select
                      value={cat === '' ? '' : String(cat)}
                      onChange={(e) =>
                        setCat(
                          e.target.value === '' ? '' : Number(e.target.value),
                        )
                      }
                    >
                      {categories.map((c) => (
                        <option key={c.id} value={c.id}>
                          {c.name}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td colSpan={3}>
                    <input
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={limit}
                      onChange={(e) => setLimit(e.target.value)}
                    />
                    <button
                      type="button"
                      className="btn small primary"
                      onClick={onSave}
                    >
                      Save
                    </button>
                    <button
                      type="button"
                      className="btn small ghost"
                      onClick={() => setEditingId(null)}
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              ) : (
                <tr key={b.id} className={b.overBudget ? 'row-warn' : ''}>
                  <td>{b.budgetMonth}</td>
                  <td>{b.categoryName}</td>
                  <td>{Number(b.limitAmount).toFixed(2)}</td>
                  <td>{Number(b.spentAmount).toFixed(2)}</td>
                  <td>{Number(b.remainingAmount).toFixed(2)}</td>
                  <td className="actions">
                    <button
                      type="button"
                      className="btn small ghost"
                      onClick={() => startEdit(b)}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="btn small danger"
                      onClick={() => onDelete(b.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ),
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
