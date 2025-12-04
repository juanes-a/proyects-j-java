<template>
  <div v-if="isAuthenticated">
    <div 
      v-if="isOpen && isMobile"
      @click="closeSidebar"
      class="fixed inset-0 bg-black/60 backdrop-blur-sm z-20 transition-opacity duration-300"
    ></div>
    
    <div 
      :class="[
        'fixed left-0 top-0 h-full shadow-2xl z-30 transition-all duration-300 ease-in-out border-r',
        'bg-white border-gray-100', // Modo Claro
        'dark:bg-zinc-900 dark:border-zinc-800', // Modo Oscuro (Más negro/elegante)
        isOpen ? 'w-64' : 'w-20',
        isOpen ? 'translate-x-0' : (isMobile ? '-translate-x-full' : 'translate-x-0')
      ]"
    >
      <button
        @click="toggleSidebar"
        class="absolute -right-3 top-8 w-7 h-7 bg-orange-500 hover:bg-orange-600 text-white rounded-full flex items-center justify-center shadow-[0_0_15px_rgba(249,115,22,0.5)] transition-all duration-200 z-40 hover:scale-110"
      >
        <ChevronLeft 
          :class="[
            'w-4 h-4 transition-transform duration-300',
            isOpen ? 'rotate-0' : 'rotate-180'
          ]" 
        />
      </button>

      <div class="pt-6 pb-4 px-4 overflow-hidden">
        <div class="flex items-center" :class="{ 'justify-center': !isOpen }">
          <div 
            class="relative flex-shrink-0 w-10 h-10 rounded-xl bg-gradient-to-br from-orange-400 to-orange-600 flex items-center justify-center text-white font-bold shadow-lg shadow-orange-500/20"
          >
            {{ authStore.user?.name?.charAt(0) || 'U' }}
            <div class="absolute -bottom-1 -right-1 w-3 h-3 bg-green-500 border-2 border-white dark:border-zinc-900 rounded-full"></div>
          </div>
          
          <div v-if="isOpen" class="ml-3 transition-opacity duration-200 fade-in">
            <p class="text-sm font-bold text-gray-800 dark:text-gray-100 truncate w-40">
              {{ authStore.user?.name || 'Usuario' }}
            </p>
            <p class="text-xs font-medium text-orange-500 tracking-wide uppercase">
              {{ userRoleLabel }}
            </p>
          </div>
        </div>
      </div>

      <div class="mx-4 my-2 border-t border-gray-100 dark:border-zinc-800"></div>
      
      <nav class="mt-4 px-3 space-y-1 h-[calc(100vh-220px)] overflow-y-auto custom-scrollbar">
        <a
          v-for="item in menuItems.filter(i => i.roles.includes(user.role))"
          :key="item.name"
          :href="item.path === '#logout' ? null : item.path"
          class="flex items-center py-3 px-3 rounded-xl transition-all duration-200 group relative mb-1"
          :class="[
            /* Estilos base */
            'font-medium',
            
            /* Estilos Logout (Rojo) */
            item.path === '#logout' 
              ? 'text-gray-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 cursor-pointer mt-8'
              
            /* Estilos Ítem Activo (Naranja) */
            : $route.path === item.path
              ? 'bg-orange-50 dark:bg-orange-500/10 text-orange-600 dark:text-orange-400 shadow-sm'
              
            /* Estilos Ítem Inactivo */
              : 'text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-zinc-800 hover:text-gray-900 dark:hover:text-gray-200'
          ]"
          @click="() => handleMenuClick(item)"
        >
          <div 
            v-if="$route.path === item.path && item.path !== '#logout'"
            class="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-8 bg-orange-500 rounded-r-full"
          ></div>

          <component 
            :is="item.icon" 
            class="w-5 h-5 flex-shrink-0 transition-colors duration-200"
            :class="[
              !isOpen && 'mx-auto',
              $route.path === item.path && item.path !== '#logout' ? 'text-orange-600 dark:text-orange-400' : ''
            ]" 
          />
          
          <span 
            v-show="isOpen"
            class="ml-3 truncate transition-all duration-300"
          >
            {{ item.name }}
          </span>
          
          <div 
            v-if="!isOpen"
            class="absolute left-14 px-3 py-1.5 bg-zinc-800 text-white text-xs font-medium rounded-md shadow-xl opacity-0 group-hover:opacity-100 transition-all duration-200 pointer-events-none whitespace-nowrap z-50 translate-x-2 group-hover:translate-x-0"
          >
            {{ item.name }}
            <div class="absolute top-1/2 -left-1 -mt-1 w-2 h-2 bg-zinc-800 transform rotate-45"></div>
          </div>
        </a>
      </nav>
      
      <div class="absolute bottom-0 w-full p-4 bg-white dark:bg-zinc-900 border-t border-gray-100 dark:border-zinc-800">
        
        <div 
          v-if="isOpen"
          class="relative overflow-hidden rounded-xl bg-gradient-to-r from-orange-500 to-red-600 p-4 text-white shadow-lg shadow-orange-500/20 group cursor-pointer transition-transform hover:-translate-y-1"
        >
          <div class="absolute -right-4 -top-4 w-16 h-16 bg-white/10 rounded-full blur-xl group-hover:bg-white/20 transition-colors"></div>
          
          <div class="flex items-center justify-between relative z-10">
            <div>
              <p class="text-xs font-medium text-orange-100 uppercase tracking-wider">Departamentos</p>
              <div class="flex items-baseline mt-1">
                <span class="text-2xl font-bold">{{ totalDepartments }}</span>
                <span class="ml-1 text-xs text-orange-100">Activos</span>
              </div>
            </div>
            <div class="p-2 bg-white/20 rounded-lg backdrop-blur-sm">
              <TrendingUp class="w-5 h-5 text-white" />
            </div>
          </div>

          <div v-if="isLoading" class="mt-3">
            <div class="animate-pulse h-1 bg-white/30 rounded w-full"></div>
          </div>
        </div>

        <div 
          v-else
          class="flex justify-center"
        >
          <div 
            class="w-10 h-10 bg-gradient-to-br from-zinc-800 to-black border border-zinc-700 rounded-xl flex items-center justify-center text-orange-500 font-bold text-sm cursor-pointer hover:border-orange-500 transition-colors shadow-lg"
            :title="`Total Departments: ${totalDepartments}`"
            @click="toggleSidebar"
          >
            {{ totalDepartments }}
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
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
const authStore = useAuthStore()

const userRoleLabel = computed(() => {
  const role = authStore.user?.role || ''
  const roleMap = {
    'ADMIN_GLOBAL': 'Global Admin',
    'ADMIN_DEPT': 'Dept Manager',
    'ADMIN_COLLAB': 'Project Manager',
    'COLLAB': 'Collaborator'
  }
  return roleMap[role] || role || 'User'
})

// Estado reactivo para autenticación
const authState = ref({
  token: localStorage.getItem('authToken'),
  user: JSON.parse(localStorage.getItem('user') || '{}')
})

const isAuthenticated = computed(() => !!authState.value.token)
const user = computed(() => authState.value.user)

const updateAuthState = () => {
  authState.value = {
    token: localStorage.getItem('authToken'),
    user: JSON.parse(localStorage.getItem('user') || '{}')
  }
}

// Items del menú (Sin cambios en lógica, solo visual en template)
const menuItems = [
  { name: 'Dashboard', path: '/homeDepartaments', icon: Home, roles: ['ADMIN_GLOBAL'] },
  { name: 'Departments', path: '/departments', icon: Building2, roles: ['ADMIN_GLOBAL'] },
  { name: 'Projects', path: '/projects', icon: FolderOpen, roles: ['ADMIN_GLOBAL'] },
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
  
  { name: 'Cerrar sesión', path: '#logout', icon: LogOut, roles: ['ADMIN_GLOBAL', 'ADMIN_DEPT', 'ADMIN_COLLAB', 'COLLAB'] }
]

const handleLogout = () => {
  localStorage.removeItem('authToken')
  localStorage.removeItem('user')
  updateAuthState()
  router.push('/')
  if (isMobile.value) closeSidebar()
}

const handleMenuClick = (item) => {
  if (item.path === '#logout') {
    handleLogout()
    return
  }
  if (isMobile.value) {
    setTimeout(() => { closeSidebar() }, 150)
  }
}

const totalDepartments = ref(0)
const isLoading = ref(true)
const isOpen = ref(true)
const isMobile = ref(false)
const emit = defineEmits(['sidebar-toggle', 'sidebar-state-change'])

const checkMobile = () => {
  const wasMobile = isMobile.value
  isMobile.value = window.innerWidth < 768
  if (isMobile.value && !wasMobile) isOpen.value = false
  else if (!isMobile.value && wasMobile) isOpen.value = true
}

const toggleSidebar = () => {
  isOpen.value = !isOpen.value
  emit('sidebar-toggle', isOpen.value)
  emit('sidebar-state-change', { isOpen: isOpen.value, isMobile: isMobile.value })
}

const closeSidebar = () => {
  if (isMobile.value) {
    isOpen.value = false
    emit('sidebar-toggle', isOpen.value)
    emit('sidebar-state-change', { isOpen: isOpen.value, isMobile: isMobile.value })
  }
}

const handleResize = () => checkMobile()
const handleKeydown = (event) => {
  if (event.key === 'Escape' && isMobile.value && isOpen.value) closeSidebar()
}

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

watch([isOpen, isMobile], ([newIsOpen, newIsMobile]) => {
  emit('sidebar-state-change', { isOpen: newIsOpen, isMobile: newIsMobile })
}, { immediate: true })

defineExpose({ toggleSidebar, closeSidebar, isOpen, isMobile })

onMounted(() => {
  fetchDepartmentStats()
  checkMobile()
  window.addEventListener('resize', handleResize)
  document.addEventListener('keydown', handleKeydown)
  window.addEventListener('storage', updateAuthState)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('storage', updateAuthState)
})
</script>

<style scoped>
/* Personalización de la barra de desplazamiento */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #e5e7eb;
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #3f3f46;
}

/* Animación simple para fade */
.fade-in {
  animation: fadeIn 0.3s ease-in-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateX(-5px); }
  to { opacity: 1; transform: translateX(0); }
}
</style>