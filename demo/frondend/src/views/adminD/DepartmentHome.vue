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
                <h1 class="text-2xl font-bold">{{ departmentInfo.name }} Department</h1>
                <p class="text-indigo-100">Department Administration Dashboard</p>
              </div>
            </div>
            <p class="text-indigo-100 mt-2">
              Welcome back! Here's your department overview for today.
            </p>
          </div>
          <div class="hidden md:block">
            <div class="w-20 h-20 bg-white/10 rounded-full flex items-center justify-center">
              <Users class="w-10 h-10 text-white/80" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Department Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <DepartmentStatsCard
        v-for="stat in departmentStats"
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
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Budget Overview</h3>
          <div class="flex items-center space-x-2">
            <div class="w-3 h-3 bg-indigo-500 rounded-full"></div>
            <span class="text-sm text-gray-600 dark:text-gray-400">Used</span>
            <div class="w-3 h-3 bg-gray-300 dark:bg-gray-600 rounded-full"></div>
            <span class="text-sm text-gray-600 dark:text-gray-400">Available</span>
          </div>
        </div>
        <div class="h-80">
          <canvas ref="budgetChart"></canvas>
        </div>
        <div class="mt-4 grid grid-cols-2 gap-4">
          <div class="text-center">
            <p class="text-sm text-gray-600 dark:text-gray-400">Budget Used</p>
            <p class="text-xl font-bold text-indigo-600 dark:text-indigo-400">
              {{ formatCurrency(budgetData.used) }}
            </p>
          </div>
          <div class="text-center">
            <p class="text-sm text-gray-600 dark:text-gray-400">Budget Available</p>
            <p class="text-xl font-bold text-gray-600 dark:text-gray-400">
              {{ formatCurrency(budgetData.available) }}
            </p>
          </div>
        </div>
      </div>

      <!-- Projects Status Chart -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Projects by Status</h3>
          <FolderOpen class="w-5 h-5 text-gray-500 dark:text-gray-400" />
        </div>
        <div class="h-80">
          <canvas ref="projectsChart"></canvas>
        </div>
        <div class="mt-4 grid grid-cols-2 gap-2">
          <div v-for="status in projectStatusData" :key="status.label" class="flex items-center space-x-2">
            <div :class="status.color" class="w-3 h-3 rounded-full"></div>
            <span class="text-sm text-gray-600 dark:text-gray-400">{{ status.label }}: {{ status.count }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity & Quick Actions -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Recent Projects Activity -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Recent Project Activity</h3>
        <div class="space-y-4">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="flex items-start space-x-3 p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
          >
            <div :class="activity.iconBg" class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0">
              <component :is="activity.icon" class="w-4 h-4 text-white" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-800 dark:text-white">{{ activity.title }}</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">{{ activity.time }}</p>
            </div>
          </div>
        </div>
        <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
          <router-link
            to="/department/projects"
            class="text-sm text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-300 font-medium"
          >
            View All Projects →
          </router-link>
        </div>
      </div>

      <!-- Department Team Overview -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Team Overview</h3>
        <div class="space-y-4">
          <!-- Team Stats -->
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-indigo-50 dark:bg-indigo-900/20 rounded-lg p-4">
              <div class="flex items-center space-x-2">
                <Users class="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                <div>
                  <p class="text-sm text-indigo-600 dark:text-indigo-400">Active Members</p>
                  <p class="text-xl font-bold text-indigo-800 dark:text-indigo-300">{{ departmentInfo.activeMembers || 0 }}</p>
                </div>
              </div>
            </div>
            <div class="bg-green-50 dark:bg-green-900/20 rounded-lg p-4">
              <div class="flex items-center space-x-2">
                <TrendingUp class="w-5 h-5 text-green-600 dark:text-green-400" />
                <div>
                  <p class="text-sm text-green-600 dark:text-green-400">Productivity</p>
                  <p class="text-xl font-bold text-green-800 dark:text-green-300">{{ departmentInfo.productivity || 'N/A' }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="space-y-2">
            <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300">Quick Actions</h4>
            <div class="grid grid-cols-1 gap-2">
              <button
                @click="$router.push('/department/projects?action=create')"
                class="flex items-center space-x-2 p-3 text-left rounded-lg border-2 border-dashed border-gray-200 dark:border-gray-600 hover:border-indigo-500 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 transition-all duration-200 group"
              >
                <div class="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                  <Plus class="w-4 h-4 text-white" />
                </div>
                <div>
                  <p class="font-medium text-gray-800 dark:text-white">New Project</p>
                  <p class="text-sm text-gray-500 dark:text-gray-400">Create a new department project</p>
                </div>
              </button>
              <button
                @click="$router.push('/department/stats')"
                class="flex items-center space-x-2 p-3 text-left rounded-lg border-2 border-dashed border-gray-200 dark:border-gray-600 hover:border-purple-500 hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-all duration-200 group"
              >
                <div class="w-8 h-8 bg-purple-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
                  <BarChart3 class="w-4 h-4 text-white" />
                </div>
                <div>
                  <p class="font-medium text-gray-800 dark:text-white">View Statistics</p>
                  <p class="text-sm text-gray-500 dark:text-gray-400">Detailed department analytics</p>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Department Performance Indicators -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover">
      <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Department Performance</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Project Completion Rate -->
        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-dasharray="75, 100"
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
              <span class="text-lg font-bold text-indigo-600 dark:text-indigo-400">75%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">Completion Rate</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Projects completed on time</p>
        </div>

        <!-- Budget Efficiency -->
        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-dasharray="85, 100"
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
              <span class="text-lg font-bold text-green-600 dark:text-green-400">85%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">Budget Efficiency</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Optimal budget utilization</p>
        </div>

        <!-- Team Productivity -->
        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-dasharray="92, 100"
                class="text-purple-500"
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
              <span class="text-lg font-bold text-purple-600 dark:text-purple-400">92%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">Team Productivity</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Overall team performance</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Building2, Users, TrendingUp, FolderOpen, DollarSign, CheckCircle, Plus, BarChart3, Edit, Play } from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import axios from 'axios'
import DepartmentStatsCard from '../../components/departmentsDept/DepartmentStatsCard.vue'
import { useToastStore } from '../../stores/toast'

const toastStore = useToastStore()

const loading = ref(true)
const budgetChart = ref(null)
const projectsChart = ref(null)
let budgetChartInstance = null
let projectsChartInstance = null

// Department specific data
const departmentInfo = ref({
  id: 1, // This would come from auth/context
  name: 'IT Department',
  activeMembers: 0,
  productivity: 'High'
})

const departmentStats = ref([
  { title: 'Active Projects', value: '0', change: '+2 this month', icon: FolderOpen, color: 'indigo' },
  { title: 'Total Budget', value: '$0', change: '85% utilized', icon: DollarSign, color: 'green' },
  { title: 'Team Members', value: '0', change: '+1 new member', icon: Users, color: 'purple' },
  { title: 'Completed Projects', value: '0', change: '+3 this quarter', icon: CheckCircle, color: 'blue' }
])

const budgetData = ref({
  used: 0,
  available: 0,
  total: 0
})

const projectStatusData = ref([
  { label: 'In Progress', count: 0, color: 'bg-blue-500' },
  { label: 'Completed', count: 0, color: 'bg-green-500' },
  { label: 'Planned', count: 0, color: 'bg-gray-500' },
  { label: 'Cancelled', count: 0, color: 'bg-red-500' }
])

const recentActivities = ref([
  { id: 1, title: 'Project "Mobile App" started', time: '2 hours ago', icon: Play, iconBg: 'bg-blue-500' },
  { id: 2, title: 'Budget updated for "Web Portal"', time: '4 hours ago', icon: Edit, iconBg: 'bg-green-500' },
  { id: 3, title: 'New team member assigned', time: '6 hours ago', icon: Users, iconBg: 'bg-purple-500' },
  { id: 4, title: 'Project "API Integration" completed', time: '1 day ago', icon: CheckCircle, iconBg: 'bg-indigo-500' }
])

const fetchDepartmentData = async () => {
  try {
    loading.value = true
    
    // Fetch department statistics
    const statsResponse = await axios.get(`/api/departments/${departmentInfo.value.id}/stats`)
    const stats = statsResponse.data
    
    // Update stats cards
    departmentStats.value[0].value = stats.activeProjects?.toString() || '0'
    departmentStats.value[1].value = formatCurrency(stats.totalBudget || 0)
    departmentStats.value[2].value = stats.teamMembers?.toString() || '0'
    departmentStats.value[3].value = stats.completedProjects?.toString() || '0'
    
    // Update budget data
    budgetData.value = {
      used: stats.budgetUsed || 0,
      available: (stats.totalBudget || 0) - (stats.budgetUsed || 0),
      total: stats.totalBudget || 0
    }
    
    // Update project status data
    projectStatusData.value = [
      { label: 'In Progress', count: stats.inProgressProjects || 0, color: 'bg-blue-500' },
      { label: 'Completed', count: stats.completedProjects || 0, color: 'bg-green-500' },
      { label: 'Planned', count: stats.plannedProjects || 0, color: 'bg-gray-500' },
      { label: 'Cancelled', count: stats.cancelledProjects || 0, color: 'bg-red-500' }
    ]
    
    // Update department info
    departmentInfo.value.activeMembers = stats.teamMembers || 0
    
    await nextTick()
    createCharts()
    
  } catch (error) {
    console.error('Error fetching department data:', error)
    toastStore.showToast('Error loading department data', 'error')
  } finally {
    loading.value = false
  }
}

const createCharts = () => {
  createBudgetChart()
  createProjectsChart()
}

const createBudgetChart = () => {
  if (budgetChartInstance) {
    budgetChartInstance.destroy()
  }
  
  const ctx = budgetChart.value?.getContext('2d')
  if (!ctx) return
  
  budgetChartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Budget Used', 'Budget Available'],
      datasets: [{
        data: [budgetData.value.used, budgetData.value.available],
        backgroundColor: ['#6366f1', '#e5e7eb'],
        borderColor: ['#4f46e5', '#d1d5db'],
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
      cutout: '70%'
    }
  })
}

const createProjectsChart = () => {
  if (projectsChartInstance) {
    projectsChartInstance.destroy()
  }
  
  const ctx = projectsChart.value?.getContext('2d')
  if (!ctx) return
  
  projectsChartInstance = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: projectStatusData.value.map(item => item.label),
      datasets: [{
        data: projectStatusData.value.map(item => item.count),
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
      }
    }
  })
}

const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

onMounted(() => {
  fetchDepartmentData()
})
</script>
