<template>
  <div class="min-h-screen flex bg-white">
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12">
      <div class="w-full max-w-md space-y-8">
        
        <div class="text-center">
          <div class="flex justify-center mb-4">
            <div class="w-12 h-12 bg-gradient-to-tr from-orange-500 to-yellow-500 rounded-xl flex items-center justify-center shadow-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-7 h-7 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
            </div>
          </div>
          <h2 class="text-3xl font-bold tracking-tight text-gray-900">
            Crear una cuenta
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            Únete a ProjectS-J y gestiona tus proyectos.
          </p>
        </div>

        <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
          
          <div v-if="errorMessage" class="p-4 rounded-lg bg-red-50 border border-red-200 flex items-start">
            <svg class="w-5 h-5 text-red-500 mt-0.5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-sm text-red-700 font-medium">{{ errorMessage }}</span>
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nombre Completo</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
                  </svg>
                </div>
                <input
                  v-model="registerForm.name"
                  type="text"
                  required
                  class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                  placeholder="Ej. Juan Pérez"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Nombre de Usuario</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <input
                  v-model="registerForm.username"
                  type="text"
                  required
                  class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                  :class="{'border-red-500 ring-red-500': errors.username}"
                  placeholder="Ej. juanperez"
                />
              </div>
              <p v-if="errors.username" class="mt-1 text-xs text-red-600">{{ errors.username }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Correo Electrónico</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
                    <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
                  </svg>
                </div>
                <input
                  v-model="registerForm.email"
                  type="email"
                  required
                  class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                  :class="{'border-red-500 ring-red-500': errors.email}"
                  placeholder="juan@empresa.com"
                />
              </div>
              <p v-if="errors.email" class="mt-1 text-xs text-red-600">{{ errors.email }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg class="h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                  </svg>
                </div>
                <input
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  class="block w-full pl-10 pr-10 py-2.5 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all"
                  placeholder="••••••••"
                />
                <button 
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600"
                >
                  <svg v-if="!showPassword" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                     <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a10.05 10.05 0 011.563-3.026m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.542 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                  </svg>
                </button>
              </div>

              <div class="mt-3 grid grid-cols-2 gap-2 text-xs">
                <div 
                    v-for="(req, index) in passwordRequirements" 
                    :key="index"
                    class="flex items-center space-x-1"
                    :class="req.met ? 'text-green-600' : 'text-gray-400'"
                >
                    <div class="w-4 h-4 flex items-center justify-center rounded-full border" 
                        :class="req.met ? 'bg-green-100 border-green-500' : 'border-gray-300'">
                        <svg v-if="req.met" class="w-2.5 h-2.5 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                        </svg>
                    </div>
                    <span>{{ req.label }}</span>
                </div>
              </div>

            </div>
          </div>

          <button
            type="submit"
            :disabled="loading || !isFormValid"
            class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white transition-all duration-200"
            :class="isFormValid && !loading 
                ? 'bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 transform hover:-translate-y-0.5 shadow-orange-500/30' 
                : 'bg-gray-300 cursor-not-allowed'"
          >
            <svg v-if="loading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            {{ loading ? 'Creando cuenta...' : 'Registrarse' }}
          </button>

          <p class="text-center text-sm text-gray-600">
            ¿Ya tienes una cuenta?
            <router-link to="/login" class="font-medium text-orange-600 hover:text-orange-500 hover:underline transition-colors">
              Inicia sesión aquí
            </router-link>
          </p>
        </form>
      </div>
    </div>

    <div class="hidden lg:flex w-1/2 relative overflow-hidden bg-gray-50">
        <div class="absolute inset-0 bg-gradient-to-br from-orange-400 via-orange-500 to-red-500 opacity-90"></div>
        
        <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1522071820081-009f0129c71c?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80')] bg-cover bg-center mix-blend-overlay opacity-30"></div>

        <div class="relative z-10 w-full flex flex-col items-center justify-center p-12 text-center text-white">
            <h1 class="text-4xl font-bold mb-6">Gestiona tus ideas</h1>
            <p class="text-lg max-w-md text-orange-50">
                La plataforma diseñada para equipos que buscan eficiencia, claridad y resultados desde el primer día.
            </p>
            
            <div class="mt-12 relative w-full max-w-sm">
                <div class="absolute top-0 left-0 -ml-10 bg-white/20 backdrop-blur-md p-4 rounded-xl shadow-xl transform -rotate-6 border border-white/30 animate-pulse-slow">
                    <div class="h-2 w-24 bg-white/50 rounded mb-2"></div>
                    <div class="h-2 w-16 bg-white/30 rounded"></div>
                </div>
                <div class="absolute top-4 right-0 -mr-4 bg-white/20 backdrop-blur-md p-4 rounded-xl shadow-xl transform rotate-3 border border-white/30 animate-pulse-slow delay-700">
                    <div class="h-8 w-8 rounded-full bg-green-400/80 mb-2"></div>
                    <div class="h-2 w-20 bg-white/40 rounded"></div>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToastStore } from '@/stores/toast'
import api from '@/api'

const router = useRouter()
const authStore = useAuthStore()
const toastStore = useToastStore()

const loading = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)
const errors = reactive({
  username: '',
  email: ''
})

const registerForm = reactive({
  name: '',
  username: '',
  email: '',
  password: ''
})

// === LOGICA DE VALIDACIÓN DE CONTRASEÑA ===
// Definimos las reglas
const passwordRequirements = computed(() => {
    const pwd = registerForm.password;
    return [
        { label: 'Mínimo 8 caracteres', met: pwd.length >= 8 },
        { label: 'Una mayúscula (A-Z)', met: /[A-Z]/.test(pwd) },
        { label: 'Un número (0-9)', met: /[0-9]/.test(pwd) },
        { label: 'Un símbolo (@$!%*?&)', met: /[@$!%*?&#_.-]/.test(pwd) } // Ajustado para incluir más símbolos comunes
    ];
});

// Verifica si TODAS las reglas se cumplen
const isPasswordValid = computed(() => {
    return passwordRequirements.value.every(req => req.met);
});

// Verifica si todo el formulario es válido para habilitar el botón
const isFormValid = computed(() => {
    return registerForm.name.trim() !== '' &&
           registerForm.username.trim() !== '' &&
           registerForm.email.trim() !== '' &&
           isPasswordValid.value;
});

const handleRegister = async () => {
  // Limpieza inicial
  loading.value = true
  errorMessage.value = ''
  errors.username = ''
  errors.email = ''

  // Validación final por seguridad antes de enviar
  if (!isPasswordValid.value) {
      errorMessage.value = 'La contraseña no cumple con los requisitos de seguridad.';
      loading.value = false;
      return;
  }

  try {
    const response = await api.post('/auth/register', {
        name: registerForm.name,
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password
    })

    const data = response.data 

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

    toastStore.showToast('¡Bienvenido! Tu cuenta ha sido creada.', 'success');
    router.push('/dashboard')

  } catch (error) {
    console.error('Error registro:', error)
    
    const backendMsg = error.response?.data?.error || error.response?.data?.message || 'Error de conexión'
    errorMessage.value = backendMsg

    if (backendMsg.toLowerCase().includes('username')) errors.username = backendMsg
    if (backendMsg.toLowerCase().includes('email')) errors.email = backendMsg
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Animación lenta para los elementos decorativos */
@keyframes pulse-slow {
  0%, 100% { transform: scale(1) rotate(var(--tw-rotate)); opacity: 0.8; }
  50% { transform: scale(1.05) rotate(var(--tw-rotate)); opacity: 1; }
}
.animate-pulse-slow {
  animation: pulse-slow 4s infinite ease-in-out;
}
</style>