import axios from 'axios'
import router from '../router'
import { useAuthStore } from '../stores/auth'

// Crea una instancia de axios configurada
const api = axios.create({
  baseURL: 'http://localhost:8081/api', // Asegúrate que coincide con tu backend
  timeout: 10000, // 10 segundos de timeout
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Interceptor para añadir el token a cada request
api.interceptors.request.use(config => {
  const authStore = useAuthStore()
  const token = authStore.token || localStorage.getItem('token')
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  
  return config
}, error => {
  return Promise.reject(error)
})

// Interceptor para manejar respuestas de error
api.interceptors.response.use(response => {
  return response
}, error => {
  if (error.response?.status === 401) {
    // Si el error es 401 (no autorizado)
    const authStore = useAuthStore()
    authStore.logout() // Limpia el store de autenticación
    localStorage.removeItem('token') // Limpia el localStorage
    router.push('/login') // Redirige al login
  }
  
  // Puedes manejar otros códigos de error aquí
  if (error.response?.status >= 500) {
    console.error('Server error:', error)
  }
  
  return Promise.reject(error)
})

export default api