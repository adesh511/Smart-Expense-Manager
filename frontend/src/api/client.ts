import axios from 'axios'

const baseURL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export const api = axios.create({
  baseURL,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err.response?.status
    // Spring often returns 403 for unauthenticated API calls; treat like 401 for this SPA.
    if (status === 401 || status === 403) {
      const path = window.location.pathname
      if (path !== '/login' && path !== '/register') {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.assign('/login')
      }
    }
    return Promise.reject(err)
  },
)
