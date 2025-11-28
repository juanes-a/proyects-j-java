<template>
  <div class="min-h-screen bg-gray-50">
    <header class="bg-gradient-to-r from-blue-600 to-indigo-800 text-white shadow-lg">
      <div class="container mx-auto px-4 py-8">
        <div class="flex flex-col md:flex-row justify-between items-center">
          <div>
            <h1 class="text-3xl md:text-4xl font-bold mb-2">Welcome, {{ username }}!</h1>
            <p class="text-blue-100 text-lg">Visual summary of your tasks</p>
          </div>
          <div class="mt-4 md:mt-0 flex items-center space-x-4">
            <div class="bg-white/10 backdrop-blur-sm rounded-lg p-4 border border-white/20">
              <p class="text-sm text-blue-100">Current Date</p>
              <p class="text-xl font-semibold">{{ currentDate }}</p>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Quick Stats</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-blue-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium uppercase">Total Tasks</p>
              <p class="text-2xl font-bold text-gray-800">{{ stats.total || 0 }}</p>
            </div>
            <div class="p-3 bg-blue-100 rounded-full">
              <i class="fas fa-tasks text-blue-600 text-xl"></i>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-green-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium uppercase">Completed</p>
              <p class="text-2xl font-bold text-gray-800">{{ stats.completed || 0 }}</p>
            </div>
            <div class="p-3 bg-green-100 rounded-full">
              <i class="fas fa-check-circle text-green-600 text-xl"></i>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-yellow-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium uppercase">Pending</p>
              <p class="text-2xl font-bold text-gray-800">{{ stats.pending || 0 }}</p>
            </div>
            <div class="p-3 bg-yellow-100 rounded-full">
              <i class="fas fa-clock text-yellow-600 text-xl"></i>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-md p-6 border-l-4 border-red-500">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm font-medium uppercase">Urgent</p>
              <p class="text-2xl font-bold text-gray-800">{{ stats.urgent || 0 }}</p>
            </div>
            <div class="p-3 bg-red-100 rounded-full">
              <i class="fas fa-exclamation-triangle text-red-600 text-xl"></i>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
        <div class="bg-white rounded-xl shadow-md p-6">
          <h3 class="text-lg font-bold text-gray-800 mb-4">Task Distribution</h3>
          <div class="h-64 relative">
             <canvas ref="taskChart"></canvas>
          </div>
        </div>
        
        <div class="bg-white rounded-xl shadow-md p-6">
          <div class="flex items-center justify-between mb-4">
             <h3 class="text-lg font-bold text-gray-800">Upcoming Deadlines</h3>
             <button class="text-sm text-blue-600 hover:text-blue-800 font-medium">View all</button>
          </div>
          <div class="space-y-4">
            <div v-for="task in urgentTasks" :key="task.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
               <div>
                 <p class="font-medium text-gray-800">{{ task.title }}</p>
                 <p class="text-xs text-gray-500">Due: {{ formatDate(task.dueDate) }}</p>
               </div>
               <span :class="getPriorityClass(task.priority)" class="px-2 py-1 text-xs rounded-full font-semibold">
                 {{ task.priority }}
               </span>
            </div>
            <p v-if="urgentTasks.length === 0" class="text-gray-500 text-center py-4">No upcoming deadlines</p>
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
