<template>
  <div class="w-full min-h-screen animate-fade-in-up">
    <div class="max-w-none space-y-6">

      <div class="relative overflow-hidden bg-gradient-to-br from-violet-700 via-purple-600 to-fuchsia-600 rounded-2xl p-6 sm:p-8 text-white shadow-xl shadow-purple-500/20">
        <div class="absolute top-0 right-0 -mt-8 -mr-8 w-40 h-40 bg-fuchsia-400/20 rounded-full blur-3xl"></div>
        <div class="absolute bottom-0 left-0 -mb-8 -ml-8 w-32 h-32 bg-violet-900/40 rounded-full blur-2xl"></div>
        
        <div class="relative flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 z-10">
          <div class="flex items-center space-x-4">
            <div class="hidden sm:flex w-16 h-16 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl items-center justify-center shadow-inner">
              <Briefcase class="w-8 h-8 text-purple-50" />
            </div>
            
            <div class="min-w-0">
              <h1 class="text-3xl sm:text-4xl font-extrabold tracking-tight truncate">
                {{ projectName || 'Cargando Proyecto...' }}
              </h1>
              <p class="text-purple-100 text-sm sm:text-base font-medium flex items-center gap-2 mt-1">
                <span :class="['w-2 h-2 rounded-full animate-pulse', projectName ? 'bg-fuchsia-400' : 'bg-gray-400']"></span>
                Project Manager Dashboard
              </p>
            </div>
          </div>

          <button 
            @click="router.push('/tasks')"
            class="hidden md:flex items-center px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/30 rounded-xl transition-all duration-200 backdrop-blur-sm text-sm font-semibold group"
          >
            <ListTodo class="w-4 h-4 mr-2 group-hover:scale-110 transition-transform" />
            Gestionar Tareas
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 lg:gap-6">
        
        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Total Horas</p>
              <h3 class="text-2xl font-bold text-gray-800 dark:text-white mt-1">{{ totalHours }}h</h3>
            </div>
            <div class="p-2 bg-violet-100 dark:bg-violet-500/10 rounded-lg text-violet-600 dark:text-violet-400">
              <Clock class="w-5 h-5" />
            </div>
          </div>
          <div class="mt-4 flex items-center text-xs">
            <span class="text-gray-400">Estimadas vs Reales</span>
          </div>
          <div class="absolute -bottom-4 -right-4 w-16 h-16 bg-violet-500/10 rounded-full blur-xl group-hover:bg-violet-500/20 transition-colors"></div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Progreso</p>
              <h3 class="text-2xl font-bold text-gray-800 dark:text-white mt-1">{{ Math.round(progressPercentage) }}%</h3>
            </div>
            <div class="p-2 bg-fuchsia-100 dark:bg-fuchsia-500/10 rounded-lg text-fuchsia-600 dark:text-fuchsia-400">
              <TrendingUp class="w-5 h-5" />
            </div>
          </div>
          <div class="w-full bg-gray-100 dark:bg-zinc-700 rounded-full h-1.5 mt-4">
            <div 
              class="bg-gradient-to-r from-violet-500 to-fuchsia-500 h-1.5 rounded-full transition-all duration-1000" 
              :style="{ width: `${progressPercentage}%` }"
            ></div>
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Pendientes</p>
              <h3 class="text-2xl font-bold text-gray-800 dark:text-white mt-1">{{ pendingTasks }}</h3>
            </div>
            <div class="p-2 bg-amber-100 dark:bg-amber-500/10 rounded-lg text-amber-600 dark:text-amber-400">
              <AlertCircle class="w-5 h-5" />
            </div>
          </div>
          <div class="mt-4 flex items-center text-xs text-amber-600 dark:text-amber-500 font-medium">
             Requieren atención
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Completadas</p>
              <h3 class="text-2xl font-bold text-gray-800 dark:text-white mt-1">{{ completedTasks }}</h3>
            </div>
            <div class="p-2 bg-emerald-100 dark:bg-emerald-500/10 rounded-lg text-emerald-600 dark:text-emerald-400">
              <CheckCircle class="w-5 h-5" />
            </div>
          </div>
          <div class="mt-4 flex items-center text-xs text-gray-400">
             Total: {{ tasks.length }}
          </div>
        </div>

      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-xl font-bold text-gray-800 dark:text-white flex items-center gap-2">
              <BarChartIcon class="w-5 h-5 text-violet-500" />
              Estadísticas de Tareas
            </h3>
            <span class="text-xs font-medium px-2 py-1 rounded-md bg-violet-50 dark:bg-violet-900/20 text-violet-600 dark:text-violet-300">
              Tiempo Real
            </span>
          </div>
          
          <div class="h-80 w-full relative">
            <canvas ref="hoursChart"></canvas>
            <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
                <Loader2 class="w-8 h-8 text-violet-500 animate-spin" />
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6 flex flex-col h-full">
          <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <Zap class="w-5 h-5 text-fuchsia-500" />
            Recientes
          </h3>
          
          <div class="overflow-y-auto max-h-[350px] custom-scrollbar space-y-3 pr-2">
            <div 
              v-for="task in recentTasks" 
              :key="task.id" 
              class="group p-3 rounded-xl border border-gray-100 dark:border-zinc-700/50 hover:border-violet-300 dark:hover:border-violet-700 bg-gray-50 dark:bg-zinc-800/30 transition-all duration-200"
            >
              <div class="flex justify-between items-start mb-1">
                <h4 class="text-sm font-semibold text-gray-800 dark:text-gray-200 line-clamp-1 group-hover:text-violet-500 transition-colors">
                  {{ task.name }} </h4>
                <span :class="['text-[10px] px-1.5 py-0.5 rounded font-bold uppercase', getPriorityClass(task.priority)]">
                  {{ task.priority }}
                </span>
              </div>
              
              <div class="flex items-center justify-between mt-2">
                <span :class="['text-xs px-2 py-0.5 rounded-full font-medium', getStatusClass(task.status)]">
                  {{ formatStatus(task.status) }}
                </span>
                <div class="flex items-center text-xs text-gray-400">
                   <Clock class="w-3 h-3 mr-1" />
                   {{ formatDate(task.endDate) }} </div>
              </div>
            </div>

            <div v-if="recentTasks.length === 0 && !loading" class="text-center py-8 text-gray-500">
               No hay tareas recientes.
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Briefcase, Clock, TrendingUp, AlertCircle, 
  CheckCircle, BarChart as BarChartIcon, Zap, 
  ListTodo, Loader2 
} from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import api from '../../api'
import { useToastStore } from '../../stores/toast'

const router = useRouter()
const toastStore = useToastStore()

// State
const loading = ref(true)
const tasks = ref([])
const projectName = ref('')
const projectId = ref(null)
const hoursChart = ref(null)
let chartInstance = null

// --- Data Fetching CORREGIDO ---
const fetchDashboardData = async () => {
  try {
    loading.value = true
    const user = JSON.parse(localStorage.getItem('user')) || {}
    const email = user.email || user.username; // Obtener email o username

    if (!email) {
        console.warn("No user email found");
        return;
    }

    // Llamada al endpoint CORRECTO que ya existe en ProjectController
    // Este endpoint devuelve: { projectId, projectName, tasks: [] }
    const response = await api.get(`/projects/assing-project/${email}`);
    
    const data = response.data;
    
    // Asignar datos
    projectId.value = data.projectId;
    projectName.value = data.projectName;
    tasks.value = data.tasks || []; // Las tareas vienen incluidas en la respuesta

    await nextTick()
    renderChart()

  } catch (error) {
    console.error('Error loading dashboard:', error)
    // Manejo silencioso de errores 404 si no tiene proyecto
    if (error.response?.status === 404) {
        projectName.value = 'Sin Proyecto Asignado';
        tasks.value = [];
    } else {
        toastStore.showToast('Error al cargar datos del proyecto', 'error')
    }
  } finally {
    loading.value = false
  }
}

// --- Computed Stats ---
const totalHours = computed(() => {
  return tasks.value.reduce((acc, task) => acc + (task.estimatedHours || 0), 0)
})

const completedTasks = computed(() => {
  return tasks.value.filter(t => t.status === 'COMPLETED' || t.status === 'DONE').length
})

const pendingTasks = computed(() => {
  return tasks.value.filter(t => t.status !== 'COMPLETED' && t.status !== 'DONE').length
})

const progressPercentage = computed(() => {
  if (tasks.value.length === 0) return 0
  return (completedTasks.value / tasks.value.length) * 100
})

const recentTasks = computed(() => {
  // Ordenar por ID descendente (más recientes primero)
  return [...tasks.value]
    .sort((a, b) => b.id - a.id)
    .slice(0, 5)
})

// --- Chart ---
const renderChart = () => {
  if (chartInstance) chartInstance.destroy()
  const ctx = hoursChart.value?.getContext('2d')
  if (!ctx) return

  const isDark = document.documentElement.classList.contains('dark')
  
  const statusCounts = {
    'Pendiente': tasks.value.filter(t => t.status === 'PENDING').length,
    'En Progreso': tasks.value.filter(t => t.status === 'IN_PROGRESS').length,
    'Revisión': tasks.value.filter(t => t.status === 'IN_REVIEW').length,
    'Completado': completedTasks.value
  }

  chartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: Object.keys(statusCounts),
      datasets: [{
        label: 'Tareas por Estado',
        data: Object.values(statusCounts),
        backgroundColor: [
          'rgba(167, 139, 250, 0.8)', // Violet
          'rgba(232, 121, 249, 0.8)', // Fuchsia
          'rgba(251, 191, 36, 0.8)',  // Amber
          'rgba(52, 211, 153, 0.8)'   // Emerald
        ],
        borderRadius: 6,
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: {
            color: isDark ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)'
          },
          ticks: { color: isDark ? '#d4d4d8' : '#52525b' }
        },
        x: {
          grid: { display: false },
          ticks: { color: isDark ? '#d4d4d8' : '#52525b' }
        }
      }
    }
  })
}

// --- Helpers ---
const formatStatus = (status) => {
  if (!status) return '';
  // Traducción simple
  const map = {
      'PENDING': 'Pendiente',
      'IN_PROGRESS': 'En Progreso',
      'IN_REVIEW': 'En Revisión',
      'COMPLETED': 'Completada',
      'CANCELLED': 'Cancelada'
  };
  return map[status] || status.replace('_', ' ');
}

const getStatusClass = (status) => {
  const map = {
    'PENDING': 'bg-gray-100 text-gray-600 dark:bg-zinc-800 dark:text-gray-400',
    'IN_PROGRESS': 'bg-fuchsia-100 text-fuchsia-700 dark:bg-fuchsia-500/20 dark:text-fuchsia-300',
    'IN_REVIEW': 'bg-violet-100 text-violet-700 dark:bg-violet-500/20 dark:text-violet-300',
    'COMPLETED': 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-300',
    'DONE': 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-300'
  }
  return map[status] || 'bg-gray-100'
}

const getPriorityClass = (priority) => {
  const map = {
    'LOW': 'text-gray-500 bg-gray-100 dark:bg-zinc-800',
    'MEDIUM': 'text-blue-500 bg-blue-50 dark:bg-blue-900/20',
    'HIGH': 'text-orange-500 bg-orange-50 dark:bg-orange-900/20',
    'URGENT': 'text-red-500 bg-red-50 dark:bg-red-900/20'
  }
  return map[priority] || 'text-gray-500'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('es-ES', { month: 'short', day: 'numeric' })
}

onMounted(() => {
  fetchDashboardData()
  new MutationObserver(renderChart).observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
})
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background-color: #e9d5ff; border-radius: 20px; }
.dark .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #581c87; }
</style>