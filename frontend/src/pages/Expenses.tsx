import { useEffect, useState, type FormEvent } from 'react'
import {
  createExpense,
  deleteExpense,
  fetchCategories,
  fetchExpenses,
  updateExpense,
} from '../api/endpoints'
import type { Category, Expense } from '../types'

export function Expenses() {
  const [categories, setCategories] = useState<Category[]>([])
  const [rows, setRows] = useState<Expense[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [from, setFrom] = useState('')
  const [to, setTo] = useState('')
  const [categoryId, setCategoryId] = useState<number | ''>('')
  const [error, setError] = useState<string | null>(null)

  const [amount, setAmount] = useState('')
  const [cat, setCat] = useState<number | ''>('')
  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 10))
  const [description, setDescription] = useState('')

  const [editingId, setEditingId] = useState<number | null>(null)

  async function reload(p = page) {
    setError(null)
    try {
      const res = await fetchExpenses({
        page: p,
        size: 10,
        from: from || undefined,
        to: to || undefined,
        categoryId: categoryId === '' ? undefined : categoryId,
      })
      setRows(res.content)
      setTotalPages(res.totalPages)
      setPage(res.number)
    } catch (e: unknown) {
      const msg =
        (e as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Failed to load expenses'
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
    })()
  }, [])

  useEffect(() => {
    reload(0)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [from, to, categoryId])

  async function onCreate(e: FormEvent) {
    e.preventDefault()
    if (cat === '') return
    setError(null)
    try {
      await createExpense({
        amount: Number(amount),
        categoryId: cat,
        date,
        description: description || undefined,
      })
      setAmount('')
      setDescription('')
      await reload(0)
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Could not create expense'
      setError(msg)
    }
  }

  async function onSaveEdit(row: Expense) {
    if (cat === '') return
    setError(null)
    try {
      await updateExpense(row.id, {
        amount: Number(amount),
        categoryId: Number(cat),
        date,
        description: description || undefined,
      })
      setEditingId(null)
      await reload()
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { error?: string } } })?.response?.data
          ?.error ?? 'Could not update expense'
      setError(msg)
    }
  }

  function startEdit(row: Expense) {
    setEditingId(row.id)
    setAmount(String(row.amount))
    setCat(row.categoryId)
    setDate(row.date)
    setDescription(row.description ?? '')
  }

  async function onDelete(id: number) {
    if (!confirm('Delete this expense?')) return
    setError(null)
    try {
      await deleteExpense(id)
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
        <h1>Expenses</h1>
      </div>
      {error && <p className="error">{error}</p>}

      <div className="card">
        <h2>Add expense</h2>
        <form className="form row-form" onSubmit={onCreate}>
          <label>
            Amount
            <input
              type="number"
              step="0.01"
              min="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </label>
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
            Date
            <input
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </label>
          <label className="grow">
            Note
            <input
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              maxLength={2000}
            />
          </label>
          <button type="submit" className="btn primary">
            Add
          </button>
        </form>
      </div>

      <div className="card">
        <h2>Filters</h2>
        <div className="form row-form">
          <label>
            From
            <input
              type="date"
              value={from}
              onChange={(e) => setFrom(e.target.value)}
            />
          </label>
          <label>
            To
            <input type="date" value={to} onChange={(e) => setTo(e.target.value)} />
          </label>
          <label>
            Category
            <select
              value={categoryId === '' ? '' : String(categoryId)}
              onChange={(e) =>
                setCategoryId(
                  e.target.value === '' ? '' : Number(e.target.value),
                )
              }
            >
              <option value="">All</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <div className="card table-card">
        <table className="table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Category</th>
              <th>Amount</th>
              <th>Note</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {rows.map((row) =>
              editingId === row.id ? (
                <tr key={row.id}>
                  <td>
                    <input
                      type="date"
                      value={date}
                      onChange={(e) => setDate(e.target.value)}
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
                  <td>
                    <input
                      type="number"
                      step="0.01"
                      min="0.01"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                    />
                  </td>
                  <td colSpan={2}>
                    <input
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                    />
                    <button
                      type="button"
                      className="btn small primary"
                      onClick={() => onSaveEdit(row)}
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
                <tr key={row.id}>
                  <td>{row.date}</td>
                  <td>{row.categoryName}</td>
                  <td>{Number(row.amount).toFixed(2)}</td>
                  <td>{row.description}</td>
                  <td className="actions">
                    <button
                      type="button"
                      className="btn small ghost"
                      onClick={() => startEdit(row)}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="btn small danger"
                      onClick={() => onDelete(row.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ),
            )}
          </tbody>
        </table>
        <div className="pager">
          <button
            type="button"
            className="btn ghost"
            disabled={page <= 0}
            onClick={() => reload(page - 1)}
          >
            Previous
          </button>
          <span className="muted small">
            Page {page + 1} of {Math.max(1, totalPages)}
          </span>
          <button
            type="button"
            className="btn ghost"
            disabled={page >= totalPages - 1}
            onClick={() => reload(page + 1)}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  )
}
