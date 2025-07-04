import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '../router'
import api from '../api' // Asegúrate de importar api si no lo has hecho

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('authToken') || null) // Cambiado a 'authToken' para coincidir con tu login
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  const welcomeMessage = ref('') // Nuevo ref para el mensaje de bienvenida
  
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  
  function setAuth(newToken, userData) {
    token.value = newToken
    user.value = userData
    
    localStorage.setItem('authToken', newToken) // Cambiado a 'authToken'
    localStorage.setItem('user', JSON.stringify(userData))
  }
  
  // Nueva función para establecer el mensaje de bienvenida
  function setWelcomeMessage(message) {
    welcomeMessage.value = message
    // Opcional: guardar en localStorage si quieres persistencia
    localStorage.setItem('welcomeMessage', message)
  }
  
  // Nueva función para limpiar el mensaje de bienvenida
  function clearWelcomeMessage() {
    welcomeMessage.value = ''
    localStorage.removeItem('welcomeMessage')
  }
  
  function logout() {
    token.value = null
    user.value = null
    welcomeMessage.value = '' // Limpiar el mensaje al hacer logout
    
    localStorage.removeItem('authToken') // Cambiado a 'authToken'
    localStorage.removeItem('user')
    localStorage.removeItem('welcomeMessage') // Limpiar el mensaje almacenado
    
    router.push('/login')
  }
  
  async function checkAuth() {
    if (!token.value) return false
    
    try {
      const response = await api.get('/auth/verify')
      return response.data.valid
    } catch (error) {
      logout()
      return false
    }
  }
  
  // Función para inicializar el estado desde localStorage
  function initialize() {
    const storedToken = localStorage.getItem('authToken')
    const storedUser = localStorage.getItem('user')
    const storedWelcome = localStorage.getItem('welcomeMessage')
    
    if (storedToken) token.value = storedToken
    if (storedUser) user.value = JSON.parse(storedUser)
    if (storedWelcome) welcomeMessage.value = storedWelcome
  }
  
  // Inicializar al crear el store
  initialize()
  
  return {
    token,
    user,
    welcomeMessage, // Exportar el mensaje de bienvenida
    isAuthenticated,
    userRole,
    setAuth,
    setWelcomeMessage, // Exportar la nueva función
    clearWelcomeMessage, // Exportar función de limpieza
    logout,
    checkAuth,
    initialize
  }
})