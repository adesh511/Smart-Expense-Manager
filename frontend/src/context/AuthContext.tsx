import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react'
import type { UserSummary } from '../types'
import * as api from '../api/endpoints'

type AuthState = {
  user: UserSummary | null
  token: string | null
  loading: boolean
  login: (email: string, password: string) => Promise<void>
  register: (name: string, email: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthState | null>(null)

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserSummary | null>(null)
  const [token, setToken] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const t = localStorage.getItem(TOKEN_KEY)
    const u = localStorage.getItem(USER_KEY)
    if (t && u) {
      setToken(t)
      try {
        setUser(JSON.parse(u) as UserSummary)
      } catch {
        localStorage.removeItem(USER_KEY)
        localStorage.removeItem(TOKEN_KEY)
      }
    }
    setLoading(false)
  }, [])

  const persist = useCallback((auth: { token: string; user: UserSummary }) => {
    localStorage.setItem(TOKEN_KEY, auth.token)
    localStorage.setItem(USER_KEY, JSON.stringify(auth.user))
    setToken(auth.token)
    setUser(auth.user)
  }, [])

  const login = useCallback(
    async (email: string, password: string) => {
      const res = await api.login({ email, password })
      persist(res)
    },
    [persist],
  )

  const register = useCallback(
    async (name: string, email: string, password: string) => {
      const res = await api.register({ name, email, password })
      persist(res)
    },
    [persist],
  )

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setToken(null)
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({ user, token, loading, login, register, logout }),
    [user, token, loading, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
