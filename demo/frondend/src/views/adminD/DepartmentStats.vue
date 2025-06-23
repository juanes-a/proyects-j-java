<template>
  <div class="space-y-6">
    <!-- Department Header -->
    <div class="bg-gradient-to-r from-purple-600 to-indigo-700 rounded-xl p-6 text-white">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold">{{ departmentInfo.name }} Statistics</h1>
          <p class="text-purple-100 mt-1">Detailed analytics and performance metrics</p>
        </div>
        <div class="hidden md:block">
          <div class="bg-white/10 rounded-lg p-3">
            <BarChart3 class="w-8 h-8 text-white/80" />
          </div>
        </div>
      </div>
    </div>

    <!-- Key Metrics -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="metric in keyMetrics"
        :key="metric.title"
        class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border-l-4"
        :class="metric.borderColor"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600 dark:text-gray-400">{{ metric.title }}</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ metric.value }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ metric.subtitle }}</p>
          </div>
          <div :class="metric.iconBg" class="w-12 h-12 rounded-lg flex items-center justify-center">
            <component :is="metric.icon" class="w-6 h-6 text-white" />
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Budget Utilization Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Budget Utilization</h3>
          <div class="flex items-center space-x-2">
            <select v-model="budgetPeriod" class="text-sm border border-gray-300 dark:border-gray-600 rounded-md px-2 py-1 bg-white dark:bg-gray-700 text-gray-800 dark:text-white">
              <option value="monthly">Monthly</option>
              <option value="quarterly">Quarterly</option>
              <option value="yearly">Yearly</option>
            </select>
          </div>
        </div>
        <div class="h-80">
          <canvas ref="budgetChart"></canvas>
        </div>
        <div class="mt-4 grid grid-cols-3 gap-4 text-center">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Allocated</p>
            <p class="text-lg font-semibold text-gray-900 dark:text-white">{{ formatCurrency(budgetStats.allocated) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Used</p>
            <p class="text-lg font-semibold text-purple-600 dark:text-purple-400">{{ formatCurrency(budgetStats.used) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">Remaining</p>
            <p class="text-lg font-semibold text-green-600 dark:text-green-400">{{ formatCurrency(budgetStats.remaining) }}</p>
          </div>
        </div>
      </div>

      <!-- Project Status Distribution -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Project Status Distribution</h3>
          <FolderOpen class="w-5 h-5 text-gray-500 dark:text-gray-400" />
        </div>
        <div class="h-80">
          <canvas ref="statusChart"></canvas>
        </div>
        <div class="mt-4 grid grid-cols-2 gap-2">
          <div v-for="status in projectStatusStats" :key="status.label" class="flex items-center justify-between p-2 rounded-lg bg-gray-50 dark:bg-gray-700">
            <div class="flex items-center space-x-2">
              <div :class="status.color" class="w-3 h-3 rounded-full"></div>
              <span class="text-sm text-gray-700 dark:text-gray-300">{{ status.label }}</span>
            </div>
            <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ status.count }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Team Performance & Project Timeline -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Team Performance -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Team Performance</h3>
        <div class="space-y-6">
          <!-- Performance Metrics -->
          <div class="grid grid-cols-2 gap-4">
            <div class="text-center">
              <div class="w-16 h-16 mx-auto mb-3 relative">
                <svg class="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                  <path
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    :stroke-dasharray="`${teamPerformance.productivity}, 100`"
                    class="text-indigo-500"
                  />
                  <path
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-dasharray="0, 100"
                    class="text-gray-200 dark:text-gray-700"
                  />
                </svg>
                <div class="absolute inset-0 flex items-center justify-center">
                  <span class="text-sm font-bold text-indigo-600 dark:text-indigo-400">{{ teamPerformance.productivity }}%</span>
                </div>
              </div>
              <p class="text-sm font-medium text-gray-800 dark:text-white">Productivity</p>
            </div>
            <div class="text-center">
              <div class="w-16 h-16 mx-auto mb-3 relative">
                <svg class="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                  <path
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    :stroke-dasharray="`${teamPerformance.efficiency}, 100`"
                    class="text-green-500"
                  />
                  <path
                    d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-dasharray="0, 100"
                    class="text-gray-200 dark:text-gray-700"
                  />
                </svg>
                <div class="absolute inset-0 flex items-center justify-center">
                  <span class="text-sm font-bold text-green-600 dark:text-green-400">{{ teamPerformance.efficiency }}%</span>
                </div>
              </div>
              <p class="text-sm font-medium text-gray-800 dark:text-white">Efficiency</p>
            </div>
          </div>

          <!-- Team Stats -->
          <div class="space-y-3">
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
              <div class="flex items-center space-x-2">
                <Users class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                <span class="text-sm text-gray-700 dark:text-gray-300">Active Members</span>
              </div>
              <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ teamStats.activeMembers }}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
              <div class="flex items-center space-x-2">
                <Clock class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                <span class="text-sm text-gray-700 dark:text-gray-300">Avg. Project Duration</span>
              </div>
              <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ teamStats.avgProjectDuration }} days</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
              <div class="flex items-center space-x-2">
                <Target class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                <span class="text-sm text-gray-700 dark:text-gray-300">On-time Delivery</span>
              </div>
              <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ teamStats.onTimeDelivery }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Project Timeline -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Project Timeline</h3>
        <div class="h-80">
          <canvas ref="timelineChart"></canvas>
        </div>
        <div class="mt-4 space-y-2">
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-400">Projects this month</span>
            <span class="font-semibold text-gray-900 dark:text-white">{{ timelineStats.thisMonth }}</span>
          </div>
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-400">Upcoming deadlines</span>
            <span class="font-semibold text-orange-600 dark:text-orange-400">{{ timelineStats.upcomingDeadlines }}</span>
          </div>
          <div class="flex items-center justify-between text-sm">
            <span class="text-gray-600 dark:text-gray-400">Overdue projects</span>
            <span class="font-semibold text-red-600 dark:text-red-400">{{ timelineStats.overdue }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Detailed Statistics Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm">
      <div class="p-6 border-b border-gray-200 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Project Details</h3>
        <p class="text-sm text-gray-600 dark:text-gray-400">Comprehensive view of all department projects</p>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Project Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Budget Allocated
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Budget Used
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Progress
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Team Members
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Deadline
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="project in detailedProjects"
              :key="project.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
            >
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-white">{{ project.name }}</div>
                <div class="text-sm text-gray-500 dark:text-gray-400">{{ project.priority }} Priority</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(project.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ formatStatus(project.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ formatCurrency(project.budget) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ formatCurrency(project.budgetUsed || 0) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2 mr-2">
                    <div 
                      :class="getProgressBarClass(project.status)"
                      class="h-2 rounded-full transition-all duration-300"
                      :style="{ width: `${getProgressPercentage(project)}%` }"
                    ></div>
                  </div>
                  <span class="text-xs text-gray-600 dark:text-gray-400">{{ getProgressPercentage(project) }}%</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ project.teamMembers || 0 }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-white">{{ formatDate(project.endDate) }}</div>
                <div class="text-xs" :class="getDeadlineClass(project.endDate)">
                  {{ getDeadlineStatus(project.endDate) }}
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { BarChart3, FolderOpen, Users, DollarSign, TrendingUp, CheckCircle, Clock, Target, AlertTriangle } from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import axios from 'axios'
import { useToastStore } from '../../stores/toast'

const toastStore = useToastStore()

// Department info (would come from auth/context)
const departmentInfo = ref({
  id: 1,
  name: 'IT Department'
})

const loading = ref(true)
const budgetPeriod = ref('monthly')

// Chart refs
const budgetChart = ref(null)
const statusChart = ref(null)
const timelineChart = ref(null)
let budgetChartInstance = null
let statusChartInstance = null
let timelineChartInstance = null

// Data
const keyMetrics = ref([
  { title: 'Total Projects', value: '0', subtitle: 'All time', icon: FolderOpen, iconBg: 'bg-indigo-500', borderColor: 'border-indigo-500' },
  { title: 'Active Projects', value: '0', subtitle: 'Currently running', icon: TrendingUp, iconBg: 'bg-blue-500', borderColor: 'border-blue-500' },
  { title: 'Budget Utilization', value: '0%', subtitle: 'Of total budget', icon: DollarSign, iconBg: 'bg-green-500', borderColor: 'border-green-500' },
  { title: 'Team Members', value: '0', subtitle: 'Active contributors', icon: Users, iconBg: 'bg-purple-500', borderColor: 'border-purple-500' }
])

const budgetStats = ref({
  allocated: 0,
  used: 0,
  remaining: 0
})

const projectStatusStats = ref([
  { label: 'In Progress', count: 0, color: 'bg-blue-500' },
  { label: 'Completed', count: 0, color: 'bg-green-500' },
  { label: 'Planned', count: 0, color: 'bg-gray-500' },
  { label: 'Cancelled', count: 0, color: 'bg-red-500' }
])

const teamPerformance = ref({
  productivity: 85,
  efficiency: 92
})

const teamStats = ref({
  activeMembers: 0,
  avgProjectDuration: 0,
  onTimeDelivery: 0
})

const timelineStats = ref({
  thisMonth: 0,
  upcomingDeadlines: 0,
  overdue: 0
})

const detailedProjects = ref([])

const fetchDepartmentStats = async () => {
  try {
    loading.value = true
    
    // Fetch department statistics
    const statsResponse = await axios.get(`/api/departments/${departmentInfo.value.id}/stats`)
    const stats = statsResponse.data
    
    // Update key metrics
    keyMetrics.value[0].value = stats.totalProjects?.toString() || '0'
    keyMetrics.value[1].value = stats.activeProjects?.toString() || '0'
    keyMetrics.value[2].value = `${Math.round((stats.budgetUsed / stats.totalBudget) * 100) || 0}%`
    keyMetrics.value[3].value = stats.teamMembers?.toString() || '0'
    
    // Update budget stats
    budgetStats.value = {
      allocated: stats.totalBudget || 0,
      used: stats.budgetUsed || 0,
      remaining: (stats.totalBudget || 0) - (stats.budgetUsed || 0)
    }
    
    // Update project status stats
    projectStatusStats.value = [
      { label: 'In Progress', count: stats.inProgressProjects || 0, color: 'bg-blue-500' },
      { label: 'Completed', count: stats.completedProjects || 0, color: 'bg-green-500' },
      { label: 'Planned', count: stats.plannedProjects || 0, color: 'bg-gray-500' },
      { label: 'Cancelled', count: stats.cancelledProjects || 0, color: 'bg-red-500' }
    ]
    
    // Update team stats
    teamStats.value = {
      activeMembers: stats.teamMembers || 0,
      avgProjectDuration: stats.avgProjectDuration || 0,
      onTimeDelivery: stats.onTimeDelivery || 0
    }
    
    // Update timeline stats
    timelineStats.value = {
      thisMonth: stats.projectsThisMonth || 0,
      upcomingDeadlines: stats.upcomingDeadlines || 0,
      overdue: stats.overdueProjects || 0
    }
    
    // Fetch detailed projects
    const projectsResponse = await axios.get(`/api/projects/department/${departmentInfo.value.id}`)
    detailedProjects.value = projectsResponse.data
    
    await nextTick()
    createCharts()
    
  } catch (error) {
    console.error('Error fetching department statistics:', error)
    toastStore.showToast('Error loading statistics', 'error')
  } finally {
    loading.value = false
  }
}

const createCharts = () => {
  createBudgetChart()
  createStatusChart()
  createTimelineChart()
}

const createBudgetChart = () => {
  if (budgetChartInstance) {
    budgetChartInstance.destroy()
  }
  
  const ctx = budgetChart.value?.getContext('2d')
  if (!ctx) return
  
  budgetChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Allocated', 'Used', 'Remaining'],
      datasets: [{
        label: 'Budget',
        data: [budgetStats.value.allocated, budgetStats.value.used, budgetStats.value.remaining],
        backgroundColor: ['#e5e7eb', '#8b5cf6', '#10b981'],
        borderColor: ['#d1d5db', '#7c3aed', '#059669'],
        borderWidth: 2
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
          ticks: {
            callback: function(value) {
              return '$' + value.toLocaleString()
            }
          }
        }
      }
    }
  })
}

const createStatusChart = () => {
  if (statusChartInstance) {
    statusChartInstance.destroy()
  }
  
  const ctx = statusChart.value?.getContext('2d')
  if (!ctx) return
  
  statusChartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: projectStatusStats.value.map(item => item.label),
      datasets: [{
        data: projectStatusStats.value.map(item => item.count),
        backgroundColor: ['#3b82f6', '#10b981', '#6b7280', '#ef4444'],
        borderColor: ['#2563eb', '#059669', '#4b5563', '#dc2626'],
        borderWidth: 2
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
      cutout: '60%'
    }
  })
}

const createTimelineChart = () => {
  if (timelineChartInstance) {
    timelineChartInstance.destroy()
  }
  
  const ctx = timelineChart.value?.getContext('2d')
  if (!ctx) return
  
  // Generate sample timeline data
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']
  const projectData = [2, 4, 3, 5, 6, 4]
  
  timelineChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: months,
      datasets: [{
        label: 'Projects',
        data: projectData,
        borderColor: '#8b5cf6',
        backgroundColor: 'rgba(139, 92, 246, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4
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
          beginAtZero: true
        }
      }
    }
  })
}

// Format functions
const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

const formatStatus = (status) => {
  const statusMap = {
    'PLANNED': 'Planned',
    'IN_PROGRESS': 'In Progress',
    'COMPLETED': 'Completed',
    'CANCELLED': 'Cancelled'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classes = {
    'PLANNED': 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
    'IN_PROGRESS': 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
    'COMPLETED': 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    'CANCELLED': 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
  }
  return classes[status] || classes.PLANNED
}

const getProgressPercentage = (project) => {
  if (!project.startDate || !project.endDate) return 0
  
  const start = new Date(project.startDate)
  const end = new Date(project.endDate)
  const today = new Date()
  
  if (project.status === 'COMPLETED') return 100
  if (project.status === 'CANCELLED') return 0
  if (today < start) return 0
  if (today > end) return 100
  
  const totalDuration = end - start
  const elapsed = today - start
  return Math.round((elapsed / totalDuration) * 100)
}

const getProgressBarClass = (status) => {
  const classes = {
    'PLANNED': 'bg-gray-400',
    'IN_PROGRESS': 'bg-blue-500',
    'COMPLETED': 'bg-green-500',
    'CANCELLED': 'bg-red-500'
  }
  return classes[status] || 'bg-gray-400'
}

const getDeadlineStatus = (endDate) => {
  if (!endDate) return 'No deadline'
  
  const end = new Date(endDate)
  const today = new Date()
  const diffTime = end - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'Overdue'
  if (diffDays === 0) return 'Due today'
  if (diffDays <= 7) return `${diffDays} days left`
  return 'On track'
}

const getDeadlineClass = (endDate) => {
  if (!endDate) return 'text-gray-500 dark:text-gray-400'
  
  const end = new Date(endDate)
  const today = new Date()
  const diffTime = end - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'text-red-600 dark:text-red-400'
  if (diffDays <= 7) return 'text-orange-600 dark:text-orange-400'
  return 'text-green-600 dark:text-green-400'
}

onMounted(() => {
  fetchDepartmentStats()
})
</script>
