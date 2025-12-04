<template>
  <header 
    class="bg-white dark:bg-zinc-900 shadow-sm border-b border-gray-100 dark:border-zinc-800 transition-colors duration-300"
  >
    <div class="flex items-center justify-between px-6 py-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800 dark:text-white tracking-tight">
          {{ pageTitle }}
        </h1>
        <p class="text-sm font-medium text-orange-500 mt-1">
          {{ pageDescription }}
        </p>
      </div>
      
      <div class="flex items-center space-x-4">
        <button
          @click="toggleTheme"
          class="p-2.5 rounded-xl bg-gray-50 dark:bg-zinc-800 text-gray-600 dark:text-gray-300 hover:bg-orange-50 dark:hover:bg-zinc-700 hover:text-orange-500 dark:hover:text-orange-400 transition-all duration-200 border border-transparent hover:border-orange-200 dark:hover:border-zinc-600"
          title="Cambiar tema"
        >
          <Sun v-if="isDark" class="w-5 h-5" />
          <Moon v-else class="w-5 h-5" />
        </button>
        
        <button 
          class="relative p-2.5 rounded-xl bg-gray-50 dark:bg-zinc-800 text-gray-600 dark:text-gray-300 hover:bg-orange-50 dark:hover:bg-zinc-700 hover:text-orange-500 dark:hover:text-orange-400 transition-all duration-200 border border-transparent hover:border-orange-200 dark:hover:border-zinc-600"
        >
          <Bell class="w-5 h-5" />
          <span class="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-zinc-800"></span>
        </button>

        <div class="h-8 w-px bg-gray-200 dark:bg-zinc-700 mx-2"></div>

        <div class="flex items-center space-x-3 pl-2">
          <div 
            class="w-9 h-9 rounded-xl bg-gradient-to-br from-orange-400 to-orange-600 flex items-center justify-center text-white font-bold shadow-lg shadow-orange-500/20"
          >
            {{ userInitial }}
          </div>
          
          <div class="hidden md:block text-left">
            <p class="text-sm font-bold text-gray-700 dark:text-gray-200 leading-none">
              {{ userName }}
            </p>
            <p class="text-xs text-gray-400 dark:text-gray-500 mt-1 font-medium">
              {{ userRole }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Sun, Moon, Bell } from 'lucide-vue-next'
import { useThemeStore } from '../stores/theme'
import { useAuthStore } from '../stores/auth' // <--- Importamos el store de Auth

const themeStore = useThemeStore()
const authStore = useAuthStore() // <--- Inicializamos el store
const route = useRoute()

// Lógica del Tema
const isDark = computed(() => themeStore.isDark)
const toggleTheme = () => themeStore.toggleTheme()

// Lógica de Usuario Dinámico
const userName = computed(() => authStore.user?.name || 'Usuario')
const userInitial = computed(() => (authStore.user?.name?.charAt(0) || 'U').toUpperCase())

// Formatear el rol para que se vea bonito (opcional, igual que en sidebar)
const userRole = computed(() => {
  const role = authStore.user?.role || ''
  const roleMap = {
    'ADMIN_GLOBAL': 'Global Admin',
    'ADMIN_DEPT': 'Manager',
    'ADMIN_COLLAB': 'Líder',
    'COLLAB': 'Colaborador'
  }
  return roleMap[role] || 'Miembro'
})

// Títulos y descripciones de página
const pageTitle = computed(() => {
  const titles = {
    '/': 'Dashboard',
    '/homeDepartaments': 'Panel Global',
    '/departments': 'Gestión de Departamentos',
    '/projects': 'Proyectos Activos',
    '/team': 'Equipo de Trabajo',
    '/settings': 'Configuración'
  }
  return titles[route.path] || 'Panel de Control'
})

const pageDescription = computed(() => {
  const descriptions = {
    '/': 'Resumen general de actividad',
    '/homeDepartaments': 'Vista general de toda la organización',
    '/departments': 'Administra las áreas de tu empresa',
    '/projects': 'Seguimiento y control de iniciativas',
    '/team': 'Gestión de talento humano',
    '/settings': 'Preferencias de la aplicación'
  }
  return descriptions[route.path] || 'Bienvenido de nuevo'
})
</script>