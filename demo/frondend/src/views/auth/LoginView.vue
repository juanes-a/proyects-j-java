<template>
  <div class="auth-container">
    <div class="auth-card">
      <h1 class="auth-title">Iniciar sesión en ProyectS-J</h1>
      
      <form @submit.prevent="handleLogin" class="auth-form">
        <!-- Email Input -->
        <div class="input-group">
          <label for="usernameOrEmail">Usuario o correo electrónico</label>
          <input
            type="text"
            id="usernameOrEmail"
            v-model="loginForm.usernameOrEmail"
            required
            placeholder="Usuario o correo electrónico"
            autocomplete="username"
          />
        </div>
        
        <!-- Password Input -->
        <div class="input-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            placeholder="••••••••"
            autocomplete="current-password"
          />
        </div>
        
        <!-- Submit Button -->
        <button type="submit" class="auth-button" :disabled="loading">
          <span v-if="!loading">Iniciar sesión</span>
          <span v-else>Procesando...</span>
        </button>
        
        <!-- Form Footer -->
        <div class="auth-footer">
          <p>¿No tienes una cuenta? <router-link to="/register">Regístrate</router-link></p>
          <p><router-link to="/forgot-password">¿Olvidaste tu contraseña?</router-link></p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../api'

const router = useRouter()
const authStore = useAuthStore()

const loginForm = ref({
  usernameOrEmail: '',
  password: ''
})

const loading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    console.log('🚀 Iniciando login con datos:', loginForm.value)
    
    // 1. Hacer login
    const response = await api.post('/auth/login', loginForm.value)
    console.log('✅ Login response:', response.data)
    console.log('✅ Status code:', response.status)
    
    if (!response.data.token) {
      throw new Error('No se recibió token de autenticación')
    }

    // 2. Guardar token inmediatamente (con la key correcta para el router)
    localStorage.setItem('authToken', response.data.token)  // 👈 Cambié 'token' por 'authToken'
    console.log('✅ Token guardado en localStorage como authToken')

    // 3. Probar endpoint /me con un pequeño delay
    console.log('🔍 Probando endpoint /auth/me...')
    
    // Pequeño delay para asegurar que el token esté disponible
    await new Promise(resolve => setTimeout(resolve, 100))
    
    const meResponse = await api.get('/auth/me')
    console.log('✅ /auth/me response:', meResponse.data)

    // 4. Guardar datos completos
    const userData = {
      id: meResponse.data.id,
      email: meResponse.data.email,
      role: meResponse.data.role,
      name: response.data.name,
      username: response.data.username
    }

    // 5. Guardar user para el router guard
    localStorage.setItem('user', JSON.stringify(userData))  // 👈 Agregué esto
    console.log('✅ Usuario guardado en localStorage:', userData)

    // 6. Establecer autenticación en el store
    authStore.setAuth(response.data.token, userData)
    console.log('✅ Auth establecida completamente')

    // 6. Redirigir según rol
    const redirectPaths = {
      'ADMIN_GLOBAL': '/homeDepartaments',
      'ADMIN_DEPT': '/departmentHome', 
      'COLLAB': '/collab/home'
    }
    
    const redirectPath = redirectPaths[userData.role] || '/access-denied'
    
    console.log('🔄 Rol del usuario:', userData.role)
    console.log('🔄 Ruta calculada:', redirectPath)
    console.log('🔄 Rutas disponibles:', Object.keys(redirectPaths))
    
    try {
      console.log('🔄 Intentando redirección...')
      await router.push(redirectPath)
      console.log('✅ Redirección exitosa')
    } catch (navError) {
      console.error('❌ Error en navegación:', navError)
      console.log('🔄 Intentando redirección alternativa...')
      
      // Intenta una redirección alternativa
      window.location.href = redirectPath
    }

  } catch (error) {
    console.error('❌ Error completo:', error)
    console.error('❌ Response data:', error.response?.data)
    console.error('❌ Status:', error.response?.status)
    console.error('❌ Request config:', error.config)
    
    errorMessage.value = error.response?.data?.error || 
                        error.response?.data?.message || 
                        error.message || 
                        'Error al iniciar sesión'
    
    // Limpiar credenciales
    authStore.logout()
    localStorage.removeItem('authToken')  // 👈 Cambié por 'authToken'
    localStorage.removeItem('user')       // 👈 Agregué limpieza del user
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f3f4f6;
}

.auth-card {
  background: white;
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.auth-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #1f2937;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-size: 0.875rem;
  color: #4b5563;
}

.input-group input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
}

.auth-button {
  padding: 0.75rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.auth-button:hover {
  background-color: #4338ca;
}

.auth-button:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.875rem;
  color: #6b7280;
}

.auth-footer a {
  color: #4f46e5;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
}
</style>