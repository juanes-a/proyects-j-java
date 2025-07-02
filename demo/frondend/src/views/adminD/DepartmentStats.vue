<template>
  <div class="space-y-6">
    <!-- Error state -->
    <div v-if="error" class="bg-red-50 border-l-4 border-red-500 p-4 rounded-lg">
      <div class="flex">
        <div class="flex-shrink-0">
          <AlertTriangle class="h-5 w-5 text-red-500" />
        </div>
        <div class="ml-3">
          <p class="text-sm text-red-700">{{ error }}</p>
        </div>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600 inline-block"></div>
      <p class="mt-4 text-purple-600 font-medium">Loading department data...</p>
    </div>

    <!-- Content -->
    <div v-else>

      <!-- Key Metrics -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div
          v-for="metric in keyMetrics"
          :key="metric.title"
          class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border-l-4 hover:shadow-md transition-shadow cursor-pointer"
          :class="metric.borderColor"
          @click="filterProjects(metric.filter)"
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
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Budget Utilization</h3>
            <div class="flex items-center space-x-2">
              <select 
                v-model="budgetPeriod" 
                class="text-sm border border-gray-300 dark:border-gray-600 rounded-md px-2 py-1 bg-white dark:bg-gray-700 text-gray-800 dark:text-white hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors"
                @change="updateBudgetChart"
              >
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
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Project Status Distribution</h3>
            <div class="flex items-center space-x-2">
              <select 
                v-model="statusFilter" 
                class="text-sm border border-gray-300 dark:border-gray-600 rounded-md px-2 py-1 bg-white dark:bg-gray-700 text-gray-800 dark:text-white hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors"
                @change="filterByStatus"
              >
                <option value="all">All Statuses</option>
                <option value="PLANNED">Planned</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
          </div>
          <div class="h-80">
            <canvas ref="statusChart"></canvas>
          </div>
          <div class="mt-4 grid grid-cols-2 gap-2">
            <div 
              v-for="status in projectStatusStats" 
              :key="status.label" 
              class="flex items-center justify-between p-2 rounded-lg bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors cursor-pointer"
              @click="filterProjects(status.status)"
            >
              <div class="flex items-center space-x-2">
                <div :class="status.color" class="w-3 h-3 rounded-full"></div>
                <span class="text-sm text-gray-700 dark:text-gray-300">{{ status.label }}</span>
              </div>
              <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ status.count }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Performance & Timeline -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Performance Metrics -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Project Performance</h3>
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
                      :stroke-dasharray="`${performanceMetrics.completionRate}, 100`"
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
                    <span class="text-sm font-bold text-indigo-600 dark:text-indigo-400">{{ performanceMetrics.completionRate }}%</span>
                  </div>
                </div>
                <p class="text-sm font-medium text-gray-800 dark:text-white">Completion Rate</p>
              </div>
              <div class="text-center">
                <div class="w-16 h-16 mx-auto mb-3 relative">
                  <svg class="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                    <path
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      :stroke-dasharray="`${performanceMetrics.onTimeRate}, 100`"
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
                    <span class="text-sm font-bold text-green-600 dark:text-green-400">{{ performanceMetrics.onTimeRate }}%</span>
                  </div>
                </div>
                <p class="text-sm font-medium text-gray-800 dark:text-white">On-time Rate</p>
              </div>
            </div>

            <!-- Project Stats -->
            <div class="space-y-3">
              <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors">
                <div class="flex items-center space-x-2">
                  <Clock class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                  <span class="text-sm text-gray-700 dark:text-gray-300">Avg. Project Duration</span>
                </div>
                <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ performanceMetrics.avgDuration }} days</span>
              </div>
              <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors">
                <div class="flex items-center space-x-2">
                  <DollarSign class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                  <span class="text-sm text-gray-700 dark:text-gray-300">Avg. Budget Utilization</span>
                </div>
                <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ performanceMetrics.avgBudgetUtilization }}%</span>
              </div>
              <div class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors">
                <div class="flex items-center space-x-2">
                  <TrendingUp class="w-4 h-4 text-gray-600 dark:text-gray-400" />
                  <span class="text-sm text-gray-700 dark:text-gray-300">Active Projects</span>
                </div>
                <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ keyMetrics[1].value }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Project Timeline -->
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
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

      <!-- Priority Distribution -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Priority Distribution</h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div 
            v-for="priority in priorityStats" 
            :key="priority.label" 
            class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors cursor-pointer"
            @click="filterProjects(priority.filter)"
          >
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm font-medium text-gray-600 dark:text-gray-400">{{ priority.label }}</p>
                <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ priority.count }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ priority.percentage }}% of total</p>
              </div>
              <div :class="priority.color" class="w-10 h-10 rounded-full flex items-center justify-center">
                <component :is="priority.icon" class="w-5 h-5 text-white" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue'
import { BarChart3, FolderOpen, DollarSign, TrendingUp, Clock, AlertTriangle, Flag, ChevronUp, ChevronDown, Circle } from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import { useToastStore } from '../../stores/toast'
import { useAuthStore } from '../../stores/auth'
import api from '../../api/index'

const toastStore = useToastStore()
const authStore = useAuthStore()

// Estado inicial
const loading = ref(true)
const error = ref(null)
const budgetPeriod = ref('monthly')
const statusFilter = ref('all')
const filteredProjects = ref([])

// Chart refs
const budgetChart = ref(null)
const statusChart = ref(null)
const timelineChart = ref(null)
let budgetChartInstance = null
let statusChartInstance = null
let timelineChartInstance = null

// Data
const departmentInfo = ref({
  id: null,
  name: 'Department'
})

const projects = ref([])

const keyMetrics = ref([
  { 
    title: 'Total Projects', 
    value: '0', 
    subtitle: 'All projects', 
    icon: FolderOpen, 
    iconBg: 'bg-indigo-500', 
    borderColor: 'border-indigo-500',
    filter: 'all'
  },
  { 
    title: 'Active Projects', 
    value: '0', 
    subtitle: 'Currently running', 
    icon: TrendingUp, 
    iconBg: 'bg-blue-500', 
    borderColor: 'border-blue-500',
    filter: 'IN_PROGRESS'
  },
  { 
    title: 'Budget Utilization', 
    value: '0%', 
    subtitle: 'Of total budget', 
    icon: DollarSign, 
    iconBg: 'bg-green-500', 
    borderColor: 'border-green-500',
    filter: 'budget'
  },
  { 
    title: 'Overdue Projects', 
    value: '0', 
    subtitle: 'Past deadline', 
    icon: AlertTriangle, 
    iconBg: 'bg-red-500', 
    borderColor: 'border-red-500',
    filter: 'overdue'
  }
])

const budgetStats = ref({
  allocated: 0,
  used: 0,
  remaining: 0
})

const projectStatusStats = computed(() => {
  const total = filteredProjects.value.length
  return [
    { 
      label: 'In Progress', 
      count: filteredProjects.value.filter(p => p.status === 'IN_PROGRESS').length, 
      color: 'bg-blue-500',
      status: 'IN_PROGRESS',
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.status === 'IN_PROGRESS').length / total) * 100) : 0
    },
    { 
      label: 'Completed', 
      count: filteredProjects.value.filter(p => p.status === 'COMPLETED').length, 
      color: 'bg-green-500',
      status: 'COMPLETED',
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.status === 'COMPLETED').length / total) * 100) : 0
    },
    { 
      label: 'Planned', 
      count: filteredProjects.value.filter(p => p.status === 'PLANNED').length, 
      color: 'bg-gray-500',
      status: 'PLANNED',
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.status === 'PLANNED').length / total) * 100) : 0
    },
    { 
      label: 'Cancelled', 
      count: filteredProjects.value.filter(p => p.status === 'CANCELLED').length, 
      color: 'bg-red-500',
      status: 'CANCELLED',
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.status === 'CANCELLED').length / total) * 100) : 0
    }
  ]
})

const performanceMetrics = computed(() => {
  const completedProjects = filteredProjects.value.filter(p => p.status === 'COMPLETED')
  const inProgressProjects = filteredProjects.value.filter(p => p.status === 'IN_PROGRESS')
  const totalProjects = filteredProjects.value.length
  
  // Calculate average duration for completed projects
  const completedWithDuration = completedProjects.filter(p => p.startDate && p.endDate)
  const avgDuration = completedWithDuration.length > 0
    ? completedWithDuration.reduce((sum, p) => {
        const start = new Date(p.startDate)
        const end = new Date(p.endDate)
        return sum + (end - start)
      }, 0) / completedWithDuration.length
    : 0

  // Calculate budget utilization
  const totalBudget = filteredProjects.value.reduce((sum, p) => sum + (p.budget || 0), 0)
  const usedBudget = filteredProjects.value.reduce((sum, p) => sum + (p.actualCost || 0), 0)
  const avgBudgetUtilization = totalBudget > 0 ? Math.round((usedBudget / totalBudget) * 100) : 0

  // Calculate on-time rate (completed projects not overdue)
  const onTimeRate = completedProjects.length > 0
    ? Math.round((completedProjects.filter(p => !p.overdue).length / completedProjects.length) * 100)
    : 0

  return {
    completionRate: totalProjects > 0 ? Math.round((completedProjects.length / totalProjects) * 100) : 0,
    onTimeRate,
    avgDuration: Math.round(avgDuration / (1000 * 60 * 60 * 24)),
    avgBudgetUtilization
  }
})

const timelineStats = computed(() => {
  const now = new Date()
  const thisMonthStart = new Date(now.getFullYear(), now.getMonth(), 1)
  const thisMonthEnd = new Date(now.getFullYear(), now.getMonth() + 1, 0)

  return {
    thisMonth: filteredProjects.value.filter(p => {
      if (!p.startDate) return false
      const startDate = new Date(p.startDate)
      return startDate >= thisMonthStart && startDate <= thisMonthEnd
    }).length,
    upcomingDeadlines: filteredProjects.value.filter(p => {
      if (!p.endDate || p.status === 'COMPLETED' || p.status === 'CANCELLED') return false
      const endDate = new Date(p.endDate)
      const diffDays = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
      return diffDays > 0 && diffDays <= 14
    }).length,
    overdue: filteredProjects.value.filter(p => p.overdue).length
  }
})

const priorityStats = computed(() => {
  const total = filteredProjects.value.length
  return [
    {
      label: 'High Priority',
      count: filteredProjects.value.filter(p => p.priority === 'HIGH').length,
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.priority === 'HIGH').length / total) * 100) : 0,
      color: 'bg-red-500',
      icon: ChevronUp,
      filter: 'HIGH'
    },
    {
      label: 'Medium Priority',
      count: filteredProjects.value.filter(p => p.priority === 'MEDIUM').length,
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.priority === 'MEDIUM').length / total) * 100) : 0,
      color: 'bg-yellow-500',
      icon: Circle,
      filter: 'MEDIUM'
    },
    {
      label: 'Low Priority',
      count: filteredProjects.value.filter(p => p.priority === 'LOW').length,
      percentage: total > 0 ? Math.round((filteredProjects.value.filter(p => p.priority === 'LOW').length / total) * 100) : 0,
      color: 'bg-green-500',
      icon: ChevronDown,
      filter: 'LOW'
    }
  ]
})

const fetchDepartment = async () => {
  try {
    error.value = null
    loading.value = true
    const userEmail = authStore.user?.email || localStorage.getItem('userEmail')
    
    if (!userEmail) {
      throw new Error('User email not available')
    }

    const response = await api.get(`/projects/user/${userEmail}`)

    if (!response.data) {
      throw new Error('Invalid response format')
    }

    departmentInfo.value = {
      id: response.data.departmentId,
      name: response.data.departmentName || 'Department'
    }
    
    projects.value = Array.isArray(response.data.projects) 
      ? response.data.projects.map(p => ({
          id: p.id,
          name: p.name,
          status: p.status,
          budget: p.budget ? Number(p.budget) : 0,
          actualCost: p.actualCost ? Number(p.actualCost) : 0,
          startDate: p.startDate,
          endDate: p.endDate,
          priority: p.priority,
          overdue: p.overdue || false,
          progress: p.progress || 0
        }))
      : []
    
    filteredProjects.value = [...projects.value]
    updateQuickStats()
    
  } catch (err) {
    console.error('[ERROR] Fetch error:', err)
    const errorMsg = err.response?.data?.message || err.message || 'Failed to load projects'
    toastStore.showToast(errorMsg, 'error')
    projects.value = []
    filteredProjects.value = []
    departmentInfo.value = { id: null, name: 'Department' }
  } finally {
    loading.value = false
  }
}

const updateQuickStats = () => {
  // Update key metrics
  keyMetrics.value[0].value = filteredProjects.value.length.toString()
  keyMetrics.value[1].value = filteredProjects.value.filter(p => p.status === 'IN_PROGRESS').length.toString()
  
  const totalBudget = filteredProjects.value.reduce((sum, p) => sum + (p.budget || 0), 0)
  const usedBudget = filteredProjects.value.reduce((sum, p) => sum + (p.actualCost || 0), 0)
  const utilizationPercentage = totalBudget > 0 ? Math.round((usedBudget / totalBudget) * 100) : 0
  
  keyMetrics.value[2].value = `${utilizationPercentage}%`
  keyMetrics.value[3].value = filteredProjects.value.filter(p => p.overdue).length.toString()

  // Update budget stats
  budgetStats.value = {
    allocated: totalBudget,
    used: usedBudget,
    remaining: totalBudget - usedBudget
  }

  // Create/update charts
  nextTick(() => {
    createCharts()
  })
}

const filterProjects = (filter) => {
  if (filter === 'all') {
    filteredProjects.value = [...projects.value]
  } else if (filter === 'overdue') {
    filteredProjects.value = projects.value.filter(p => p.overdue)
  } else if (filter === 'budget') {
    // Sort by budget utilization (highest first)
    filteredProjects.value = [...projects.value].sort((a, b) => {
      const aUtilization = a.budget > 0 ? (a.actualCost || 0) / a.budget : 0
      const bUtilization = b.budget > 0 ? (b.actualCost || 0) / b.budget : 0
      return bUtilization - aUtilization
    })
  } else if (['HIGH', 'MEDIUM', 'LOW'].includes(filter)) {
    filteredProjects.value = projects.value.filter(p => p.priority === filter)
  } else {
    filteredProjects.value = projects.value.filter(p => p.status === filter)
  }
  updateQuickStats()
}

const filterByStatus = () => {
  if (statusFilter.value === 'all') {
    filteredProjects.value = [...projects.value]
  } else {
    filteredProjects.value = projects.value.filter(p => p.status === statusFilter.value)
  }
  updateQuickStats()
}

const createCharts = () => {
  createBudgetChart()
  createStatusChart()
  createTimelineChart()
}

const updateBudgetChart = () => {
  if (budgetChartInstance) {
    budgetChartInstance.destroy()
  }
  createBudgetChart()
}

const createBudgetChart = () => {
  const ctx = budgetChart.value?.getContext('2d')
  if (!ctx) return
  
  // Prepare data based on selected period
  let labels = ['Allocated', 'Used', 'Remaining']
  let data = [budgetStats.value.allocated, budgetStats.value.used, budgetStats.value.remaining]
  
  if (budgetPeriod.value !== 'monthly') {
    // For quarterly or yearly, group projects by period
    const now = new Date()
    const periods = budgetPeriod.value === 'quarterly' ? 4 : 12
    labels = []
    const allocatedData = []
    const usedData = []
    
    for (let i = periods - 1; i >= 0; i--) {
      const periodStart = new Date(now.getFullYear(), now.getMonth() - i, 1)
      const periodEnd = new Date(now.getFullYear(), now.getMonth() - i + 1, 0)
      
      const periodProjects = filteredProjects.value.filter(p => {
        const startDate = p.startDate ? new Date(p.startDate) : null
        return startDate && startDate >= periodStart && startDate <= periodEnd
      })
      
      const periodAllocated = periodProjects.reduce((sum, p) => sum + (p.budget || 0), 0)
      const periodUsed = periodProjects.reduce((sum, p) => sum + (p.actualCost || 0), 0)
      
      labels.push(periodStart.toLocaleDateString('default', { month: 'short' }))
      allocatedData.push(periodAllocated)
      usedData.push(periodUsed)
    }
    
    budgetChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Allocated',
            data: allocatedData,
            backgroundColor: '#e5e7eb',
            borderColor: '#d1d5db',
            borderWidth: 2
          },
          {
            label: 'Used',
            data: usedData,
            backgroundColor: '#8b5cf6',
            borderColor: '#7c3aed',
            borderWidth: 2
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
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
    return
  }
  
  // Default monthly view (total budget)
  budgetChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Budget',
        data: data,
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
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || ''
              const value = context.raw || 0
              const total = context.dataset.data.reduce((a, b) => a + b, 0)
              const percentage = Math.round((value / total) * 100)
              return `${label}: ${value} (${percentage}%)`
            }
          }
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
  
  // Generate timeline data for the last 6 months
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  const now = new Date()
  const monthLabels = []
  const projectData = []
  const completedData = []
  
  for (let i = 5; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    monthLabels.push(months[date.getMonth()] + ' ' + date.getFullYear().toString().slice(2))
    
    const monthStart = new Date(date.getFullYear(), date.getMonth(), 1)
    const monthEnd = new Date(date.getFullYear(), date.getMonth() + 1, 0)
    
    projectData.push(
      filteredProjects.value.filter(p => {
        const startDate = p.startDate ? new Date(p.startDate) : null
        return startDate && startDate >= monthStart && startDate <= monthEnd
      }).length
    )
    
    completedData.push(
      filteredProjects.value.filter(p => {
        const endDate = p.endDate ? new Date(p.endDate) : null
        return p.status === 'COMPLETED' && endDate && endDate >= monthStart && endDate <= monthEnd
      }).length
    )
  }
  
  timelineChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels: monthLabels,
      datasets: [
        {
          label: 'Projects Started',
          data: projectData,
          borderColor: '#8b5cf6',
          backgroundColor: 'rgba(139, 92, 246, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4
        },
        {
          label: 'Projects Completed',
          data: completedData,
          borderColor: '#10b981',
          backgroundColor: 'rgba(16, 185, 129, 0.1)',
          borderWidth: 3,
          fill: true,
          tension: 0.4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
        },
        tooltip: {
          mode: 'index',
          intersect: false
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0
          }
        }
      },
      interaction: {
        mode: 'nearest',
        axis: 'x',
        intersect: false
      }
    }
  })
}

// Format functions
const formatCurrency = (amount) => {
  if (amount === undefined || amount === null) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(amount)
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// Initialize
onMounted(() => {
  fetchDepartment()
})

// Watch for department ID changes to load additional stats
watch(() => departmentInfo.value.id, (newId) => {
  if (newId) {
    // You can add additional data fetching here if needed
  }
})
</script>