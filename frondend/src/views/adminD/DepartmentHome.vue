<template>
  <div class="w-full min-h-screen animate-fade-in-up">
    <div class="max-w-none space-y-6">
      
      <div class="relative overflow-hidden bg-gradient-to-br from-blue-700 via-blue-600 to-cyan-500 rounded-2xl p-6 sm:p-8 text-white shadow-xl shadow-cyan-500/20">
        <div class="absolute top-0 right-0 -mt-8 -mr-8 w-40 h-40 bg-cyan-400/20 rounded-full blur-3xl"></div>
        <div class="absolute bottom-0 left-0 -mb-8 -ml-8 w-32 h-32 bg-blue-900/40 rounded-full blur-2xl"></div>
        
        <div class="relative flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 z-10">
          <div class="flex items-center space-x-4">
             <div class="hidden sm:flex w-16 h-16 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl items-center justify-center shadow-inner">
              <Building2 class="w-8 h-8 text-cyan-50" />
            </div>
            
            <div class="min-w-0">
              <h2 class="text-3xl sm:text-4xl font-extrabold tracking-tight truncate">
                {{ departmentInfo.name || 'Loading...' }}
              </h2>
              <p class="text-blue-100 text-sm sm:text-base font-medium flex items-center gap-2 mt-1">
                <span :class="['w-2 h-2 rounded-full animate-pulse', departmentInfo.id ? 'bg-cyan-400' : 'bg-gray-400']"></span>
                Department Dashboard
              </p>
            </div>
          </div>

          <div class="hidden md:block">
            <div class="flex items-center space-x-2 bg-white/10 backdrop-blur-sm rounded-xl p-3 border border-white/10">
              <div class="text-right mr-2">
                <p class="text-xs text-cyan-200 font-medium uppercase tracking-wider">Productivity</p>
                <p class="text-xl font-bold">{{ departmentInfo.productivity || 0 }}%</p>
              </div>
              <div class="w-10 h-10 bg-gradient-to-br from-cyan-400 to-blue-500 rounded-full flex items-center justify-center shadow-lg">
                <TrendingUp class="w-5 h-5 text-white" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 lg:gap-6">
        
        <StatsCard
          title="Total Projects"
          :value="departmentStats.totalProjects"
          :change="`${Math.abs(projectChange)}% from last month`"
          :changeType="projectChange >= 0 ? 'positive' : 'negative'"
          :icon="markRaw(FolderOpen)"
          color="cyan"
          :loading="loading"
        />
        
        <StatsCard
          title="Completed"
          :value="departmentStats.completedProjects"
          :change="`${completionRate}% Completion Rate`"
          :icon="markRaw(CheckCircle)"
          color="emerald"
          :loading="loading"
        />

        <StatsCard
          title="Active Projects"
          :value="departmentStats.activeProjects"
          :change="`${activePercentage}% of total`"
          :icon="markRaw(Activity)"
          color="blue"
          :loading="loading"
        />

        <StatsCard
          title="Total Budget"
          :value="formatCurrency(departmentStats.totalBudget)"
          :change="`${budgetUsage}% Utilized`"
          :icon="markRaw(DollarSign)"
          color="indigo"
          :loading="loading"
        />
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
        
        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6">
          <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <PieChartIcon class="w-5 h-5 text-cyan-500" /> Projects Status
          </h3>
          <div class="h-64 relative">
             <PieChart 
                v-if="!loading"
                :data="projectsStatusData"
                :options="pieChartOptions"
              />
             <div v-else class="absolute inset-0 flex items-center justify-center">
                <Loader2 class="w-8 h-8 text-cyan-500 animate-spin" />
             </div>
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6">
          <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <BarChartIcon class="w-5 h-5 text-blue-500" />
            Budget Allocation
          </h3>
          <div class="h-64 relative">
            <BarChart
              v-if="!loading"
              :data="budgetAllocationData"
              :options="barChartOptions"
            />
            <div v-else class="absolute inset-0 flex items-center justify-center">
                <Loader2 class="w-8 h-8 text-blue-500 animate-spin" />
             </div>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6">
          <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <Clock class="w-5 h-5 text-cyan-500" />
            Recent Activities
          </h3>
          
          <div class="space-y-4">
             <div v-for="(activity, index) in recentActivities" :key="index" class="flex items-start space-x-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-zinc-800/50 transition-colors">
               <div class="flex-shrink-0 mt-1">
                 <div class="w-8 h-8 rounded-full flex items-center justify-center shadow-sm" 
                      :class="activityColors[activity.type]">
                   <component :is="activityIcons[activity.type]" class="w-4 h-4 text-white" />
                 </div>
               </div>
               <div class="flex-1 min-w-0">
                 <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ activity.title }}</p>
                 <p class="text-sm text-gray-500 dark:text-gray-400">{{ activity.description }}</p>
                 <p class="text-xs text-gray-400 dark:text-gray-500 mt-1 font-mono">{{ formatDate(activity.date) }}</p>
               </div>
             </div>
             
             <div v-if="recentActivities.length === 0" class="text-center py-8">
               <p class="text-gray-500 dark:text-gray-400">No recent activities</p>
             </div>
          </div>
        </div>
      
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
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
  Users,
  PieChart as PieChartIcon,
  BarChart as BarChartIcon
} from 'lucide-vue-next'
import api from '../../api/index'
import { useToastStore } from '../../stores/toast'
import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'
// Usamos TUS componentes de gráficos
import PieChart from '../../components/charts/PieChart.vue'
import BarChart from '../../components/charts/BarChart.vue'
import StatsCard from '../../components/StatsCard.vue' // Importamos el componente StatsCard

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

// Recent activities mock data (adaptado al nuevo tema)
const recentActivities = ref([
  {
    type: 'project',
    title: 'New project started',
    description: 'Project "Cyber Infrastructure" has been initiated',
    date: new Date(Date.now() - 3600000 * 2)
  },
  {
    type: 'task',
    title: 'Milestone reached',
    description: 'Phase 1 completed ahead of schedule',
    date: new Date(Date.now() - 3600000 * 5)
  },
  {
    type: 'budget',
    title: 'Budget increased',
    description: 'Quarterly budget review approved',
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

// Colores actualizados al tema Cyber Blue
const activityColors = {
  project: 'bg-cyan-500',
  task: 'bg-emerald-500',
  budget: 'bg-blue-500',
  team: 'bg-indigo-500',
  alert: 'bg-amber-500'
}

// Computed properties (Lógica original intacta)
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

// Chart data (Con colores del tema Cyber Blue)
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
      '#10B981', // Emerald (Completed)
      '#06B6D4', // Cyan (Active)
      '#6366F1', // Indigo (Planned)
      '#EF4444'  // Red (Cancelled)
    ],
    borderWidth: 0
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
      '#3B82F6', // Blue 500
      '#E4E4E7'  // Zinc 200 (Gray)
    ],
    borderRadius: 5
  }]
}))

// Chart options
const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right',
      labels: {
        usePointStyle: true,
        padding: 20,
        color: document.documentElement.classList.contains('dark') ? '#fff' : '#333'
      }
    }
  }
}

const barChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      grid: {
         color: document.documentElement.classList.contains('dark') ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)'
      }
    },
    x: {
      grid: { display: false }
    }
  },
  plugins: {
    legend: { display: false }
  }
}

// Data Fetching Logic (EXACTAMENTE como en tu archivo funcional)
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
    currency: 'USD',
    maximumFractionDigits: 0
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

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>