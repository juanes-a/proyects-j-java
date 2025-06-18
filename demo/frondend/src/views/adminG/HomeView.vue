<template>
  <div class="space-y-6">
    <!-- Welcome Banner -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-700 rounded-xl p-6 text-white">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-3xl font-bold mb-2">Welcome back! 👋</h2>
          <p class="text-blue-100">Here's what's happening with your departments today.</p>
        </div>
        <div class="hidden md:block">
          <div class="w-24 h-24 bg-white/10 rounded-full flex items-center justify-center">
            <TrendingUp class="w-12 h-12 text-white/80" />
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <StatsCard
        v-for="stat in stats"
        :key="stat.title"
        :title="stat.title"
        :value="stat.value"
        :change="stat.change"
        :icon="stat.icon"
        :color="stat.color"
        :loading="loading"
      />
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Budget Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Budget by Department</h3>
          <div class="flex space-x-2">
            <button
              @click="changeChartType('bar')"
              :class="chartType === 'bar' ? 'bg-blue-500 text-white' : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400'"
              class="px-3 py-1 rounded-md text-sm transition-colors duration-200"
            >
              Bar
            </button>
            <button
              @click="changeChartType('pie')"
              :class="chartType === 'pie' ? 'bg-blue-500 text-white' : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-400'"
              class="px-3 py-1 rounded-md text-sm transition-colors duration-200"
            >
              Pie
            </button>
          </div>
        </div>
        <div class="h-80">
          <canvas ref="budgetChart"></canvas>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Recent Activity</h3>
        <div class="space-y-4">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="flex items-start space-x-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
          >
            <div :class="activity.iconBg" class="w-8 h-8 rounded-full flex items-center justify-center">
              <component :is="activity.icon" class="w-4 h-4 text-white" />
            </div>
            <div class="flex-1">
              <p class="text-sm font-medium text-gray-800 dark:text-white">{{ activity.title }}</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">{{ activity.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
      <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Quick Actions</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <button
          v-for="action in quickActions"
          :key="action.title"
          @click="action.action"
          class="flex items-center space-x-3 p-4 rounded-lg border-2 border-dashed border-gray-200 dark:border-gray-600 hover:border-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-all duration-200 group"
        >
          <div :class="action.iconBg" class="w-10 h-10 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
            <component :is="action.icon" class="w-5 h-5 text-white" />
          </div>
          <div class="text-left">
            <p class="font-medium text-gray-800 dark:text-white">{{ action.title }}</p>
            <p class="text-sm text-gray-500 dark:text-gray-400">{{ action.description }}</p>
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { 
  TrendingUp, 
  Building2, 
  FolderOpen, 
  Users, 
  Plus, 
  Edit, 
  Eye 
} from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import api from '../../api'
import StatsCard from '../../components/StatsCard.vue'
import { useToastStore } from '../../stores/toast'

const router = useRouter()
const toastStore = useToastStore()

// Estados reactivos
const loading = ref(true)
const chartType = ref('bar')
const budgetChart = ref(null)
let chartInstance = null

// Datos iniciales - usar markRaw para componentes que se pasan a component :is
const stats = ref([
  { 
    title: 'Total Departments', 
    value: '0', 
    change: 'Loading...', 
    icon: markRaw(Building2),
    color: 'blue' 
  },
  { 
    title: 'Active Projects', 
    value: '0', 
    change: 'Loading...', 
    icon: markRaw(FolderOpen),
    color: 'green' 
  },
  { 
    title: 'Total Budget', 
    value: '$0', 
    change: 'Loading...', 
    icon: markRaw(TrendingUp),
    color: 'purple' 
  },
  { 
    title: 'Team Members', 
    value: '0', 
    change: 'Loading...', 
    icon: markRaw(Users),
    color: 'orange' 
  }
])

const recentActivities = ref([])

// Para quickActions, usa markRaw si los vas a usar en component :is
const quickActions = [
  {
    title: 'Add Department',
    description: 'Create a new department',
    icon: markRaw(Building2),
    iconBg: 'bg-blue-500',
    action: () => router.push('/departments?action=create')
  },
  {
    title: 'New Project',
    description: 'Start a new project',
    icon: markRaw(FolderOpen),
    iconBg: 'bg-green-500',
    action: () => router.push('/projects?action=create')
  },
  {
    title: 'Add Team Member',
    description: 'Invite new team member',
    icon: markRaw(Users),
    iconBg: 'bg-purple-500',
    action: () => router.push('/team?action=invite')
  }
]

const chartData = ref({
  labels: [],
  datasets: [{
    label: 'Budget',
    data: [],
    backgroundColor: [
      '#3B82F6', '#10B981', '#8B5CF6', '#F59E0B', '#EF4444', '#06B6D4'
    ],
    borderColor: [
      '#2563EB', '#059669', '#7C3AED', '#D97706', '#DC2626', '#0891B2'
    ],
    borderWidth: 2
  }]
})

// Métodos
const fetchDashboardData = async () => {
  try {
    loading.value = true
    
    // 1. Cargar estadísticas principales desde DepartmentController
    const statsResponse = await api.get('/departments/stats')
    const statsData = statsResponse.data
    
    stats.value = [
      { 
        ...stats.value[0],
        value: statsData.totalDepartments?.toString() || '0',
        change: statsData.departmentsChange || '0%'
      },
      { 
        ...stats.value[1],
        value: statsData.activeProjects?.toString() || '0',
        change: statsData.projectsChange || '0%'
      },
      { 
        ...stats.value[2],
        value: `$${(statsData.totalBudget || 0).toLocaleString()}`,
        change: statsData.budgetChange || '0%'
      },
      { 
        ...stats.value[3],
        value: statsData.teamMembers?.toString() || '0',
        change: statsData.teamChange || '0%'
      }
    ]
    
    // 2. Cargar datos para el gráfico
    const departmentsResponse = await api.get('/departments')
    const departments = departmentsResponse.data
    
    chartData.value.labels = departments.map(dept => dept.name)
    chartData.value.datasets[0].data = departments.map(dept => dept.budget || 0)
    
    // 3. Cargar actividades recientes con manejo de errores
    try {
      const activitiesResponse = await api.get('/activities/recent')
      recentActivities.value = (activitiesResponse.data || []).map(act => ({
        id: act.id,
        title: act.description || 'Activity',
        time: formatTimeAgo(act.createdAt || new Date().toISOString()),
        icon: getActivityIcon(act.type),
        iconBg: getActivityBg(act.type)
      }))
    } catch (error) {
      console.error('Error loading activities:', error)
      // Mostrar actividades de ejemplo si hay error
      recentActivities.value = [
        {
          id: 1,
          title: 'Sample activity',
          time: 'Just now',
          icon: markRaw(Eye),
          iconBg: 'bg-gray-500'
        }
      ]
    }
    
    await nextTick()
    createChart()
    
  } catch (error) {
    console.error('Error fetching dashboard data:', error)
    toastStore.showToast('Error loading dashboard data', 'error')
  } finally {
    loading.value = false
  }
}

const createChart = () => {
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const ctx = budgetChart.value?.getContext('2d')
  if (!ctx) return
  
  chartInstance = new Chart(ctx, {
    type: chartType.value,
    data: chartData.value,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: chartType.value === 'pie',
          position: 'bottom'
        }
      },
      scales: chartType.value === 'bar' ? {
        y: {
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return '$' + value.toLocaleString()
            }
          }
        }
      } : {}
    }
  })
}

const changeChartType = (type) => {
  chartType.value = type
  createChart()
}

// Helpers
const getActivityIcon = (type) => {
  const iconMap = {
    'DEPARTMENT_CREATED': markRaw(Building2),
    'PROJECT_UPDATED': markRaw(Edit),
    'TEAM_MEMBER_ADDED': markRaw(Users),
    'REPORT_GENERATED': markRaw(Eye)
  }
  return iconMap[type] || markRaw(Eye)
}

const getActivityBg = (type) => {
  const colorMap = {
    'DEPARTMENT_CREATED': 'bg-blue-500',
    'PROJECT_UPDATED': 'bg-green-500',
    'TEAM_MEMBER_ADDED': 'bg-purple-500',
    'REPORT_GENERATED': 'bg-orange-500'
  }
  return colorMap[type] || 'bg-gray-500'
}

const formatTimeAgo = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return 'Just now'
  if (minutes < 60) return `${minutes} min ago`
  if (minutes < 1440) return `${Math.floor(minutes / 60)} hours ago`
  return `${Math.floor(minutes / 1440)} days ago`
}

// Inicialización
onMounted(() => {
  fetchDashboardData()
})
</script>