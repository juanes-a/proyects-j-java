<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300 p-4 sm:p-8">
    <div class="max-w-4xl mx-auto space-y-6">
      
      <div class="flex items-center space-x-4 mb-8">
        <div class="p-3 bg-blue-600 rounded-lg shadow-lg">
          <SettingsIcon class="w-8 h-8 text-white" />
        </div>
        <div>
          <h1 class="text-2xl font-bold text-gray-800 dark:text-white">Configuración</h1>
          <p class="text-gray-500 dark:text-gray-400">Gestiona tu perfil y preferencias</p>
        </div>
      </div>

      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden border border-gray-100 dark:border-gray-700">
        <div class="flex flex-col md:flex-row">
          
          <nav class="md:w-64 bg-gray-50/50 dark:bg-gray-800/50 p-4 border-b md:border-b-0 md:border-r border-gray-200 dark:border-gray-700">
            <div class="space-y-1">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="currentTab = tab.id"
                :class="[
                  'w-full flex items-center space-x-3 px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200',
                  currentTab === tab.id
                    ? 'bg-blue-50 text-blue-600 dark:bg-blue-900/20 dark:text-blue-400 shadow-sm'
                    : 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700'
                ]"
              >
                <component :is="tab.icon" class="w-5 h-5" />
                <span>{{ tab.label }}</span>
              </button>
            </div>
          </nav>

          <div class="flex-1 p-6 sm:p-8">
            
            <div v-if="currentTab === 'profile'" class="space-y-6">
              <h2 class="text-xl font-semibold text-gray-800 dark:text-white mb-4">Información Personal</h2>
              <form @submit.prevent="updateProfile" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nombre Completo</label>
                    <div class="relative">
                      <User class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                      <input 
                        v-model="profileForm.name" 
                        type="text" 
                        class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-colors"
                        required
                      />
                    </div>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nombre de Usuario</label>
                    <div class="relative">
                      <AtSign class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                      <input 
                        v-model="profileForm.username" 
                        type="text" 
                        class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-colors"
                        required
                      />
                    </div>
                  </div>
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Correo Electrónico</label>
                  <div class="relative opacity-70">
                    <Mail class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input 
                      v-model="profileForm.email" 
                      type="email" 
                      disabled
                      class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-gray-100 dark:bg-gray-800 text-gray-500 dark:text-gray-400 cursor-not-allowed"
                    />
                  </div>
                  <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">El correo electrónico no se puede cambiar.</p>
                </div>

                <div class="pt-4 flex justify-end">
                  <button 
                    type="submit" 
                    :disabled="loading"
                    class="flex items-center space-x-2 px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <Loader2 v-if="loading" class="w-4 h-4 animate-spin" />
                    <span>{{ loading ? 'Guardando...' : 'Guardar Cambios' }}</span>
                  </button>
                </div>
              </form>
            </div>

            <div v-if="currentTab === 'security'" class="space-y-6">
              <h2 class="text-xl font-semibold text-gray-800 dark:text-white mb-4">Cambiar Contraseña</h2>
              <form @submit.prevent="changePassword" class="space-y-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Contraseña Actual</label>
                  <div class="relative">
                    <Lock class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input 
                      v-model="passwordForm.currentPassword" 
                      type="password" 
                      class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none"
                      required
                    />
                  </div>
                </div>

                <div class="space-y-2">
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Nueva Contraseña</label>
                  <div class="relative">
                    <Key class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input 
                      v-model="passwordForm.newPassword" 
                      type="password" 
                      class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none"
                      required
                    />
                  </div>
                  <PasswordStrengthMeter :password="passwordForm.newPassword" />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Confirmar Nueva Contraseña</label>
                  <div class="relative">
                    <Key class="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                    <input 
                      v-model="passwordForm.confirmPassword" 
                      type="password" 
                      class="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none"
                      required
                    />
                  </div>
                  <p v-if="passwordError" class="mt-1 text-sm text-red-500">{{ passwordError }}</p>
                </div>

                <div class="pt-4 flex justify-end">
                  <button 
                    type="submit" 
                    :disabled="loading || !!passwordError"
                    class="flex items-center space-x-2 px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <Loader2 v-if="loading" class="w-4 h-4 animate-spin" />
                    <span>Actualizar Contraseña</span>
                  </button>
                </div>
              </form>
            </div>

            <div v-if="currentTab === 'appearance'" class="space-y-6">
              <h2 class="text-xl font-semibold text-gray-800 dark:text-white mb-4">Apariencia del Sistema</h2>
              
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <button
                  v-for="theme in themes"
                  :key="theme.value"
                  @click="themeStore.setTheme(theme.value)"
                  :class="[
                    'relative p-4 rounded-xl border-2 transition-all duration-200 flex flex-col items-center space-y-3',
                    themeStore.theme === theme.value
                      ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
                      : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
                  ]"
                >
                  <div :class="['w-full h-24 rounded-lg shadow-inner mb-2', theme.previewClass]"></div>
                  <div class="flex items-center space-x-2">
                    <component :is="theme.icon" class="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <span class="font-medium text-gray-700 dark:text-gray-200">{{ theme.label }}</span>
                  </div>
                  
                  <div v-if="themeStore.theme === theme.value" class="absolute top-2 right-2 text-blue-500">
                    <CheckCircle2 class="w-5 h-5" />
                  </div>
                </button>
              </div>

              <div class="mt-6 p-4 bg-blue-50 dark:bg-blue-900/10 rounded-lg border border-blue-100 dark:border-blue-900/30">
                <p class="text-sm text-blue-800 dark:text-blue-200 flex items-start space-x-2">
                  <Info class="w-5 h-5 flex-shrink-0" />
                  <span>El tema "Sistema" se adaptará automáticamente a la configuración de tu dispositivo.</span>
                </p>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '../stores/theme'
import { useToastStore } from '../stores/toast'
import api from '../api'
import PasswordStrengthMeter from '../components/auth/PasswordStrengthMeter.vue'
import { 
  Settings as SettingsIcon, 
  User, 
  Lock, 
  Palette, 
  Mail, 
  AtSign, 
  Key, 
  Loader2,
  Moon,
  Sun,
  Monitor,
  CheckCircle2,
  Info
} from 'lucide-vue-next'

const authStore = useAuthStore()
const themeStore = useThemeStore()
const toastStore = useToastStore()

const currentTab = ref('profile')
const loading = ref(false)
const passwordError = ref('')

// Configuración de Tabs
const tabs = [
  { id: 'profile', label: 'Mi Perfil', icon: markRaw(User) },
  { id: 'security', label: 'Seguridad', icon: markRaw(Lock) },
  { id: 'appearance', label: 'Apariencia', icon: markRaw(Palette) },
]

// Configuración de Temas
const themes = [
  { value: 'light', label: 'Claro', icon: markRaw(Sun), previewClass: 'bg-white border border-gray-200' },
  { value: 'dark', label: 'Oscuro', icon: markRaw(Moon), previewClass: 'bg-gray-900 border border-gray-700' },
  { value: 'system', label: 'Sistema', icon: markRaw(Monitor), previewClass: 'bg-gradient-to-br from-white to-gray-900 border border-gray-300' },
]

// Formulario de Perfil
const profileForm = ref({
  name: '',
  username: '',
  email: ''
})

// Formulario de Contraseña
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Cargar datos del usuario
onMounted(() => {
  if (authStore.user) {
    profileForm.value = {
      name: authStore.user.name || '',
      username: authStore.user.username || '',
      email: authStore.user.email || ''
    }
  }
})

// Lógica de actualización de perfil
const updateProfile = async () => {
  loading.value = true
  try {
    // Asumiendo que existe este endpoint en tu backend
    const response = await api.put(`/users/${authStore.user.id}`, {
      name: profileForm.value.name,
      username: profileForm.value.username
    })
    
    // Actualizar el store con los nuevos datos
    const updatedUser = { ...authStore.user, ...response.data }
    authStore.setAuth(authStore.token, updatedUser)
    
    toastStore.showToast('Perfil actualizado con éxito', 'success')
  } catch (error) {
    console.error('Error updating profile:', error)
    toastStore.showToast('Error al actualizar el perfil', 'error')
  } finally {
    loading.value = false
  }
}

// Lógica de cambio de contraseña
const changePassword = async () => {
  passwordError.value = ''
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = 'Las contraseñas no coinciden'
    return
  }

  loading.value = true
  try {
    // Asumiendo endpoint estándar de cambio de password
    await api.post('/auth/change-password', {
      currentPassword: passwordForm.value.currentPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    toastStore.showToast('Contraseña actualizada correctamente', 'success')
    
    // Limpiar formulario
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    console.error('Error changing password:', error)
    toastStore.showToast(error.response?.data?.message || 'Error al cambiar la contraseña', 'error')
  } finally {
    loading.value = false
  }
}
</script>