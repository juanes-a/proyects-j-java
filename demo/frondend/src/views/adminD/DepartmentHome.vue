<template>
  <div class="space-y-6">
    <!-- Department Header Banner -->
    <div class="bg-gradient-to-r from-indigo-600 to-purple-700 rounded-xl p-6 text-white relative overflow-hidden">
      <div class="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -translate-y-16 translate-x-16"></div>
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full translate-y-12 -translate-x-12"></div>
      <div class="relative z-10">
        <div class="flex items-center justify-between">
          <div>
            <div class="flex items-center space-x-3 mb-2">
              <div class="w-12 h-12 bg-white/20 rounded-lg flex items-center justify-center">
                <Building2 class="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 class="text-2xl font-bold">{{ departmentInfo.name || 'My Department' }}</h1>
                <p class="text-indigo-100">Department Dashboard</p>
              </div>
            </div>
          </div>
          <div class="hidden md:block">
            <div class="flex items-center space-x-2">
              <div class="text-right">
                <p class="text-sm text-indigo-200">Productivity</p>
                <p class="text-xl font-bold">{{ departmentInfo.productivity || 0 }}%</p>
              </div>
              <div class="w-16 h-16 bg-white/10 rounded-full flex items-center justify-center">
                <TrendingUp class="w-6 h-6 text-white/80" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Cards Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- Total Projects Card -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center space-x-4">
          <div class="w-12 h-12 bg-indigo-100 dark:bg-indigo-900/50 rounded-lg flex items-center justify-center">
            <FolderOpen class="w-6 h-6 text-indigo-600 dark:text-indigo-400" />
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Total Projects</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">
              {{ loading ? '...' : departmentStats.totalProjects }}
            </p>
            <p class="text-xs mt-1" :class="projectChange >= 0 ? 'text-green-500' : 'text-red-500'">
              {{ projectChange >= 0 ? '↑' : '↓' }} {{ Math.abs(projectChange) }}% from last month
            </p>
          </div>
        </div>
      </div>

      <!-- Completed Projects Card -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center space-x-4">
          <div class="w-12 h-12 bg-green-100 dark:bg-green-900/50 rounded-lg flex items-center justify-center">
            <CheckCircle class="w-6 h-6 text-green-600 dark:text-green-400" />
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Completed</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">
              {{ loading ? '...' : departmentStats.completedProjects }}
            </p>
            <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">
              {{ completionRate }}% completion rate
            </p>
          </div>
        </div>
      </div>

      <!-- Active Projects Card -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center space-x-4">
          <div class="w-12 h-12 bg-amber-100 dark:bg-amber-900/50 rounded-lg flex items-center justify-center">
            <Activity class="w-6 h-6 text-amber-600 dark:text-amber-400" />
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Active</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">
              {{ loading ? '...' : departmentStats.activeProjects }}
            </p>
            <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">
              {{ activePercentage }}% of total
            </p>
          </div>
        </div>
      </div>

      <!-- Department Budget Card -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center space-x-4">
          <div class="w-12 h-12 bg-blue-100 dark:bg-blue-900/50 rounded-lg flex items-center justify-center">
            <DollarSign class="w-6 h-6 text-blue-600 dark:text-blue-400" />
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">Budget</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">
              {{ loading ? '...' : formatCurrency(departmentStats.totalBudget) }}
            </p>
            <div class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-1.5 mt-2">
              <div class="bg-blue-600 h-1.5 rounded-full" :style="{ width: budgetUsage + '%' }"></div>
            </div>
            <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">
              {{ budgetUsage }}% utilized
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Projects Status Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-4">Projects Status</h3>
        <div class="h-64">
          <PieChart 
            v-if="!loading"
            :data="projectsStatusData"
            :options="pieChartOptions"
          />
          <div v-else class="h-full flex items-center justify-center">
            <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
          </div>
        </div>
      </div>

      <!-- Budget Allocation Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-4">Budget Allocation</h3>
        <div class="h-64">
          <BarChart
            v-if="!loading"
            :data="budgetAllocationData"
            :options="barChartOptions"
          />
          <div v-else class="h-full flex items-center justify-center">
            <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activities -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
      <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-4">Recent Activities</h3>
      <div class="space-y-4">
        <div v-for="(activity, index) in recentActivities" :key="index" class="flex items-start space-x-3">
          <div class="flex-shrink-0 mt-1">
            <div class="w-8 h-8 rounded-full flex items-center justify-center" 
                 :class="activityColors[activity.type]">
              <component :is="activityIcons[activity.type]" class="w-4 h-4 text-white" />
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-gray-900 dark:text-white">{{ activity.title }}</p>
            <p class="text-sm text-gray-500 dark:text-gray-400">{{ activity.description }}</p>
            <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">{{ formatDate(activity.date) }}</p>
          </div>
        </div>
        <div v-if="recentActivities.length === 0" class="text-center py-4">
          <p class="text-gray-500 dark:text-gray-400">No recent activities</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Building2, 
  FolderOpen, 
  DollarSign, 
  CheckCircle, 
  Activity, 
  TrendingUp,
  Clock,
  Loader2,
  AlertCircle,
  FileText,
  Users  
} from 'lucide-vue-next'
import api from '../../api/index'
import { useToastStore } from '../../stores/toast'
import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'
import PieChart from '../../components/charts/PieChart.vue'
import BarChart from '../../components/charts/BarChart.vue'

const router = useRouter()
const toastStore = useToastStore()
const authStore = useAuthStore()

const loading = ref(true)

// Department data
const departmentInfo = ref({
  id: null,
  name: 'Loading department...',
  productivity: 0
})

// Stats data
const departmentStats = ref({
  totalProjects: 0,
  completedProjects: 0,
  activeProjects: 0,
  plannedProjects: 0,
  cancelledProjects: 0,
  totalBudget: 0,
  usedBudget: 0,
  lastMonthProjects: 0
})

// Recent activities mock data (replace with real data)
const recentActivities = ref([
  {
    type: 'project',
    title: 'New project started',
    description: 'Project "Website Redesign" has been initiated',
    date: new Date(Date.now() - 3600000 * 2)
  },
  {
    type: 'task',
    title: 'Task completed',
    description: 'Task "Homepage layout" marked as done by John Doe',
    date: new Date(Date.now() - 3600000 * 5)
  },
  {
    type: 'budget',
    title: 'Budget updated',
    description: 'Additional $5,000 allocated to marketing',
    date: new Date(Date.now() - 3600000 * 24)
  }
])

const activityIcons = {
  project: FileText,
  task: CheckCircle,
  budget: DollarSign,
  team: Users,
  alert: AlertCircle
}

const activityColors = {
  project: 'bg-indigo-500',
  task: 'bg-green-500',
  budget: 'bg-blue-500',
  team: 'bg-purple-500',
  alert: 'bg-amber-500'
}

// Computed properties
const completionRate = computed(() => {
  if (departmentStats.value.totalProjects === 0) return 0
  return Math.round((departmentStats.value.completedProjects / departmentStats.value.totalProjects) * 100)
})

const activePercentage = computed(() => {
  if (departmentStats.value.totalProjects === 0) return 0
  return Math.round((departmentStats.value.activeProjects / departmentStats.value.totalProjects) * 100)
})

const budgetUsage = computed(() => {
  if (departmentStats.value.totalBudget === 0) return 0
  return Math.round((departmentStats.value.usedBudget / departmentStats.value.totalBudget) * 100)
})

const projectChange = computed(() => {
  if (departmentStats.value.lastMonthProjects === 0) return 0
  const change = ((departmentStats.value.totalProjects - departmentStats.value.lastMonthProjects) / 
                departmentStats.value.lastMonthProjects) * 100
  return Math.round(change)
})

// Chart data
const projectsStatusData = computed(() => ({
  labels: ['Completed', 'Active', 'Planned', 'Cancelled'],
  datasets: [{
    data: [
      departmentStats.value.completedProjects,
      departmentStats.value.activeProjects,
      departmentStats.value.plannedProjects,
      departmentStats.value.cancelledProjects
    ],
    backgroundColor: [
      '#10B981', // green
      '#3B82F6', // blue
      '#F59E0B', // amber
      '#EF4444'  // red
    ]
  }]
}))

const budgetAllocationData = computed(() => ({
  labels: ['Used', 'Remaining'],
  datasets: [{
    label: 'Budget',
    data: [
      departmentStats.value.usedBudget,
      departmentStats.value.totalBudget - departmentStats.value.usedBudget
    ],
    backgroundColor: [
      '#3B82F6', // blue
      '#E5E7EB'  // gray
    ]
  }]
}))

// Chart options
const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom'
    }
  }
}

const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true
    }
  },
  plugins: {
    legend: {
      display: false
    }
  }
}

const fetchDepartmentData = async () => {
  try {
    loading.value = true
    
    if (!authStore.isAuthenticated) {
      throw new Error('User not authenticated')
    }

    // 1. Get authenticated user info
    const userEmail = authStore.user?.email
    if (!userEmail) {
      throw new Error('Could not get user email')
    }

    // 2. Get department assignment
    const assignmentResponse = await api.get(`/departments/user/${encodeURIComponent(userEmail)}`)
    if (!assignmentResponse.data?.department) {
      throw new Error('User not assigned to any department')
    }

    const department = assignmentResponse.data.department
    
    // 3. Update basic department info
    departmentInfo.value = {
      id: department.id,
      name: department.name,
      productivity: assignmentResponse.data.productivity || 0
    }

    // 4. Get department stats
    const statsResponse = await api.get(`/departments/${department.id}/stats`)
    const stats = statsResponse.data

    // Update stats data
    departmentStats.value = {
      totalProjects: (stats.activeProjects || 0) + (stats.completedProjects || 0) + 
                    (stats.plannedProjects || 0) + (stats.cancelledProjects || 0),
      completedProjects: stats.completedProjects || 0,
      activeProjects: stats.activeProjects || 0,
      plannedProjects: stats.plannedProjects || 0,
      cancelledProjects: stats.cancelledProjects || 0,
      totalBudget: stats.totalBudget || 0,
      usedBudget: stats.usedBudget || 0,
      lastMonthProjects: stats.lastMonthProjects || 0
    }

    // 5. Get recent activities (si tu API tiene este endpoint)
    // const activitiesResponse = await api.get(`/departments/${department.id}/activities`)
    // recentActivities.value = activitiesResponse.data || []

  } catch (error) {
    console.error('Error loading department data:', error)
    
    let errorMessage = 'Error loading department data'
    if (error.response?.status === 400) {
      errorMessage = 'Bad request to server'
    } else if (error.response?.status === 404) {
      errorMessage = 'Department not found'
    } else if (error.message.includes('not assigned')) {
      errorMessage = 'You are not assigned to any department'
      router.push('/unauthorized')
    }

    toastStore.showToast(errorMessage, 'error')
    departmentInfo.value.name = 'Error loading data'

  } finally {
    loading.value = false
  }
}

const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const formatDate = (date) => {
  return new Date(date).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchDepartmentData()
})
</script>