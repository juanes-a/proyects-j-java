import axios from 'axios'
import router from '../router'

// Variable para almacenar el store
let authStore = null

// Configuración base de Axios
// http://localhost:8081/api localhost url de desarrollo

const api = axios.create({
  baseURL: 'https://proyects-j-java-production.up.railway.app/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Función para inicializar el store
export const initApi = (store) => {
  authStore = store
}

// Interceptor de solicitudes
api.interceptors.request.use(
  (config) => {
    // 👈 Cambia 'token' por 'authToken' para ser consistente con el router
    const token = localStorage.getItem('authToken')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor de respuestas
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401: // No autorizado
        case 403: // Prohibido
          if (authStore) {
            authStore.logout()
          }
          break
          
        case 500: // Error del servidor
          console.error('Error del servidor:', error)
          break
          
        default:
          console.error('Error HTTP:', error.response.status)
      }
    } else if (error.request) {
      console.error('No se recibió respuesta del servidor')
    } else {
      console.error('Error al configurar la solicitud:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default api