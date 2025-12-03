<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 font-sans transition-colors duration-300">
    
    <header class="bg-gradient-to-r from-indigo-600 to-blue-500 pb-24 pt-10 px-4 shadow-xl">
      <div class="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
        <div class="text-white text-center md:text-left">
          <h1 class="text-3xl md:text-4xl font-extrabold tracking-tight">
            Hola, {{ username }} 👋
          </h1>
          <p class="text-blue-100 mt-2 text-lg font-medium opacity-90">
            Aquí tienes el resumen de tu productividad hoy.
          </p>
        </div>
        
        <div class="flex flex-col items-center md:items-end gap-3">
          <div class="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-4 shadow-lg">
            <p class="text-xs text-blue-100 uppercase tracking-wider font-bold">Fecha Actual</p>
            <p class="text-xl font-bold text-white">{{ currentDate }}</p>
          </div>
          <button 
            @click="fetchAssignedTasks" 
            class="text-sm bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-full transition-all flex items-center gap-2 backdrop-blur-sm cursor-pointer"
            :disabled="loading"
          >
            <span :class="{'animate-spin': loading}">↻</span> Actualizar datos
          </button>
        </div>
      </div>
    </header>

    <main class="max-w-7xl mx-auto px-4 -mt-16 pb-12">
      
      <div v-if="loading && !hasData" class="flex justify-center py-20 bg-white dark:bg-gray-800 rounded-3xl shadow-sm">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>

      <div v-else-if="error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded-r-lg mb-8 shadow-sm">
        <div class="flex">
          <div class="flex-shrink-0">⚠️</div>
          <div class="ml-3">
            <p class="text-sm text-red-700 font-medium">No se pudieron cargar los datos.</p>
            <p class="text-xs text-red-500 mt-1">{{ error }}</p>
            <button @click="fetchAssignedTasks" class="text-xs underline text-red-700 mt-2">Intentar de nuevo</button>
          </div>
        </div>
      </div>

      <div v-else>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 hover:-translate-y-1 transition-transform duration-300 relative overflow-hidden group">
            <div class="absolute right-0 top-0 w-24 h-24 bg-blue-500/10 rounded-bl-full -mr-4 -mt-4 transition-all group-hover:bg-blue-500/20"></div>
            <div class="relative z-10">
              <div class="flex items-center justify-between mb-4">
                <div class="p-3 bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded-xl">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path></svg>
                </div>
                <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Total</span>
              </div>
              <p class="text-3xl font-extrabold text-gray-800 dark:text-white">{{ stats.totalTasks }}</p>
            </div>
          </div>

          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 hover:-translate-y-1 transition-transform duration-300 relative overflow-hidden group">
            <div class="absolute right-0 top-0 w-24 h-24 bg-emerald-500/10 rounded-bl-full -mr-4 -mt-4 transition-all group-hover:bg-emerald-500/20"></div>
            <div class="relative z-10">
              <div class="flex items-center justify-between mb-4">
                <div class="p-3 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400 rounded-xl">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
                <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Completadas</span>
              </div>
              <p class="text-3xl font-extrabold text-gray-800 dark:text-white">{{ stats.completedTasks }}</p>
              <div class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-1.5 mt-3">
                <div class="bg-emerald-500 h-1.5 rounded-full transition-all duration-1000" :style="`width: ${getPercentage(stats.completedTasks)}%`"></div>
              </div>
            </div>
          </div>

          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 hover:-translate-y-1 transition-transform duration-300 relative overflow-hidden group">
            <div class="absolute right-0 top-0 w-24 h-24 bg-amber-500/10 rounded-bl-full -mr-4 -mt-4 transition-all group-hover:bg-amber-500/20"></div>
            <div class="relative z-10">
              <div class="flex items-center justify-between mb-4">
                <div class="p-3 bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400 rounded-xl">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
                <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Pendientes</span>
              </div>
              <p class="text-3xl font-extrabold text-gray-800 dark:text-white">{{ stats.pendingTasks }}</p>
            </div>
          </div>

          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 hover:-translate-y-1 transition-transform duration-300 relative overflow-hidden group">
            <div class="absolute right-0 top-0 w-24 h-24 bg-red-500/10 rounded-bl-full -mr-4 -mt-4 transition-all group-hover:bg-red-500/20"></div>
            <div class="relative z-10">
              <div class="flex items-center justify-between mb-4">
                <div class="p-3 bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-xl">
                  <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
                </div>
                <span class="text-xs font-bold text-gray-400 uppercase tracking-wider">Vencidas</span>
              </div>
              <p class="text-3xl font-extrabold text-gray-800 dark:text-white">{{ stats.overdueTasks }}</p>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-8">
          
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6 lg:col-span-2">
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-lg font-bold text-gray-800 dark:text-white flex items-center gap-2">
                <span class="w-1.5 h-6 bg-blue-500 rounded-full"></span>
                Distribución de Tareas
              </h3>
              <div class="flex bg-gray-100 dark:bg-gray-700 rounded-lg p-1">
                <button 
                  @click="changeChartType('doughnut')"
                  :class="['px-3 py-1 text-xs font-medium rounded-md transition-all', chartType === 'doughnut' ? 'bg-white dark:bg-gray-600 text-blue-600 dark:text-blue-400 shadow-sm' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700']"
                >
                  Circular
                </button>
                <button 
                  @click="changeChartType('bar')"
                  :class="['px-3 py-1 text-xs font-medium rounded-md transition-all', chartType === 'bar' ? 'bg-white dark:bg-gray-600 text-blue-600 dark:text-blue-400 shadow-sm' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700']"
                >
                  Barras
                </button>
              </div>
            </div>
            
            <div class="h-80 w-full relative flex items-center justify-center">
               <canvas ref="chartCanvas" v-show="hasData"></canvas>
               
               <div v-if="!hasData" class="text-center text-gray-400 flex flex-col items-center">
                  <svg class="w-12 h-12 mb-2 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path></svg>
                  <p>No hay datos suficientes para graficar</p>
               </div>
            </div>
          </div>
          
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-lg p-6">
            <h3 class="text-lg font-bold text-gray-800 dark:text-white flex items-center gap-2 mb-6">
              <span class="w-1.5 h-6 bg-amber-500 rounded-full"></span>
              Próximos Vencimientos
            </h3>
            
            <div class="space-y-4">
              <div v-for="task in urgentTasks" :key="task.id" class="flex items-center p-3 bg-gray-50 dark:bg-gray-700/50 rounded-xl hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors border border-gray-100 dark:border-gray-700 group">
                 <div class="flex-shrink-0 mr-4">
                   <div class="w-10 h-10 rounded-full bg-blue-50 dark:bg-blue-900/30 flex items-center justify-center text-blue-500 dark:text-blue-400 font-bold text-xs">
                     {{ getDayNumber(task.endDate) }}
                   </div>
                 </div>
                 <div class="flex-1 min-w-0">
                   <p class="font-bold text-gray-800 dark:text-white truncate group-hover:text-blue-600 transition-colors">{{ task.name }}</p>
                   <p class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1">
                     <span>{{ formatDate(task.endDate) }}</span>
                     <span v-if="daysUntilDue(task.endDate) < 0" class="text-red-500 font-bold">• Vencida</span>
                     <span v-else class="text-amber-500 font-medium">• {{ daysUntilDue(task.endDate) }} días</span>
                   </p>
                 </div>
                 <div class="flex-shrink-0 ml-2">
                    <span :class="getPriorityBadgeClass(task.priority)" class="px-2 py-1 text-[10px] rounded-full font-bold uppercase tracking-wide border">
                      {{ getPriorityShort(task.priority) }}
                    </span>
                 </div>
              </div>

              <div v-if="urgentTasks.length === 0" class="text-center py-10">
                <p class="text-gray-500 dark:text-gray-400 font-medium">¡No tienes entregas próximas!</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import axios from 'axios'
import { Chart, registerables } from 'chart.js'
import { useAuthStore } from '../../stores/auth'

// Registrar componentes de Chart.js
Chart.register(...registerables)

export default {
  name: 'DashboardView',
  setup() {
    const authStore = useAuthStore()
    const username = ref('Usuario')
    const loading = ref(false)
    const error = ref(null)
    const assignedTasks = ref([])
    const urgentTasks = ref([])
    
    // Aquí es donde ocurría el error: la referencia debe coincidir con el template
    const chartCanvas = ref(null) 
    
    const chartType = ref('doughnut')
    let chartInstance = null

    const currentDate = ref(new Date().toLocaleDateString('es-ES', {
      weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    }))

    const stats = ref({
      totalTasks: 0, completedTasks: 0, pendingTasks: 0, overdueTasks: 0
    })

    const hasData = computed(() => assignedTasks.value.length > 0)

    const parseJavaDate = (dateString) => {
      if (!dateString) return null
      const isoString = dateString.replace(' ', 'T')
      return new Date(isoString)
    }

    const fetchAssignedTasks = async () => {
      loading.value = true
      error.value = null
      
      try {
        const usernameOrEmail = authStore.user?.email || localStorage.getItem('userEmail')
        if (!usernameOrEmail) throw new Error('Usuario no identificado.')

        const response = await axios.get(`/api/tasks/assigned-tasks/${usernameOrEmail}`)
        
        username.value = response.data.user || 'Usuario'
        assignedTasks.value = response.data.assignedTasks || []

        calculateStats(assignedTasks.value)
        filterUrgentTasks(assignedTasks.value)

        // Renderizar gráfico
        await nextTick()
        renderChart()

      } catch (err) {
        console.error('Error fetching tasks:', err)
        error.value = 'No se pudieron cargar las tareas. Verifica tu conexión.'
      } finally {
        loading.value = false
      }
    }

    const calculateStats = (tasks) => {
      if (!tasks) return
      stats.value.totalTasks = tasks.length
      stats.value.completedTasks = tasks.filter(t => t.status === 'COMPLETED').length
      stats.value.pendingTasks = tasks.filter(t => t.status !== 'COMPLETED' && t.status !== 'CANCELLED').length
      
      const today = new Date()
      stats.value.overdueTasks = tasks.filter(t => {
        if (!t.endDate || t.status === 'COMPLETED') return false
        return parseJavaDate(t.endDate) < today
      }).length
    }

    const filterUrgentTasks = (tasks) => {
      const today = new Date()
      today.setHours(0,0,0,0)
      const nextWeek = new Date(today)
      nextWeek.setDate(nextWeek.getDate() + 7)

      urgentTasks.value = tasks
        .filter(task => {
          if (!task.endDate || task.status === 'COMPLETED' || task.status === 'CANCELLED') return false
          const dueDate = parseJavaDate(task.endDate)
          return dueDate <= nextWeek
        })
        .sort((a, b) => parseJavaDate(a.endDate) - parseJavaDate(b.endDate))
        .slice(0, 5)
    }

    const renderChart = () => {
      // Validación extra para asegurar que el canvas existe
      if (!chartCanvas.value || !hasData.value) return

      if (chartInstance) {
        chartInstance.destroy()
      }

      const ctx = chartCanvas.value.getContext('2d')

      const completed = stats.value.completedTasks
      const inProgress = assignedTasks.value.filter(t => t.status === 'IN_PROGRESS').length
      const pending = assignedTasks.value.filter(t => t.status === 'PENDING').length
      const overdue = stats.value.overdueTasks

      const config = {
        type: chartType.value,
        data: {
          labels: ['Completadas', 'En Progreso', 'Pendientes', 'Vencidas'],
          datasets: [{
            data: [completed, inProgress, pending, overdue],
            backgroundColor: ['#10B981', '#3B82F6', '#F59E0B', '#EF4444'],
            borderWidth: 0,
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          cutout: chartType.value === 'doughnut' ? '75%' : 0,
          plugins: {
            legend: {
              display: chartType.value === 'doughnut',
              position: 'bottom',
              labels: { usePointStyle: true, padding: 20 }
            }
          },
          scales: chartType.value === 'bar' ? {
            y: { beginAtZero: true, grid: { color: '#f3f4f6' } },
            x: { grid: { display: false } }
          } : {}
        }
      }

      chartInstance = new Chart(ctx, config)
    }

    const changeChartType = (type) => {
      chartType.value = type
      renderChart()
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      return parseJavaDate(dateString).toLocaleDateString('es-ES', { month: 'short', day: 'numeric' })
    }

    const getDayNumber = (dateString) => {
      if (!dateString) return '?'
      return parseJavaDate(dateString).getDate()
    }

    const daysUntilDue = (dateString) => {
      if (!dateString) return 0
      const diff = parseJavaDate(dateString) - new Date().setHours(0,0,0,0)
      return Math.ceil(diff / (1000 * 60 * 60 * 24))
    }

    const getPercentage = (value) => {
      if (stats.value.totalTasks === 0) return 0
      return Math.round((value / stats.value.totalTasks) * 100)
    }

    const getPriorityBadgeClass = (priority) => {
      const map = {
        'HIGH': 'bg-red-50 text-red-600 border-red-200 dark:bg-red-900/30 dark:text-red-300',
        'URGENT': 'bg-red-50 text-red-600 border-red-200 dark:bg-red-900/30 dark:text-red-300',
        'MEDIUM': 'bg-amber-50 text-amber-600 border-amber-200 dark:bg-amber-900/30 dark:text-amber-300',
        'LOW': 'bg-green-50 text-green-600 border-green-200 dark:bg-green-900/30 dark:text-green-300'
      }
      return map[priority] || 'bg-gray-100 text-gray-600'
    }

    const getPriorityShort = (priority) => {
      const map = { 'HIGH': 'Alta', 'URGENT': 'Urg', 'MEDIUM': 'Med', 'LOW': 'Baja' }
      return map[priority] || priority?.substring(0,3)
    }

    onMounted(() => {
      fetchAssignedTasks()
    })

    // Limpieza al salir de la vista para evitar errores de memoria
    onUnmounted(() => {
      if (chartInstance) {
        chartInstance.destroy()
      }
    })

    return {
      username, loading, error, currentDate, stats, hasData,
      chartCanvas, chartType, urgentTasks,
      changeChartType, formatDate, getDayNumber, daysUntilDue,
      getPriorityBadgeClass, getPriorityShort, getPercentage, fetchAssignedTasks
    }
  }
}
</script>