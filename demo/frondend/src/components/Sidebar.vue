<template>
  <div v-if="isAuthenticated">
    <!-- Overlay para móviles cuando el sidebar está abierto -->
    <div 
      v-if="isOpen && isMobile"
      @click="closeSidebar"
      class="fixed inset-0 bg-black bg-opacity-50 z-20 transition-opacity duration-300"
    ></div>
    
    <!-- Sidebar -->
    <div 
      :class="[
        'fixed left-0 top-0 h-full bg-white dark:bg-gray-800 shadow-lg z-30 transition-all duration-300 ease-in-out',
        isOpen ? 'w-64' : 'w-16',
        isOpen ? 'translate-x-0' : (isMobile ? '-translate-x-full' : 'translate-x-0')
      ]"
    >
      <!-- Toggle Button -->
      <button
        @click="toggleSidebar"
        class="absolute -right-3 top-6 w-6 h-6 bg-blue-600 hover:bg-blue-700 text-white rounded-full flex items-center justify-center shadow-lg transition-colors duration-200 z-40"
      >
        <ChevronLeft 
          :class="[
            'w-4 h-4 transition-transform duration-300',
            isOpen ? 'rotate-0' : 'rotate-180'
          ]" 
        />
      </button>

      <!-- Logo Section -->
      <div class="flex items-center justify-center h-16 border-b border-gray-200 dark:border-gray-700 px-4">
        <div class="flex items-center space-x-2">
          <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center flex-shrink-0">
            <Building2 class="w-5 h-5 text-white" />
          </div>
          <span 
            :class="[
              'text-xl font-bold text-gray-800 dark:text-white transition-all duration-300',
              isOpen ? 'opacity-100 w-auto' : 'opacity-0 w-0 overflow-hidden'
            ]"
          >
            DeptManager
          </span>
        </div>
      </div>
      
      <!-- Navigation Menu -->
      <nav class="mt-8">
        <div class="px-4 space-y-2">
          <a
            v-for="item in menuItems.filter(i => i.roles.includes(user.role))"
            :key="item.name"
            :href="item.path === '#logout' ? null : item.path"
            :title="!isOpen ? item.name : ''"
            class="flex items-center px-4 py-3 rounded-lg transition-colors duration-200 group relative"
            :class="[
              item.path === '#logout' 
                ? 'text-red-500 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 cursor-pointer'
                : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700',
              $route.path === item.path && item.path !== '#logout'
                ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400'
                : '',
              $route.path === item.path && isOpen && item.path !== '#logout'
                ? 'border-r-2 border-blue-600'
                : '',
              $route.path === item.path && !isOpen && item.path !== '#logout'
                ? 'border-l-4 border-blue-600'
                : ''
            ]"
            @click="() => handleMenuClick(item)"
          >
            <component 
              :is="item.icon" 
              class="w-5 h-5 flex-shrink-0 transition-all duration-300" 
              :class="[
                isOpen ? 'mr-3' : 'mx-auto',
                $route.path === item.path && !isOpen ? 'transform -translate-x-1' : ''
              ]" 
            />
            
            <span 
              :class="[
                'font-medium transition-all duration-300',
                isOpen ? 'opacity-100 w-auto' : 'opacity-0 w-0 overflow-hidden'
              ]"
            >
              {{ item.name }}
            </span>
            
            <div 
              v-if="!isOpen"
              class="absolute left-full ml-2 px-2 py-1 bg-gray-900 text-white text-sm rounded opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap z-50"
            >
              {{ item.name }}
            </div>
            
            <ChevronRight 
              v-if="$route.path === item.path && isOpen && item.path !== '#logout'" 
              class="w-4 h-4 ml-auto text-blue-600 dark:text-blue-400" 
            />
          </a>
        </div>
      </nav>
      
      <!-- Stats Section -->
      <div 
        :class="[
          'absolute bottom-4 left-4 right-4 transition-all duration-300',
          isOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'
        ]"
      >
        <div class="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg p-4 text-white">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm opacity-90">Total Departments</p>
              <p class="text-2xl font-bold">{{ totalDepartments }}</p>
            </div>
            <TrendingUp class="w-8 h-8 opacity-80" />
          </div>
          <!-- Loading indicator -->
          <div v-if="isLoading" class="mt-2">
            <div class="animate-pulse flex space-x-2">
              <div class="h-2 bg-white/30 rounded w-full"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Mini stats para modo colapsado -->
      <div 
        v-if="!isOpen"
        class="absolute bottom-4 left-1/2 transform -translate-x-1/2"
      >
        <div 
          class="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center text-white font-bold text-sm cursor-pointer hover:scale-105 transition-transform"
          :title="`Total Departments: ${totalDepartments}`"
          @click="toggleSidebar"
        >
          {{ totalDepartments }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  Building2, 
  Home, 
  Users, 
  FolderOpen, 
  Settings, 
  ChevronRight, 
  ChevronLeft, 
  TrendingUp,
  LogOut 
} from 'lucide-vue-next'
import api from '../api'

const router = useRouter()
const route = useRoute()

// Estado reactivo para autenticación
const authState = ref({
  token: localStorage.getItem('authToken'),
  user: JSON.parse(localStorage.getItem('user') || '{}')
})

// Computed para autenticación y usuario
const isAuthenticated = computed(() => !!authState.value.token)
const user = computed(() => authState.value.user)

// Función para actualizar el estado de autenticación
const updateAuthState = () => {
  authState.value = {
    token: localStorage.getItem('authToken'),
    user: JSON.parse(localStorage.getItem('user') || '{}')
  }
}

// Items del menú
const menuItems = [
  { name: 'Dashboard', path: '/homeDepartaments', icon: Home, roles: ['ADMIN_GLOBAL'] },
  { name: 'Departments', path: '/departments', icon: Building2, roles: ['ADMIN_GLOBAL'] },
  { name: 'Projects', path: '/projects', icon: FolderOpen, roles: ['ADMIN_GLOBAL'] },
  { name: 'Team', path: '/team', icon: Users, roles: ['ADMIN_GLOBAL'] },
  { name: 'Settings', path: '/settings', icon: Settings, roles: ['ADMIN_GLOBAL'] },

  { name: 'Dashboard', path: '/departmentHome', icon: Home, roles: ['ADMIN_DEPT'] },
  { name: 'Projects', path: '/projectsDept', icon: FolderOpen, roles: ['ADMIN_DEPT'] },
  { name: 'Stats', path: '/deptStats', icon: Users, roles: ['ADMIN_DEPT'] },
  { name: 'Settings', path: '/settings', icon: Settings, roles: ['ADMIN_DEPT'] },

  { name: 'Dashboard', path: '/dashTask', icon: FolderOpen, roles: ['ADMIN_COLLAB'] },
  { name: 'Tasks', path: '/tasks', icon: Users, roles: ['ADMIN_COLLAB'] },
  { name: 'Settings', path: '/settings', icon: Settings, roles: ['ADMIN_COLLAB'] },

  { name: 'Dashboard', path: '/homeTask', icon: FolderOpen, roles: ['COLLAB'] },
  { name: 'Tasks', path: '/tasksDash', icon: Users, roles: ['COLLAB'] },
  { name: 'Settings', path: '/settings', icon: Settings, roles: ['COLLAB'] },
  
  // Item de logout
  { name: 'Cerrar sesión', path: '#logout', icon: LogOut, roles: ['ADMIN_GLOBAL', 'ADMIN_DEPT', 'ADMIN_COLLAB', 'COLLAB'] }
]

// Función para cerrar sesión
const handleLogout = () => {
  // Eliminar datos de autenticación
  localStorage.removeItem('authToken')
  localStorage.removeItem('user')
  
  // Actualizar estado reactivo
  updateAuthState()
  
  // Redirigir a login
  router.push('/')
  
  // Cerrar sidebar si está en móvil
  if (isMobile.value) {
    closeSidebar()
  }
}

// Función para manejar clicks en el menú
const handleMenuClick = (item) => {
  // Si es el ítem de logout
  if (item.path === '#logout') {
    handleLogout()
    return
  }
  
  // En móvil, cerrar el sidebar después de navegar
  if (isMobile.value) {
    setTimeout(() => {
      closeSidebar()
    }, 150)
  }
}

// Estados del sidebar
const totalDepartments = ref(0)
const isLoading = ref(true)
const isOpen = ref(true)
const isMobile = ref(false)

// Emits para comunicar cambios al componente padre
const emit = defineEmits(['sidebar-toggle', 'sidebar-state-change'])

// Función para detectar si es móvil
const checkMobile = () => {
  const wasMobile = isMobile.value
  isMobile.value = window.innerWidth < 768
  
  if (isMobile.value && !wasMobile) {
    // Cambió a móvil, cerrar sidebar
    isOpen.value = false
  } else if (!isMobile.value && wasMobile) {
    // Cambió a desktop, abrir sidebar
    isOpen.value = true
  }
}

// Función para toggle del sidebar
const toggleSidebar = () => {
  isOpen.value = !isOpen.value
  emit('sidebar-toggle', isOpen.value)
  emit('sidebar-state-change', { isOpen: isOpen.value, isMobile: isMobile.value })
}

// Función para cerrar sidebar (usado en overlay móvil)
const closeSidebar = () => {
  if (isMobile.value) {
    isOpen.value = false
    emit('sidebar-toggle', isOpen.value)
    emit('sidebar-state-change', { isOpen: isOpen.value, isMobile: isMobile.value })
  }
}

// Función para manejar el resize de la ventana
const handleResize = () => {
  checkMobile()
}

// Función para manejar la tecla Escape
const handleKeydown = (event) => {
  if (event.key === 'Escape' && isMobile.value && isOpen.value) {
    closeSidebar()
  }
}

// Función para obtener stats de departamentos
const fetchDepartmentStats = async () => {
  try {
    isLoading.value = true
    const response = await api.get('/departments/stats')
    totalDepartments.value = response.data.totalDepartments || 0
  } catch (error) {
    console.error('Error fetching department stats:', error)
    try {
      const departmentsResponse = await api.get('/departments')
      totalDepartments.value = departmentsResponse.data.length || 0
    } catch (fallbackError) {
      console.error('Fallback also failed:', fallbackError)
      totalDepartments.value = 0
    }
  } finally {
    isLoading.value = false
  }
}

// Watch para emitir cambios de estado
watch([isOpen, isMobile], ([newIsOpen, newIsMobile]) => {
  emit('sidebar-state-change', { isOpen: newIsOpen, isMobile: newIsMobile })
}, { immediate: true })

// Exponer funciones para uso externo
defineExpose({
  toggleSidebar,
  closeSidebar,
  isOpen,
  isMobile
})

// Lifecycle hooks
onMounted(() => {
  fetchDepartmentStats()
  checkMobile()
  window.addEventListener('resize', handleResize)
  document.addEventListener('keydown', handleKeydown)
  
  // Emitir estado inicial
  emit('sidebar-state-change', { isOpen: isOpen.value, isMobile: isMobile.value })
  
  // Escuchar cambios en el localStorage desde otras pestañas
  window.addEventListener('storage', updateAuthState)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('storage', updateAuthState)
})
</script>

<style scoped>
/* Asegurar que las transiciones funcionen correctamente */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}

/* Mejorar la apariencia del tooltip */
.group:hover .absolute {
  transform: translateX(0);
}

/* Efecto hover para el botón de estadísticas mini */
.hover\:scale-105:hover {
  transform: scale(1.05);
}

/* Estilo para el ítem de logout */
a[href="#logout"]:hover {
  background-color: #fee2e2;
}
.dark a[href="#logout"]:hover {
  background-color: #7f1d1d;
}
</style>