<template>
  <div class="min-h-screen flex items-center justify-center bg-[#0f172a] relative overflow-hidden">
    <div class="absolute top-0 left-0 w-full h-full overflow-hidden z-0">
      <div class="absolute -top-[20%] -left-[10%] w-[50%] h-[50%] rounded-full bg-purple-600/20 blur-[120px] animate-pulse-slow"></div>
      <div class="absolute top-[40%] -right-[10%] w-[40%] h-[40%] rounded-full bg-blue-600/20 blur-[120px] animate-pulse-slow delay-1000"></div>
      <div class="absolute -bottom-[10%] left-[20%] w-[30%] h-[30%] rounded-full bg-indigo-600/20 blur-[100px] animate-pulse-slow delay-2000"></div>
    </div>

    <div class="w-full max-w-md z-10 p-4">
      <div class="bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl shadow-2xl overflow-hidden transform transition-all duration-300">
        
        <div class="px-8 pt-8 pb-6 text-center">
          <div class="inline-flex items-center justify-center p-3 bg-gradient-to-tr from-indigo-500 to-purple-500 rounded-xl shadow-lg mb-4 transform hover:rotate-12 transition-transform duration-300">
            <LayoutDashboard class="w-8 h-8 text-white" />
          </div>
          <h2 class="text-3xl font-bold text-white tracking-tight">
            Únete a <span class="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-purple-400">ProjectS-J</span>
          </h2>
          <p class="text-slate-400 mt-2 text-sm">Gestiona tus proyectos al siguiente nivel 🚀</p>
        </div>

        <div class="px-8 pb-8">
          
          <transition name="fade">
            <div v-if="errorMessage" class="mb-5 p-3 rounded-lg bg-red-500/10 border border-red-500/20 flex items-center gap-3">
              <AlertCircle class="w-5 h-5 text-red-400 flex-shrink-0" />
              <span class="text-sm text-red-300">{{ errorMessage }}</span>
            </div>
          </transition>

          <form @submit.prevent="handleRegister" class="space-y-5">
            
            <div class="group">
              <label class="block text-xs font-medium text-slate-400 mb-1 ml-1 uppercase tracking-wider">Nombre Completo</label>
              <div class="relative">
                <User class="absolute left-3 top-3.5 w-5 h-5 text-slate-500 transition-colors group-focus-within:text-indigo-400" />
                <input 
                  v-model="registerForm.fullName" 
                  @blur="validateFullName"
                  type="text" 
                  placeholder="Ej. Juan Pérez"
                  class="w-full bg-slate-800/50 text-white border border-slate-700 rounded-xl py-3 pl-10 pr-4 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all placeholder:text-slate-600"
                  :class="{'!border-red-500/50 focus:!ring-red-500/50': errors.fullName}"
                />
              </div>
              <p v-if="errors.fullName" class="text-xs text-red-400 mt-1 ml-1">{{ errors.fullName }}</p>
            </div>

            <div class="group">
              <label class="block text-xs font-medium text-slate-400 mb-1 ml-1 uppercase tracking-wider">Usuario</label>
              <div class="relative">
                <AtSign class="absolute left-3 top-3.5 w-5 h-5 text-slate-500 transition-colors group-focus-within:text-indigo-400" />
                <input 
                  v-model="registerForm.username" 
                  @blur="validateUsername"
                  type="text" 
                  placeholder="usuario_pro"
                  class="w-full bg-slate-800/50 text-white border border-slate-700 rounded-xl py-3 pl-10 pr-4 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all placeholder:text-slate-600"
                  :class="{'!border-red-500/50 focus:!ring-red-500/50': errors.username}"
                />
              </div>
              <p v-if="errors.username" class="text-xs text-red-400 mt-1 ml-1">{{ errors.username }}</p>
            </div>

            <div class="group">
              <label class="block text-xs font-medium text-slate-400 mb-1 ml-1 uppercase tracking-wider">Correo Electrónico</label>
              <div class="relative">
                <Mail class="absolute left-3 top-3.5 w-5 h-5 text-slate-500 transition-colors group-focus-within:text-indigo-400" />
                <input 
                  v-model="registerForm.email" 
                  @blur="validateEmail"
                  type="email" 
                  placeholder="nombre@empresa.com"
                  class="w-full bg-slate-800/50 text-white border border-slate-700 rounded-xl py-3 pl-10 pr-4 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all placeholder:text-slate-600"
                  :class="{'!border-red-500/50 focus:!ring-red-500/50': errors.email}"
                />
              </div>
              <p v-if="errors.email" class="text-xs text-red-400 mt-1 ml-1">{{ errors.email }}</p>
            </div>

            <div class="group">
              <label class="block text-xs font-medium text-slate-400 mb-1 ml-1 uppercase tracking-wider">Contraseña</label>
              <div class="relative">
                <Lock class="absolute left-3 top-3.5 w-5 h-5 text-slate-500 transition-colors group-focus-within:text-indigo-400" />
                <input 
                  v-model="registerForm.password" 
                  @input="validatePassword"
                  :type="showPassword ? 'text' : 'password'" 
                  placeholder="••••••••"
                  class="w-full bg-slate-800/50 text-white border border-slate-700 rounded-xl py-3 pl-10 pr-10 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all placeholder:text-slate-600"
                  :class="{'!border-red-500/50 focus:!ring-red-500/50': errors.password}"
                />
                <button type="button" @click="showPassword = !showPassword" class="absolute right-3 top-3.5 text-slate-500 hover:text-white transition-colors">
                  <Eye v-if="showPassword" class="w-5 h-5" />
                  <EyeOff v-else class="w-5 h-5" />
                </button>
              </div>
              <div class="mt-2">
                 <PasswordStrengthMeter :password="registerForm.password" />
              </div>
              <p v-if="errors.password" class="text-xs text-red-400 mt-1 ml-1">{{ errors.password }}</p>
            </div>

            <div class="group">
              <label class="block text-xs font-medium text-slate-400 mb-1 ml-1 uppercase tracking-wider">Confirmar Contraseña</label>
              <div class="relative">
                <CheckCircle2 class="absolute left-3 top-3.5 w-5 h-5 text-slate-500 transition-colors group-focus-within:text-indigo-400" />
                <input 
                  v-model="registerForm.confirmPassword" 
                  @blur="validateConfirmPassword"
                  :type="showPassword ? 'text' : 'password'" 
                  placeholder="••••••••"
                  class="w-full bg-slate-800/50 text-white border border-slate-700 rounded-xl py-3 pl-10 pr-4 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 transition-all placeholder:text-slate-600"
                  :class="{'!border-red-500/50 focus:!ring-red-500/50': errors.confirmPassword}"
                />
              </div>
              <p v-if="errors.confirmPassword" class="text-xs text-red-400 mt-1 ml-1">{{ errors.confirmPassword }}</p>
            </div>

            <div class="flex items-center gap-3 pt-2">
              <div class="relative flex items-center">
                <input 
                  id="terms" 
                  v-model="registerForm.acceptedTerms" 
                  type="checkbox" 
                  class="peer h-5 w-5 cursor-pointer appearance-none rounded-md border border-slate-600 bg-slate-800 transition-all checked:border-indigo-500 checked:bg-indigo-500 hover:border-indigo-400"
                />
                <Check class="pointer-events-none absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-3.5 h-3.5 text-white opacity-0 peer-checked:opacity-100 transition-opacity" />
              </div>
              <label for="terms" class="text-sm text-slate-300 cursor-pointer select-none">
                Acepto los <span class="text-indigo-400 hover:text-indigo-300 underline">Términos y Condiciones</span>
              </label>
            </div>
            <p v-if="errors.terms" class="text-xs text-red-400 ml-1">{{ errors.terms }}</p>

            <button 
              type="submit" 
              :disabled="loading || !isFormValid"
              class="w-full py-3.5 px-4 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold rounded-xl shadow-lg shadow-indigo-500/30 transform transition-all duration-200 hover:-translate-y-0.5 active:translate-y-0 disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none flex items-center justify-center gap-2"
            >
              <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
              <span v-else>Crear Cuenta</span>
              <ArrowRight v-if="!loading" class="w-5 h-5" />
            </button>

          </form>

          <div class="mt-8 text-center border-t border-slate-700/50 pt-6">
            <p class="text-slate-400 text-sm">
              ¿Ya tienes cuenta? 
              <router-link to="/login" class="text-indigo-400 font-semibold hover:text-indigo-300 transition-colors inline-flex items-center gap-1">
                Inicia sesión aquí
              </router-link>
            </p>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '@/api' // ✅ Importación corregida (Axios inteligente)
import PasswordStrengthMeter from '../../components/auth/PasswordStrengthMeter.vue'
// Iconos modernos de Lucide (usando la librería que ya tienes)
import { 
  LayoutDashboard, User, AtSign, Mail, Lock, Eye, EyeOff, 
  CheckCircle2, Check, ArrowRight, Loader2, AlertCircle 
} from 'lucide-vue-next'

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

// Validaciones
const validateFullName = () => {
  if (!registerForm.fullName.trim()) errors.fullName = 'El nombre es obligatorio'
  else if (registerForm.fullName.trim().length < 3) errors.fullName = 'Mínimo 3 caracteres'
  else errors.fullName = ''
}

const validateUsername = () => {
  const usernameRegex = /^[a-zA-Z0-9_]{4,20}$/
  if (!registerForm.username.trim()) errors.username = 'El usuario es obligatorio'
  else if (!usernameRegex.test(registerForm.username)) errors.username = '4-20 caracteres (letras, números, _)'
  else errors.username = ''
}

const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!registerForm.email.trim()) errors.email = 'El correo es obligatorio'
  else if (!emailRegex.test(registerForm.email)) errors.email = 'Correo inválido'
  else errors.email = ''
}

const validatePassword = () => {
  if (!registerForm.password) errors.password = 'La contraseña es obligatoria'
  else if (registerForm.password.length < 8) errors.password = 'Mínimo 8 caracteres'
  else errors.password = ''
  
  if (registerForm.confirmPassword) validateConfirmPassword()
}

const validateConfirmPassword = () => {
  if (!registerForm.confirmPassword) errors.confirmPassword = 'Confirma tu contraseña'
  else if (registerForm.password !== registerForm.confirmPassword) errors.confirmPassword = 'Las contraseñas no coinciden'
  else errors.confirmPassword = ''
}

const isFormValid = computed(() => {
  return (
    registerForm.fullName && registerForm.username && registerForm.email && 
    registerForm.password && registerForm.confirmPassword && registerForm.acceptedTerms &&
    !errors.fullName && !errors.username && !errors.email && 
    !errors.password && !errors.confirmPassword
  )
})

const handleRegister = async () => {
  validateFullName(); validateUsername(); validateEmail(); 
  validatePassword(); validateConfirmPassword();

  if (!registerForm.acceptedTerms) {
    errors.terms = 'Debes aceptar los términos'
    return
  } else {
    errors.terms = ''
  }

  if (!isFormValid.value) return

  loading.value = true
  errorMessage.value = ''

  try {
    // ✅ CORRECCIÓN TÉCNICA: Usamos api.post sin .json()
    const response = await api.post('/auth/register', {
        name: registerForm.fullName,
        username: registerForm.username,
        email: registerForm.email,
        password: registerForm.password
    })

    // Con axios, la respuesta ya está en .data
    const data = response.data 

    // Guardar sesión y estado
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
    console.error('Error registro:', error)
    
    // Manejo de errores seguro para Axios
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
/* Animación suave para el fondo */
@keyframes pulse-slow {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.5; }
}
.animate-pulse-slow {
  animation: pulse-slow 8s infinite ease-in-out;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>