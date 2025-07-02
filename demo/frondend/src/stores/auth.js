import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '../router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || null)
  
  function setAuth(newToken, userData) {
    token.value = newToken
    user.value = userData
    
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }
  
  function logout() {
    token.value = null
    user.value = null
    
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
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
  
  return {
    token,
    user,
    isAuthenticated,
    userRole,
    setAuth,
    logout,
    checkAuth
  }
})