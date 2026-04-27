import type {
  AuthResponse,
  Budget,
  Category,
  Dashboard,
  Expense,
  PageResponse,
  UserSummary,
} from '../types'
import { api } from './client'

export async function register(payload: {
  name: string
  email: string
  password: string
}): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/api/auth/register', payload)
  return data
}

export async function login(payload: {
  email: string
  password: string
}): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/api/auth/login', payload)
  return data
}

export async function me(): Promise<UserSummary> {
  const { data } = await api.get<UserSummary>('/api/auth/me')
  return data
}

export async function fetchCategories(): Promise<Category[]> {
  const { data } = await api.get<Category[]>('/api/categories')
  return data
}

export async function fetchExpenses(params: {
  from?: string
  to?: string
  categoryId?: number
  page?: number
  size?: number
}): Promise<PageResponse<Expense>> {
  const { data } = await api.get<PageResponse<Expense>>('/api/expenses', { params })
  return data
}

export async function createExpense(payload: {
  amount: number
  categoryId: number
  date: string
  description?: string
  recurring?: boolean
  frequency?: 'DAILY' | 'WEEKLY' | 'MONTHLY'
  nextRunDate?: string
  active?: boolean
}): Promise<Expense> {
  const { data } = await api.post<Expense>('/api/expenses', payload)
  return data
}

export async function updateExpense(
  id: number,
  payload: {
    amount: number
    categoryId: number
    date: string
    description?: string
  },
): Promise<Expense> {
  const { data } = await api.put<Expense>(`/api/expenses/${id}`, payload)
  return data
}

export async function deleteExpense(id: number): Promise<void> {
  await api.delete(`/api/expenses/${id}`)
}

export async function fetchBudgets(): Promise<Budget[]> {
  const { data } = await api.get<Budget[]>('/api/budgets')
  return data
}

export async function createBudget(payload: {
  categoryId: number
  limitAmount: number
  budgetMonth: string
}): Promise<Budget> {
  const { data } = await api.post<Budget>('/api/budgets', payload)
  return data
}

export async function updateBudget(
  id: number,
  payload: { categoryId: number; limitAmount: number; budgetMonth: string },
): Promise<Budget> {
  const { data } = await api.put<Budget>(`/api/budgets/${id}`, payload)
  return data
}

export async function deleteBudget(id: number): Promise<void> {
  await api.delete(`/api/budgets/${id}`)
}

export async function fetchDashboard(month?: string): Promise<Dashboard> {
  const { data } = await api.get<Dashboard>('/api/analytics/dashboard', {
    params: month ? { month } : undefined,
  })
  return data
}
