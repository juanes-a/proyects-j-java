<template>
  <div class="container">
    <div class="header">
      <h1>Dashboard de Tareas</h1>
      <p>Gestión y análisis de proyectos</p>
    </div>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="loading" class="loading">Cargando datos...</div>

    <div v-else>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ totalTasks }}</div>
          <div class="stat-label">Total Tareas</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ completedTasks }}</div>
          <div class="stat-label">Completadas</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ pendingTasks }}</div>
          <div class="stat-label">Pendientes</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ overdueTasks }}</div>
          <div class="stat-label">Vencidas</div>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-container">
          <div class="chart-title">Distribución por Estado</div>
          <canvas ref="statusChart"></canvas>
        </div>
        <div class="chart-container">
          <div class="chart-title">Distribución por Prioridad</div>
          <canvas ref="priorityChart"></canvas>
        </div>
      </div>

      <div class="filters">
        <div class="filter-row">
          <div class="filter-group">
            <label>Estado:</label>
            <select v-model="filters.status" @change="filterTasks">
              <option value="">Todos</option>
              <option value="PENDING">Pendiente</option>
              <option value="IN_PROGRESS">En Progreso</option>
              <option value="IN_REVIEW">En Revisión</option>
              <option value="COMPLETED">Completada</option>
              <option value="CANCELLED">Cancelada</option>
            </select>
          </div>
          <div class="filter-group">
            <label>Prioridad:</label>
            <select v-model="filters.priority" @change="filterTasks">
              <option value="">Todas</option>
              <option value="LOW">Baja</option>
              <option value="MEDIUM">Media</option>
              <option value="HIGH">Alta</option>
              <option value="URGENT">Urgente</option>
            </select>
          </div>
          <div class="filter-group">
            <label>Búsqueda:</label>
            <input type="text" v-model="filters.keyword" @input="filterTasks" placeholder="Buscar tareas...">
          </div>
        </div>
      </div>

      <div class="tasks-table">
        <div class="table-header">Lista de Tareas</div>
        <div class="table-content">
          <div v-for="task in filteredTasks" :key="task.id" class="task-row">
            <div>
              <strong>{{ task.name }}</strong>
              <div style="font-size: 0.9rem; color: #7f8c8d;">{{ task.description || 'Sin descripción' }}</div>
            </div>
            <div>
              <span :class="'status-badge status-' + task.status.toLowerCase().replace('_', '-')">
                {{ getStatusDisplay(task.status) }}
              </span>
            </div>
            <div :class="'priority-' + task.priority.toLowerCase()">
              {{ getPriorityDisplay(task.priority) }}
            </div>
            <div>{{ formatDate(task.endDate) }}</div>
            <div>{{ getProjectName(task) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'TaskDash',
  setup() {
    const loading = ref(true)
    const error = ref(null)
    const tasks = ref([])
    const filteredTasks = ref([])
    const filters = ref({
      status: '',
      priority: '',
      keyword: ''
    })
    const statusChart = ref(null)
    const priorityChart = ref(null)

    const totalTasks = computed(() => tasks.value.length)
    const completedTasks = computed(() => tasks.value.filter(t => t.status === 'COMPLETED').length)
    const pendingTasks = computed(() => tasks.value.filter(t => t.status === 'PENDING').length)
    const overdueTasks = computed(() =>
      tasks.value.filter(t => t.endDate && new Date(t.endDate) < new Date() && t.status !== 'COMPLETED').length
    )

    const loadTasks = async () => {
      try {
        const response = await fetch('http://localhost:8081/api/tasks')
        if (!response.ok) throw new Error('Error al cargar tareas')
        tasks.value = await response.json()
        filteredTasks.value = [...tasks.value]
      } catch (err) {
        error.value = 'Error al conectar con el servidor: ' + err.message
      } finally {
        loading.value = false
      }
    }

    const filterTasks = () => {
      filteredTasks.value = tasks.value.filter(task => {
        const statusMatch = !filters.value.status || task.status === filters.value.status
        const priorityMatch = !filters.value.priority || task.priority === filters.value.priority
        const keywordMatch = !filters.value.keyword ||
          task.name.toLowerCase().includes(filters.value.keyword.toLowerCase()) ||
          (task.description && task.description.toLowerCase().includes(filters.value.keyword.toLowerCase()))
        return statusMatch && priorityMatch && keywordMatch
      })
    }

    const createStatusChart = () => {
      const ctx = statusChart.value.getContext('2d')
      const statusCounts = {
        'PENDING': tasks.value.filter(t => t.status === 'PENDING').length,
        'IN_PROGRESS': tasks.value.filter(t => t.status === 'IN_PROGRESS').length,
        'IN_REVIEW': tasks.value.filter(t => t.status === 'IN_REVIEW').length,
        'COMPLETED': tasks.value.filter(t => t.status === 'COMPLETED').length,
        'CANCELLED': tasks.value.filter(t => t.status === 'CANCELLED').length
      }

      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: ['Pendiente', 'En Progreso', 'En Revisión', 'Completada', 'Cancelada'],
          datasets: [{
            data: Object.values(statusCounts),
            backgroundColor: ['#ffeaa7', '#74b9ff', '#fd79a8', '#00b894', '#636e72']
          }]
        },
        options: { responsive: true, maintainAspectRatio: false }
      })
    }

    const createPriorityChart = () => {
      const ctx = priorityChart.value.getContext('2d')
      const priorityCounts = {
        'LOW': tasks.value.filter(t => t.priority === 'LOW').length,
        'MEDIUM': tasks.value.filter(t => t.priority === 'MEDIUM').length,
        'HIGH': tasks.value.filter(t => t.priority === 'HIGH').length,
        'URGENT': tasks.value.filter(t => t.priority === 'URGENT').length
      }

      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['Baja', 'Media', 'Alta', 'Urgente'],
          datasets: [{
            label: 'Tareas por Prioridad',
            data: Object.values(priorityCounts),
            backgroundColor: ['#00b894', '#fdcb6e', '#e17055', '#d63031']
          }]
        },
        options: { responsive: true, maintainAspectRatio: false }
      })
    }

    const createCharts = () => {
      createStatusChart()
      createPriorityChart()
    }

    const getStatusDisplay = (status) => {
      const statusMap = {
        'PENDING': 'Pendiente',
        'IN_PROGRESS': 'En Progreso',
        'IN_REVIEW': 'En Revisión',
        'COMPLETED': 'Completada',
        'CANCELLED': 'Cancelada'
      }
      return statusMap[status] || status
    }

    const getPriorityDisplay = (priority) => {
      const priorityMap = {
        'LOW': 'Baja',
        'MEDIUM': 'Media',
        'HIGH': 'Alta',
        'URGENT': 'Urgente'
      }
      return priorityMap[priority] || priority
    }

    const getProjectName = (task) => {
      return task.project ? (task.project.name || `Proyecto ${task.project.id}`) : 'Sin proyecto'
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'Sin fecha'
      return new Date(dateString).toLocaleDateString('es-ES')
    }

    onMounted(async () => {
      await loadTasks()
      createCharts()
    })

    return {
      loading,
      error,
      tasks,
      filteredTasks,
      filters,
      statusChart,
      priorityChart,
      totalTasks,
      completedTasks,
      pendingTasks,
      overdueTasks,
      filterTasks,
      getStatusDisplay,
      getPriorityDisplay,
      getProjectName,
      formatDate
    }
  }
}
</script>

<style scoped>
.container { max-width: 1400px; margin: 0 auto; padding: 20px; }
.header { background: #2c3e50; color: white; padding: 20px; border-radius: 10px; margin-bottom: 30px; }
.header h1 { font-size: 2.5rem; margin-bottom: 10px; }
.stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
.stat-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
.stat-number { font-size: 2.5rem; font-weight: bold; color: #3498db; }
.stat-label { color: #7f8c8d; font-size: 0.9rem; text-transform: uppercase; }
.charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 30px; }
.chart-container { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.chart-title { font-size: 1.2rem; font-weight: bold; margin-bottom: 15px; color: #2c3e50; }
.tasks-table { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }
.table-header { background: #34495e; color: white; padding: 15px; font-weight: bold; }
.table-content { max-height: 400px; overflow-y: auto; }
.task-row { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr 1fr; padding: 15px; border-bottom: 1px solid #ecf0f1; align-items: center; }
.task-row:hover { background: #f8f9fa; }
.status-badge { padding: 5px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: bold; }
.status-pending { background: #ffeaa7; color: #d63031; }
.status-in-progress { background: #74b9ff; color: white; }
.status-in-review { background: #fd79a8; color: white; }
.status-completed { background: #00b894; color: white; }
.status-cancelled { background: #636e72; color: white; }
.priority-high { color: #e17055; font-weight: bold; }
.priority-medium { color: #fdcb6e; font-weight: bold; }
.priority-low { color: #00b894; font-weight: bold; }
.priority-urgent { color: #d63031; font-weight: bold; }
.loading { text-align: center; padding: 50px; color: #7f8c8d; }
.error { background: #e74c3c; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
.filters { background: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
.filter-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }
.filter-group { display: flex; flex-direction: column; }
.filter-group label { margin-bottom: 5px; font-weight: bold; color: #2c3e50; }
.filter-group select, .filter-group input { padding: 8px; border: 1px solid #ddd; border-radius: 5px; }
@media (max-width: 768px) {
  .charts-grid { grid-template-columns: 1fr; }
  .task-row { grid-template-columns: 1fr; gap: 10px; }
}
</style>
