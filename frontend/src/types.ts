export type UserSummary = {
  id: number
  name: string
  email: string
}

export type AuthResponse = {
  token: string
  user: UserSummary
}

export type Category = {
  id: number
  name: string
}

export type Expense = {
  id: number
  amount: number
  categoryId: number
  categoryName: string
  date: string
  description?: string | null
}

export type PageResponse<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export type Budget = {
  id: number
  categoryId: number
  categoryName: string
  limitAmount: number
  budgetMonth: string
  spentAmount: number
  remainingAmount: number
  overBudget: boolean
}

export type Dashboard = {
  referenceMonth: string
  totalForMonth: number
  spendByCategory: { categoryName: string; amount: number }[]
  lastMonthsTrend: { month: string; total: number }[]
}
