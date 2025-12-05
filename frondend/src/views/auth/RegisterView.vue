<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
    <!-- Encabezado con animación -->
    <div class="sm:mx-auto sm:w-full sm:max-w-md text-center transform transition-all duration-500 hover:scale-105">
      <div class="flex justify-center mb-4">
        <div class="bg-white p-3 rounded-full shadow-lg animate-bounce">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="#4f46e5" class="w-12 h-12">
            <path d="M3 3v18h18V3H3zm16 16H5V5h14v14z"/>
            <path d="M7 7h4v4H7zm6 0h4v4h-4zm-6 6h4v4H7zm6 0h4v4h-4z"/>
          </svg>
        </div>
      </div>
      <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900 font-sans">
        Únete a <span class="text-indigo-600">ProyectS-J</span>
      </h2>
      <p class="mt-2 text-center text-sm text-gray-600">
        Donde los proyectos cobran vida
        <span class="inline-block animate-pulse">✨</span>
      </p>
    </div>

    <!-- Tarjeta de formulario -->
    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white/90 backdrop-blur-sm py-8 px-6 shadow-xl rounded-2xl border border-white/20 relative overflow-hidden">
        <!-- Efecto de decoración abstracta -->
        <div class="absolute -top-20 -right-20 w-40 h-40 bg-indigo-200 rounded-full mix-blend-multiply filter blur-xl opacity-30"></div>
        <div class="absolute -bottom-20 -left-20 w-40 h-40 bg-blue-200 rounded-full mix-blend-multiply filter blur-xl opacity-30"></div>
        
        <div class="relative">
          <!-- Pestaña única de Registro -->
          <div class="flex mb-6 border-b border-gray-200">
            <div class="w-full py-2 text-center font-medium text-indigo-600 border-b-2 border-indigo-600">
              Registrarse
            </div>
          </div>

          <!-- Mensaje de error -->
          <div v-if="errorMessage" class="mb-4 bg-red-50 border-l-4 border-red-500 p-4 rounded-lg animate-shake">
            <div class="flex items-center">
              <svg class="h-5 w-5 text-red-500 flex-shrink-0" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
              </svg>
              <p class="ml-3 text-sm text-red-700">{{ errorMessage }}</p>
            </div>
          </div>

          <form class="space-y-5" @submit.prevent="handleRegister">
            <!-- Grupo de campos -->
            <div class="space-y-4">
              <!-- Nombre completo -->
              <div>
                <label for="fullName" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                  </svg>
                  Nombre completo
                </label>
                <div class="mt-1 relative rounded-lg shadow-sm transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500">
                  <input id="fullName" v-model="registerForm.fullName" @blur="validateFullName" type="text" autocomplete="name" required
                    class="block w-full pl-3 pr-3 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-0 sm:text-sm"
                    :class="{'border-red-300': errors.fullName, 'border-green-300': registerForm.fullName && !errors.fullName}"
                    placeholder="Ej. María González"
                  />
                </div>
                <p v-if="errors.fullName" class="mt-1 text-xs text-red-600 animate-fade-in">{{ errors.fullName }}</p>
              </div>

              <!-- Username -->
              <div>
                <label for="username" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M5 4a1 1 0 00-2 0v7.268a2 2 0 000 3.464V16a1 1 0 102 0v-1.268a2 2 0 000-3.464V4zM11 4a1 1 0 10-2 0v1.268a2 2 0 000 3.464V16a1 1 0 102 0V8.732a2 2 0 000-3.464V4zM16 3a1 1 0 011 1v7.268a2 2 0 010 3.464V16a1 1 0 11-2 0v-1.268a2 2 0 010-3.464V4a1 1 0 011-1z" />
                  </svg>
                  Nombre de usuario
                </label>
                <div class="mt-1 relative rounded-lg shadow-sm transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500">
                  <input id="username" v-model="registerForm.username" @blur="validateUsername" type="text" autocomplete="username" required
                    class="block w-full pl-3 pr-3 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-0 sm:text-sm"
                    :class="{'border-red-300': errors.username, 'border-green-300': registerForm.username && !errors.username}"
                    placeholder="Ej. mgonzalez2023"
                  />
                </div>
                <p v-if="errors.username" class="mt-1 text-xs text-red-600 animate-fade-in">{{ errors.username }}</p>
                <p class="mt-1 text-xs text-gray-500">Entre 4-20 caracteres, solo letras, números y guiones bajos</p>
              </div>

              <!-- Email -->
              <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                    <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                  </svg>
                  Correo corporativo
                </label>
                <div class="mt-1 relative rounded-lg shadow-sm transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500">
                  <input id="email" v-model="registerForm.email" @blur="validateEmail" type="email" autocomplete="email" required
                    class="block w-full pl-3 pr-3 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-0 sm:text-sm"
                    :class="{'border-red-300': errors.email, 'border-green-300': registerForm.email && !errors.email}"
                    placeholder="tu@empresa.com"
                  />
                </div>
                <p v-if="errors.email" class="mt-1 text-xs text-red-600 animate-fade-in">{{ errors.email }}</p>
              </div>

              <!-- Contraseña -->
              <div>
                <label for="password" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                  </svg>
                  Crea tu contraseña
                </label>
                <div class="mt-1 relative rounded-lg shadow-sm transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500">
                  <input id="password" v-model="registerForm.password" @input="validatePassword" :type="showPassword ? 'text' : 'password'" autocomplete="new-password" required
                    class="block w-full pl-3 pr-10 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-0 sm:text-sm"
                    :class="{'border-red-300': errors.password, 'border-green-300': registerForm.password && !errors.password}"
                    placeholder="••••••••"
                  />
                  <button type="button" @click="togglePasswordVisibility" class="absolute inset-y-0 right-0 pr-3 flex items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 hover:text-indigo-500 transition-colors" viewBox="0 0 20 20" fill="currentColor">
                      <path v-if="showPassword" d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                      <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd" />
                    </svg>
                  </button>
                </div>
                <PasswordStrengthMeter :password="registerForm.password" class="mt-2" />
                <p v-if="errors.password" class="mt-1 text-xs text-red-600 animate-fade-in">{{ errors.password }}</p>
              </div>

              <!-- Confirmar Contraseña -->
              <div>
                <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1 text-indigo-500" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M2.166 4.999A11.954 11.954 0 0010 1.944 11.954 11.954 0 0017.834 5c.11.65.166 1.32.166 2.001 0 5.225-3.34 9.67-8 11.317C5.34 16.67 2 12.225 2 7c0-.682.057-1.35.166-2.001zm11.541 3.708a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                  </svg>
                  Confirmar contraseña
                </label>
                <div class="mt-1 relative rounded-lg shadow-sm transition-all duration-300 focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500">
                  <input id="confirmPassword" v-model="registerForm.confirmPassword" @blur="validateConfirmPassword" :type="showPassword ? 'text' : 'password'" autocomplete="new-password" required
                    class="block w-full pl-3 pr-3 py-3 border border-gray-300 rounded-lg placeholder-gray-400 focus:outline-none focus:ring-0 sm:text-sm"
                    :class="{'border-red-300': errors.confirmPassword, 'border-green-300': registerForm.confirmPassword && !errors.confirmPassword && registerForm.password === registerForm.confirmPassword}"
                    placeholder="••••••••"
                  />
                </div>
                <p v-if="errors.confirmPassword" class="mt-1 text-xs text-red-600 animate-fade-in">{{ errors.confirmPassword }}</p>
              </div>
            </div>

            <!-- Términos y condiciones -->
            <div class="flex items-start">
              <div class="flex items-center h-5">
                <input id="terms" v-model="registerForm.acceptedTerms" type="checkbox" required
                  class="focus:ring-indigo-500 h-4 w-4 text-indigo-600 border-gray-300 rounded"
                />
              </div>
              <div class="ml-3 text-sm">
                <label for="terms" class="font-medium text-gray-700">Acepto los términos y condiciones</label>
                <p class="text-gray-500">Al registrarte, aceptas nuestras <a href="#" class="text-indigo-600 hover:text-indigo-500">Condiciones de Servicio</a> y <a href="#" class="text-indigo-600 hover:text-indigo-500">Política de Privacidad</a>.</p>
              </div>
            </div>
            <p v-if="errors.terms" class="mt-1 text-sm text-red-600">{{ errors.terms }}</p>

            <!-- Botón de registro -->
            <div>
              <button type="submit" :disabled="loading || !isFormValid"
                class="w-full flex justify-center items-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-gradient-to-r from-indigo-600 to-blue-500 hover:from-indigo-700 hover:to-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all duration-300 transform hover:scale-[1.02] disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
              >
                <span v-if="loading" class="flex items-center">
                  <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Creando cuenta...
                </span>
                <span v-else class="flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2H7a1 1 0 100 2h2v2a1 1 0 102 0v-2h2a1 1 0 100-2h-2V7z" clip-rule="evenodd" />
                  </svg>
                  Crear mi cuenta
                </span>
              </button>
            </div>
          </form>

          <!-- Redes sociales -->
          <div class="mt-6">
            <div class="relative">
              <div class="absolute inset-0 flex items-center">
                <div class="w-full border-t border-gray-300"></div>
              </div>
              <div class="relative flex justify-center text-sm">
                <span class="px-2 bg-white text-gray-500">o regístrate con</span>
              </div>
            </div>

            <div class="mt-6 grid grid-cols-2 gap-3">
              <button type="button" @click="loginWithGoogle"
                class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-300"
              >
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12.545 10.239v3.821h5.445c-0.712 2.315-2.647 3.972-5.445 3.972-3.332 0-6.033-2.701-6.033-6.032s2.701-6.032 6.033-6.032c1.498 0 2.866 0.549 3.921 1.453l2.814-2.814c-1.786-1.667-4.167-2.698-6.735-2.698-5.522 0-10 4.477-10 10s4.478 10 10 10c8.396 0 10-7.524 10-10 0-0.67-0.069-1.325-0.189-1.955h-9.811z"/>
                </svg>
                <span class="ml-2">Google</span>
              </button>

              <button type="button" @click="loginWithMicrosoft"
                class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors duration-300"
              >
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M3 3h8v8H3zm0 10h8v8H3zm10-10h8v8h-8zm0 10h8v8h-8z"/>
                </svg>
                <span class="ml-2">Microsoft</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-6 text-center text-sm text-gray-600">
        <p>¿Ya tienes una cuenta? <router-link to="/login" class="font-medium text-indigo-600 hover:text-indigo-500 transition-colors">Inicia sesión aquí</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import PasswordStrengthMeter from '../../components/auth/PasswordStrengthMeter.vue'
import api from '../../api'

const router = useRouter()
const authStore = useAuthStore()

const registerForm = reactive({
  fullName: '',
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptedTerms: false
})

const errors = reactive({
  fullName: '',
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  terms: ''
})

const showPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const validateFullName = () => {
  if (!registerForm.fullName.trim()) {
    errors.fullName = 'Por favor ingresa tu nombre completo'
  } else if (registerForm.fullName.trim().length < 3) {
    errors.fullName = 'El nombre debe tener al menos 3 caracteres'
  } else {
    errors.fullName = ''
  }
}

const validateUsername = () => {
  const usernameRegex = /^[a-zA-Z0-9_]{4,20}$/
  if (!registerForm.username.trim()) {
    errors.username = 'Por favor ingresa un nombre de usuario'
  } else if (!usernameRegex.test(registerForm.username)) {
    errors.username = 'Solo letras, números y guiones bajos (4-20 caracteres)'
  } else {
    errors.username = ''
  }
}

const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!registerForm.email.trim()) {
    errors.email = 'Por favor ingresa tu correo electrónico'
  } else if (!emailRegex.test(registerForm.email)) {
    errors.email = 'Por favor ingresa un correo electrónico válido'
  } else {
    errors.email = ''
  }
}

const validatePassword = () => {
  if (!registerForm.password) {
    errors.password = 'Por favor ingresa una contraseña'
  } else if (registerForm.password.length < 8) {
    errors.password = 'La contraseña debe tener al menos 8 caracteres'
  } else {
    errors.password = ''
  }

  if (registerForm.confirmPassword) {
    validateConfirmPassword()
  }
}

const validateConfirmPassword = () => {
  if (!registerForm.confirmPassword) {
    errors.confirmPassword = 'Por favor confirma tu contraseña'
  } else if (registerForm.password !== registerForm.confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden'
  } else {
    errors.confirmPassword = ''
  }
}

const validateTerms = () => {
  if (!registerForm.acceptedTerms) {
    errors.terms = 'Debes aceptar los términos y condiciones'
  } else {
    errors.terms = ''
  }
}

const isFormValid = computed(() => {
  return (
    registerForm.fullName &&
    registerForm.username &&
    registerForm.email &&
    registerForm.password &&
    registerForm.confirmPassword &&
    registerForm.acceptedTerms &&
    !errors.fullName &&
    !errors.username &&
    !errors.email &&
    !errors.password &&
    !errors.confirmPassword &&
    !errors.terms
  )
})

const loginWithGoogle = () => {
  errorMessage.value = 'Registro con Google estará disponible pronto'
}

const loginWithMicrosoft = () => {
  errorMessage.value = 'Registro con Microsoft estará disponible pronto'
}

const handleRegister = async () => {
  validateFullName()
  validateUsername()
  validateEmail()
  validatePassword()
  validateConfirmPassword()
  validateTerms()

  if (!isFormValid.value) {
    errorMessage.value = 'Por favor completa todos los campos correctamente'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await api.post('/auth/register', {
        name: registerForm.fullName,
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password
    })

    const data = await response.json()

    if (!response.ok) {
      throw new Error(data.error || 'Error al registrarse')
    }

    localStorage.setItem('authToken', data.token)
    localStorage.setItem('user', JSON.stringify({
      name: data.name,
      username: data.username,
      email: data.email,
      role: data.role || 'COLLAB'
    }))

    authStore.setAuth(data.token, {
      name: data.name,
      username: data.username,
      email: data.email,
      role: data.role || 'COLLAB'
    })

    router.push('/dashboard')
  } catch (error) {
    console.error('Error en el registro:', error)
    errorMessage.value = error.message || 'Ocurrió un error al registrar la cuenta'
    
    if (error.message.includes('username')) {
      errors.username = 'Este nombre de usuario ya está en uso'
    } else if (error.message.includes('email')) {
      errors.email = 'Este correo electrónico ya está registrado'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style>
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-5px); }
  40%, 80% { transform: translateX(5px); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-shake {
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out forwards;
}
</style>