 <template>
  <div class="min-h-screen bg-gray-50">
    <!-- Banner de bienvenida mejorado -->
    <header class="bg-gradient-to-r from-blue-600 to-indigo-800 text-white shadow-lg">
      <div class="container mx-auto px-4 py-8">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <div>
            <h1 class="text-3xl md:text-4xl font-bold mb-2">¡Bienvenido, {{ username }}!</h1>
            <p class="text-blue-100 text-lg">Resumen visual de tus tareas</p>
          </div>
          <div class="mt-4 md:mt-0 flex items-center space-x-4">
            <div class="bg-white/10 backdrop-blur-sm rounded-lg p-4 border border-white/20">
              <p class="text-sm text-blue-100">Fecha actual</p>
              <p class="text-xl font-semibold">{{ currentDate }}</p>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- Sección principal de gráficos -->
    <main class="container mx-auto px-4 py-8">
      <!-- Estadísticas rápidas -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">Total tareas</p>
              <h3 class="text-2xl font-bold text-gray-800">{{ stats.totalTasks }}</h3>
            </div>
            <div class="bg-blue-100 p-3 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-green-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">Completadas</p>
              <h3 class="text-2xl font-bold text-gray-800">{{ stats.completedTasks }}</h3>
            </div>
            <div class="bg-green-100 p-3 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-yellow-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">Pendientes</p>
              <h3 class="text-2xl font-bold text-gray-800">{{ stats.pendingTasks }}</h3>
            </div>
            <div class="bg-yellow-100 p-3 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-red-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">Vencidas</p>
              <h3 class="text-2xl font-bold text-gray-800">{{ stats.overdueTasks }}</h3>
            </div>
            <div class="bg-red-100 p-3 rounded-full">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- Gráfico principal -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Gráfico circular de estado de tareas -->
        <div class="lg:col-span-2 bg-white rounded-2xl shadow-xl p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800">Distribución de Tareas</h2>
            <div class="flex space-x-2">
              <button @click="changeChartType('doughnut')" :class="`px-3 py-1 text-xs rounded-full ${chartType === 'doughnut' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600'}`">
                Circular
              </button>
              <button @click="changeChartType('bar')" :class="`px-3 py-1 text-xs rounded-full ${chartType === 'bar' ? 'bg-blue-600 text-white' : 'bg-gray-100 text-gray-600'}`">
                Barras
              </button>
            </div>
          </div>
          <div class="h-80">
            <canvas v-if="hasData" ref="chartCanvas"></canvas>
            <div v-else class="flex flex-col items-center justify-center h-full text-gray-500 space-y-2">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p>No hay tareas asignadas actualmente</p>
            </div>
          </div>
        </div>

        <!-- Tareas urgentes -->
        <div class="bg-white rounded-2xl shadow-xl p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">Tareas urgentes</h3>
          <div class="space-y-4">
            <div v-for="task in urgentTasks" :key="task.id" class="border-l-4 border-red-500 pl-4 py-3 bg-red-50 rounded-r-lg">
              <div class="flex justify-between items-start">
                <div>
                  <h4 class="font-medium text-gray-800">{{ task.name }}</h4>
                  <p class="text-sm text-gray-500">{{ task.projectName }}</p>
                </div>
                <span class="text-xs px-2 py-1 bg-red-100 text-red-800 rounded-full">
                  {{ daysUntilDue(task.endDate) }} días
                </span>
              </div>
              <div class="flex items-center mt-2 text-sm text-gray-500">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span>Vence: {{ formatDate(task.endDate) }}</span>
              </div>
              <div class="mt-2 flex items-center text-sm">
                <span :class="`px-2 py-1 text-xs rounded-full ${getPriorityClass(task.priority)}`">
                  {{ task.priorityDisplayName }}
                </span>
              </div>
            </div>
            <div v-if="urgentTasks.length === 0" class="text-center py-4 text-gray-500">
              No hay tareas urgentes
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import axios from 'axios'
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

import { useAuthStore } from '../../stores/auth'

export default {
  name: 'DashboardView',
  setup() {
    const authStore = useAuthStore()
    const username = ref('Usuario')
    const currentDate = ref(new Date().toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }))
    const assignedTasks = ref([])
    const urgentTasks = ref([])
    const chartCanvas = ref(null)
    const chartType = ref('doughnut')
    let chartInstance = null

    const stats = ref({
      totalTasks: 0,
      completedTasks: 0,
      pendingTasks: 0,
      overdueTasks: 0
    })

    const hasData = computed(() => assignedTasks.value.length > 0)

    const fetchAssignedTasks = async () => {
      try {
        const usernameOrEmail = authStore.user?.email || localStorage.getItem('userEmail')
        if (!usernameOrEmail) throw new Error('No se pudo obtener el usuario autenticado')

        const response = await axios.get(`/api/tasks/assigned-tasks/${usernameOrEmail}`)
        username.value = response.data.user || 'Usuario'
        assignedTasks.value = response.data.assignedTasks || []

        calculateStats(response.data.assignedTasks)
        filterUrgentTasks(response.data.assignedTasks)

        // Renderizar el gráfico después de que los datos estén disponibles
        await nextTick()
        renderChart()

      } catch (error) {
        console.error('Error fetching assigned tasks:', error)
      }
    }

    const calculateStats = (tasks) => {
      if (!tasks) return

      stats.value.totalTasks = tasks.length
      stats.value.completedTasks = tasks.filter(t => t.status === 'COMPLETED').length
      stats.value.pendingTasks = tasks.filter(t => t.status !== 'COMPLETED').length

      const today = new Date()
      stats.value.overdueTasks = tasks.filter(t => {
        if (!t.endDate || t.status === 'COMPLETED') return false
        return new Date(t.endDate) < today
      }).length
    }

    const filterUrgentTasks = (tasks) => {
      const today = new Date()
      const nextWeek = new Date(today)
      nextWeek.setDate(nextWeek.getDate() + 7)

      urgentTasks.value = tasks
        .filter(task => {
          if (!task.endDate || task.status === 'COMPLETED') return false
          const dueDate = new Date(task.endDate)
          return dueDate > today && dueDate <= nextWeek
        })
        .sort((a, b) => new Date(a.endDate) - new Date(b.endDate))
        .slice(0, 3)
    }

    const renderChart = async () => {
      // Esperar a que el DOM esté completamente renderizado
      await nextTick()

      if (!chartCanvas.value || !hasData.value) return

      if (chartInstance) {
        chartInstance.destroy()
      }

      const ctx = chartCanvas.value.getContext('2d')

      if (chartType.value === 'doughnut') {
        chartInstance = new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: ['Completadas', 'En progreso', 'Pendientes', 'Vencidas'],
            datasets: [{
              data: [
                stats.value.completedTasks,
                assignedTasks.value.filter(t => t.status === 'IN_PROGRESS').length,
                assignedTasks.value.filter(t => t.status === 'PENDING').length,
                stats.value.overdueTasks
              ],
              backgroundColor: [
                '#10B981', // verde
                '#3B82F6', // azul
                '#F59E0B', // amarillo
                '#EF4444'  // rojo
              ],
              borderWidth: 0
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
              legend: {
                position: 'bottom',
                labels: {
                  usePointStyle: true,
                  padding: 20
                }
              }
            }
          }
        })
      } else {
        chartInstance = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: ['Completadas', 'En progreso', 'Pendientes', 'Vencidas'],
            datasets: [{
              label: 'Cantidad de tareas',
              data: [
                stats.value.completedTasks,
                assignedTasks.value.filter(t => t.status === 'IN_PROGRESS').length,
                assignedTasks.value.filter(t => t.status === 'PENDING').length,
                stats.value.overdueTasks
              ],
              backgroundColor: [
                '#10B981', // verde
                '#3B82F6', // azul
                '#F59E0B', // amarillo
                '#EF4444'  // rojo
              ],
              borderWidth: 0,
              borderRadius: 4
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true,
                grid: {
                  display: false
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            }
          }
        })
      }
    }

    const changeChartType = (type) => {
      chartType.value = type
      renderChart()
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'Sin fecha'
      const options = { year: 'numeric', month: 'short', day: 'numeric' }
      return new Date(dateString).toLocaleDateString('es-ES', options)
    }

    const daysUntilDue = (dateString) => {
      if (!dateString) return '?'
      const dueDate = new Date(dateString)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const diffTime = dueDate - today
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    }

    const getPriorityClass = (priority) => {
      const classes = {
        HIGH: 'bg-red-100 text-red-800',
        MEDIUM: 'bg-yellow-100 text-yellow-800',
        LOW: 'bg-green-100 text-green-800'
      }
      return classes[priority] || 'bg-gray-100 text-gray-800'
    }

    onMounted(() => {
      fetchAssignedTasks()
    })

    // Remover el watch que causaba problemas
    // watch([assignedTasks, chartType], () => {
    //   renderChart()
    // })

    return {
      username,
      currentDate,
      assignedTasks,
      urgentTasks,
      stats,
      hasData,
      chartCanvas,
      chartType,
      changeChartType,
      formatDate,
      daysUntilDue,
      getPriorityClass
    }
  }
}
</script>

<style scoped>
/* Transición suave para los botones de cambio de gráfico */
button {
  transition: all 0.2s ease;
}
</style>
