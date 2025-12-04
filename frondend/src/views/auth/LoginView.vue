<template>
  <div class="auth-container">
    
    <div class="auth-card">
      <!-- Logo y presentación -->
      <div class="brand-section">
        <div class="logo-container">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#4f46e5" class="logo-icon">
            <path d="M3 3v18h18V3H3zm16 16H5V5h14v14z"/>
            <path d="M7 7h4v4H7zm6 0h4v4h-4zm-6 6h4v4H7zm6 0h4v4h-4z"/>
          </svg>
          <span class="logo-text">ProyectS-J</span>
        </div>
        <p class="app-description">
          La solución integral para gestión de proyectos, equipos y rendimiento empresarial
        </p>
      </div>

      <h1 class="auth-title">Inicio de Sesión</h1>

      <form @submit.prevent="handleLogin" class="auth-form">
        <!-- Mensaje de error -->
        <div v-if="errorMessage" class="error-message">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="error-icon">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          {{ errorMessage }}
        </div>

        <!-- Email Input -->
        <div class="input-group">
          <label for="usernameOrEmail">Usuario o correo electrónico</label>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#6b7280" class="input-icon">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
            </svg>
            <input
              type="text"
              id="usernameOrEmail"
              v-model="loginForm.usernameOrEmail"
              required
              placeholder="Usuario o correo electrónico"
              autocomplete="username"
              :class="{ 'input-error': errorMessage }"
            />
          </div>
        </div>

        <!-- Password Input -->
        <div class="input-group">
          <div class="label-row">
            <label for="password">Contraseña</label>
            <router-link to="/forgot-password" class="forgot-password">¿Olvidaste tu contraseña?</router-link>
          </div>
          <div class="input-wrapper">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#6b7280" class="input-icon">
              <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
            </svg>
            <input
              type="password"
              id="password"
              v-model="loginForm.password"
              required
              placeholder="••••••••"
              autocomplete="current-password"
              :class="{ 'input-error': errorMessage }"
            />
            <button type="button" class="show-password" @click="togglePasswordVisibility">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#6b7280">
                <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Recordar credenciales -->
        <div class="remember-me">
          <input type="checkbox" id="remember" v-model="rememberMe" />
          <label for="remember">Recordar mis credenciales</label>
        </div>

        <!-- Submit Button -->
        <button type="submit" class="auth-button" :disabled="loading">
          <span v-if="!loading">Iniciar sesión</span>
          <span v-else class="button-loading">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="loading-icon">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8z"/>
              <path d="M12 6v6l4 2" style="opacity: 0.5"/>
            </svg>
            Procesando...
          </span>
        </button>

        <!-- OAuth Login -->
        <div class="oauth-section">
          <div class="divider">
            <span>o continuar con</span>
          </div>
          <div class="oauth-buttons">
            <button type="button" class="oauth-button google" @click="loginWithGoogle">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="oauth-icon">
                <path d="M12.545 10.239v3.821h5.445c-0.712 2.315-2.647 3.972-5.445 3.972-3.332 0-6.033-2.701-6.033-6.032s2.701-6.032 6.033-6.032c1.498 0 2.866 0.549 3.921 1.453l2.814-2.814c-1.786-1.667-4.167-2.698-6.735-2.698-5.522 0-10 4.477-10 10s4.478 10 10 10c8.396 0 10-7.524 10-10 0-0.67-0.069-1.325-0.189-1.955h-9.811z"/>
              </svg>
              Google
            </button>
            <button type="button" class="oauth-button microsoft" @click="loginWithMicrosoft">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="oauth-icon">
                <path d="M3 3h8v8H3zm0 10h8v8H3zm10-10h8v8h-8zm0 10h8v8h-8z"/>
              </svg>
              Microsoft
            </button>
          </div>
        </div>

        <!-- Form Footer -->
        <div class="auth-footer">
          <p>¿No tienes una cuenta? <router-link to="/register" class="register-link">Regístrate ahora</router-link></p>
          <p class="demo-info">
            <router-link to="/demo" class="demo-link">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="demo-icon">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.31-8.86c-1.77-.45-2.34-.94-2.34-1.67 0-.84.79-1.43 2.1-1.43 1.38 0 1.9.66 1.94 1.64h1.71c-.05-1.34-.87-2.57-2.49-2.97V5H10.9v1.69c-1.51.32-2.72 1.3-2.72 2.81 0 1.79 1.49 2.69 3.66 3.21 1.95.46 2.34 1.15 2.34 1.87 0 .53-.39 1.39-2.1 1.39-1.6 0-2.23-.72-2.32-1.64H8.04c.1 1.7 1.36 2.66 2.86 2.97V19h2.34v-1.67c1.52-.29 2.72-1.16 2.73-2.77-.01-2.2-1.9-2.96-3.66-3.42z"/>
              </svg>
              Solicitar demo empresarial
            </router-link>
          </p>
        </div>
      </form>
    </div>

    <!-- Características destacadas -->
    <div class="features-container">
      <h3 class="features-title">Optimiza la gestión de tus proyectos empresariales</h3>
      <div class="features-grid">
        <div class="feature-card">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#4f46e5" class="feature-icon">
            <path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
          <h4>Gestión de Proyectos</h4>
          <p>Organiza y prioriza proyectos entre departamentos con seguimiento en tiempo real.</p>
        </div>
        <div class="feature-card">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#4f46e5" class="feature-icon">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.31-8.86c-1.77-.45-2.34-.94-2.34-1.67 0-.84.79-1.43 2.1-1.43 1.38 0 1.9.66 1.94 1.64h1.71c-.05-1.34-.87-2.57-2.49-2.97V5H10.9v1.69c-1.51.32-2.72 1.3-2.72 2.81 0 1.79 1.49 2.69 3.66 3.21 1.95.46 2.34 1.15 2.34 1.87 0 .53-.39 1.39-2.1 1.39-1.6 0-2.23-.72-2.32-1.64H8.04c.1 1.7 1.36 2.66 2.86 2.97V19h2.34v-1.67c1.52-.29 2.72-1.16 2.73-2.77-.01-2.2-1.9-2.96-3.66-3.42z"/>
          </svg>
          <h4>Métricas de Rendimiento</h4>
          <p>Analiza el desempeño de equipos y departamentos con dashboards personalizados.</p>
        </div>
        <div class="feature-card">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#4f46e5" class="feature-icon">
            <path d="M17 10.5V7c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-3.5l4 4v-11l-4 4zM14 13h-3v3H9v-3H6v-2h3V8h2v3h3v2z"/>
          </svg>
          <h4>Colaboración</h4>
          <p>Comunicación integrada entre equipos con asignación clara de responsabilidades.</p>
        </div>
      </div>
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
const rememberMe = ref(false)
const showPassword = ref(false)

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
  const passwordInput = document.getElementById('password')
  if (passwordInput) {
    passwordInput.type = showPassword.value ? 'text' : 'password'
  }
}

const loginWithGoogle = () => {
  errorMessage.value = 'Integración con Google en desarrollo. Por ahora use credenciales normales.'
}

const loginWithMicrosoft = () => {
  errorMessage.value = 'Integración con Microsoft 365 en desarrollo. Por ahora use credenciales normales.'
}

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''

  // Validación básica
  if (!loginForm.value.usernameOrEmail.trim() || !loginForm.value.password.trim()) {
    errorMessage.value = 'Por favor complete todos los campos'
    loading.value = false
    return
  }

  try {
    const response = await api.post('/auth/login', loginForm.value)

    if (!response.data.token) {
      throw new Error('No se recibió token de autenticación')
    }

    // Guardar token
    localStorage.setItem('authToken', response.data.token)

    // Obtener datos del usuario
    const meResponse = await api.get('/auth/me')
    
    const userData = {
      id: meResponse.data.id,
      email: meResponse.data.email,
      role: meResponse.data.role,
      name: response.data.name,
      username: response.data.username
    }

    // Guardar user para el router guard
    localStorage.setItem('user', JSON.stringify(userData))

    // Establecer autenticación en el store
    authStore.setAuth(response.data.token, userData)

    // Redirigir según rol con mensajes más específicos
    const redirectPaths = {
      'ADMIN_GLOBAL': '/homeDepartaments',
      'ADMIN_DEPT': '/departmentHome',
      'ADMIN_COLLAB': '/dashTask',
      'COLLAB': '/homeTask'
    }

    const redirectPath = redirectPaths[userData.role] || '/access-denied'

    // Mostrar mensaje de bienvenida personalizado
    let welcomeMessage = ''
    switch(userData.role) {
      case 'ADMIN_GLOBAL':
        welcomeMessage = `Bienvenido Administrador Global ${userData.name}`
        break
      case 'ADMIN_DEPT':
        welcomeMessage = `Bienvenido Administrador de Departamento ${userData.name}`
        break
      case 'ADMIN_COLLAB':
        welcomeMessage = `Bienvenido Líder de Equipo ${userData.name}`
        break
      default:
        welcomeMessage = `Bienvenido ${userData.name}`
    }

    // Usar el store para mostrar este mensaje en la siguiente vista
    authStore.setWelcomeMessage(welcomeMessage)

    await router.push(redirectPath)

  } catch (error) {
    console.error('Error en login:', error)
    
    // Manejo específico de errores
    if (error.response) {
      switch(error.response.status) {
        case 401:
          errorMessage.value = 'Credenciales incorrectas. Por favor verifique su usuario y contraseña.'
          break
        case 403:
          errorMessage.value = 'Cuenta desactivada. Contacte al administrador.'
          break
        case 429:
          errorMessage.value = 'Demasiados intentos. Espere 5 minutos antes de intentar nuevamente.'
          break
        case 500:
          errorMessage.value = 'Error del servidor. Intente nuevamente más tarde.'
          break
        default:
          errorMessage.value = error.response.data?.error || 
                             error.response.data?.message || 
                             'Error al iniciar sesión'
      }
    } else if (error.request) {
      errorMessage.value = 'No se pudo conectar al servidor. Verifique su conexión a internet.'
    } else {
      errorMessage.value = error.message || 'Error al iniciar sesión'
    }

    // Limpiar credenciales
    authStore.logout()
    localStorage.removeItem('authToken')
    localStorage.removeItem('user')
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
  background-color: #f8fafc;
  padding: 2rem;
  gap: 3rem;
}

.auth-card {
  background: white;
  padding: 2.5rem;
  border-radius: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 450px;
  position: relative;
  z-index: 1;
}

.brand-section {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.logo-icon {
  width: 32px;
  height: 32px;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4f46e5;
}

.app-description {
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.5;
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #1e293b;
  position: relative;
}

.auth-title::after {
  content: '';
  display: block;
  width: 60px;
  height: 4px;
  background: #4f46e5;
  margin: 0.75rem auto 0;
  border-radius: 2px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.error-message {
  background: #fee2e2;
  color: #b91c1c;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.error-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-size: 0.875rem;
  color: #475569;
  font-weight: 500;
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-password {
  font-size: 0.75rem;
  color: #64748b;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-password:hover {
  color: #4f46e5;
  text-decoration: underline;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 0.75rem;
  width: 18px;
  height: 18px;
  pointer-events: none;
}

.input-group input {
  padding: 0.75rem 1rem 0.75rem 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  width: 100%;
  transition: border-color 0.2s, box-shadow 0.2s;
  font-size: 0.95rem;
}

.input-group input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.input-group input.input-error {
  border-color: #dc2626;
}

.show-password {
  position: absolute;
  right: 0.75rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.show-password svg {
  width: 20px;
  height: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #475569;
}

.remember-me input {
  width: 16px;
  height: 16px;
  accent-color: #4f46e5;
}

.auth-button {
  padding: 1rem;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
  font-weight: 600;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.auth-button:hover {
  background-color: #4338ca;
}

.auth-button:active {
  transform: scale(0.98);
}

.auth-button:disabled {
  background-color: #a5b4fc;
  cursor: not-allowed;
  transform: none;
}

.button-loading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.loading-icon {
  width: 18px;
  height: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.oauth-section {
  margin-top: 1rem;
}

.divider {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #64748b;
  font-size: 0.875rem;
  margin: 1rem 0;
}

.divider::before, .divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0;
}

.oauth-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.oauth-button {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.2s;
}

.oauth-button:hover {
  background-color: #f8fafc;
}

.oauth-button.google {
  color: #db4437;
}

.oauth-button.microsoft {
  color: #0078d4;
}

.oauth-icon {
  width: 18px;
  height: 18px;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
  color: #64748b;
}

.auth-footer p {
  margin: 0.5rem 0;
}

.register-link {
  color: #4f46e5;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s;
}

.register-link:hover {
  color: #4338ca;
  text-decoration: underline;
}

.demo-info {
  margin-top: 1.5rem;
}

.demo-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: #4f46e5;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s;
}

.demo-link:hover {
  color: #4338ca;
  text-decoration: underline;
}

.demo-icon {
  width: 18px;
  height: 18px;
}

.features-container {
  max-width: 600px;
  padding: 2rem;
  background: white;
  border-radius: 1rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
}

.features-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
}

.features-title::after {
  content: '';
  display: block;
  width: 80px;
  height: 4px;
  background: #4f46e5;
  margin: 1rem auto 0;
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.feature-card {
  background: #f8fafc;
  padding: 1.5rem;
  border-radius: 0.75rem;
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 40px;
  height: 40px;
  margin-bottom: 1rem;
}

.feature-card h4 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.5;
}

@media (max-width: 1024px) {
  .auth-container {
    flex-direction: column;
    padding: 1.5rem;
  }
  
  .features-container {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .auth-card {
    padding: 1.5rem;
  }
  
  .oauth-buttons {
    flex-direction: column;
  }
}
</style>