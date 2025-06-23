<template>
  <div class="space-y-6">
    <!-- Department Header -->
    <div class="bg-gradient-to-r from-indigo-500 to-purple-600 rounded-xl p-6 text-white">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold">{{ departmentInfo.name }} Projects</h1>
          <p class="text-indigo-100 mt-1">Manage and track your department's projects</p>
        </div>
        <div class="hidden md:block">
          <div class="bg-white/10 rounded-lg p-3">
            <FolderOpen class="w-8 h-8 text-white/80" />
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div
        v-for="stat in quickStats"
        :key="stat.title"
        class="bg-white dark:bg-gray-800 rounded-xl p-4 shadow-sm border-l-4"
        :class="stat.borderColor"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">{{ stat.title }}</p>
            <p class="text-xl font-bold text-gray-800 dark:text-white">{{ stat.value }}</p>
          </div>
          <component :is="stat.icon" :class="stat.iconColor" class="w-6 h-6" />
        </div>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Filter Projects</h3>
        <button
          @click="clearFilters"
          class="text-sm text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-300"
        >
          Clear Filters
        </button>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- Search -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Search Projects</label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              v-model="filters.search"
              type="text"
              placeholder="Search by name..."
              class="pl-10 w-full form-input"
            />
          </div>
        </div>

        <!-- Status Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
          <select v-model="filters.status" class="form-input">
            <option value="">All Status</option>
            <option value="PLANNED">Planned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>

        <!-- Priority Filter -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Priority</label>
          <select v-model="filters.priority" class="form-input">
            <option value="">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </div>

        <!-- Date Range -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Date Range</label>
          <select v-model="filters.dateRange" class="form-input">
            <option value="">All Dates</option>
            <option value="this_month">This Month</option>
            <option value="next_month">Next Month</option>
            <option value="this_quarter">This Quarter</option>
            <option value="overdue">Overdue</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Projects Table -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm">
      <!-- Table Header -->
      <div class="p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          <div>
            <h2 class="text-xl font-semibold text-gray-800 dark:text-white">Department Projects</h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">{{ filteredProjects.length }} projects found</p>
          </div>
          
          <button
            @click="openCreateModal"
            class="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors duration-200"
          >
            <Plus class="w-4 h-4" />
            <span>New Project</span>
          </button>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Project
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Priority
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Budget
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Timeline
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Progress
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="project in filteredProjects"
              :key="project.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
            >
              <td class="px-6 py-4">
                <div class="flex items-center">
                  <div class="w-10 h-10 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg flex items-center justify-center">
                    <FolderOpen class="w-5 h-5 text-white" />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900 dark:text-white">{{ project.name }}</div>
                    <div class="text-sm text-gray-500 dark:text-gray-400 truncate max-w-xs">
                      {{ project.description || 'No description' }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(project.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ formatStatus(project.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <component :is="getPriorityIcon(project.priority)" :class="getPriorityColor(project.priority)" class="w-4 h-4 mr-2" />
                  <span class="text-sm text-gray-900 dark:text-white">{{ formatPriority(project.priority) }}</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-white">
                  {{ formatCurrency(project.budget) }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ getBudgetStatus(project) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-white">
                  {{ formatDate(project.startDate) }}
                </div>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  {{ formatDate(project.endDate) }}
                </div>
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
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center space-x-2">
                  <button
                    @click="viewProject(project)"
                    class="text-indigo-600 hover:text-indigo-900 dark:text-indigo-400 dark:hover:text-indigo-300"
                    title="View Details"
                  >
                    <Eye class="w-4 h-4" />
                  </button>
                  <button
                    @click="editProject(project)"
                    class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                    title="Edit Project"
                  >
                    <Edit class="w-4 h-4" />
                  </button>
                  
                  <!-- Status Actions -->
                  <button
                    v-if="project.status === 'PLANNED'"
                    @click="startProject(project)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                    title="Start Project"
                  >
                    <Play class="w-4 h-4" />
                  </button>
                  <button
                    v-if="project.status === 'IN_PROGRESS'"
                    @click="completeProject(project)"
                    class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                    title="Complete Project"
                  >
                    <CheckCircle class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Empty State -->
      <div v-if="filteredProjects.length === 0" class="text-center py-12">
        <FolderOpen class="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">No projects found</h3>
        <p class="text-gray-500 dark:text-gray-400 mb-4">
          {{ projects.length === 0 ? 'Create your first department project.' : 'Try adjusting your filters.' }}
        </p>
        <button
          v-if="projects.length === 0"
          @click="openCreateModal"
          class="inline-flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors duration-200"
        >
          <Plus class="w-4 h-4" />
          <span>Create Project</span>
        </button>
      </div>
    </div>

    <!-- Modals -->
    <DepartmentProjectModal
      v-if="showModal"
      :project="selectedProject"
      :is-editing="isEditing"
      :department-id="departmentInfo.id"
      @close="closeModal"
      @save="handleSave"
    />

    <DepartmentProjectViewModal
      v-if="showViewModal"
      :project="selectedProject"
      @close="closeViewModal"
      @edit="editProject"
    />
  </div>
</template>

<script setup>

import { ref, computed, onMounted, watch } from 'vue'
import {
  FolderOpen, Search, Plus, Eye, Edit, Play, CheckCircle, Circle, AlertCircle, 
  AlertTriangle, Zap, TrendingUp, Clock, DollarSign
} from 'lucide-vue-next'
import axios from 'axios'
import DepartmentProjectModal from '../../components/departmentsDept/DepartmentProjectModal.vue'
import DepartmentProjectViewModal from '../../components/departmentsDept/DepartmentProjectViewModal.vue'
import { useToastStore } from '../../stores/toast'

const toastStore = useToastStore()

// Department info (would come from auth/context)
const departmentInfo = ref({
  id: 1,
  name: 'IT Department'
})

// Data
const projects = ref([])
const loading = ref(true)

// Modals
const showModal = ref(false)
const showViewModal = ref(false)
const selectedProject = ref(null)
const isEditing = ref(false)

// Filters
const filters = ref({
  search: '',
  status: '',
  priority: '',
  dateRange: ''
})

// Quick stats
const quickStats = ref([
  { title: 'Active', value: '0', icon: TrendingUp, iconColor: 'text-blue-500', borderColor: 'border-blue-500' },
  { title: 'Completed', value: '0', icon: CheckCircle, iconColor: 'text-green-500', borderColor: 'border-green-500' },
  { title: 'Overdue', value: '0', icon: Clock, iconColor: 'text-red-500', borderColor: 'border-red-500' },
  { title: 'Budget Used', value: '$0', icon: DollarSign, iconColor: 'text-purple-500', borderColor: 'border-purple-500' }
])

// Computed
const filteredProjects = computed(() => {
  let filtered = projects.value

  if (filters.value.search) {
    filtered = filtered.filter(project =>
      project.name.toLowerCase().includes(filters.value.search.toLowerCase())
    )
  }

  if (filters.value.status) {
    filtered = filtered.filter(project => project.status === filters.value.status)
  }

  if (filters.value.priority) {
    filtered = filtered.filter(project => project.priority === filters.value.priority)
  }

  if (filters.value.dateRange) {
    const now = new Date()
    const nextMonth = new Date(now.getFullYear(), now.getMonth() + 1)
    const quarter = Math.floor(now.getMonth() / 3)
    filtered = filtered.filter(project => {
      const startDate = new Date(project.startDate)
      const endDate = new Date(project.endDate)
      const projectQuarter = Math.floor(startDate.getMonth() / 3)
      
      switch (filters.value.dateRange) {
        case 'this_month':
          return startDate.getMonth() === now.getMonth() && startDate.getFullYear() === now.getFullYear()
        case 'next_month':
          return startDate.getMonth() === nextMonth.getMonth() && startDate.getFullYear() === nextMonth.getFullYear()
        case 'this_quarter':
          return projectQuarter === quarter && startDate.getFullYear() === now.getFullYear()
        case 'overdue':
          return endDate < now && project.status !== 'COMPLETED'
        default:
          return true
      }
    })
  }

  return filtered
})

// Methods
const fetchDepartmentProjects = async () => {
  try {
    loading.value = true
    const response = await axios.get(`/api/projects/department/${departmentInfo.value.id}`)
    projects.value = response.data
    updateQuickStats()
  } catch (error) {
    console.error('Error fetching department projects:', error)
    toastStore.showToast('Error loading projects', 'error')
  } finally {
    loading.value = false
  }
}

const updateQuickStats = () => {
  const active = projects.value.filter(p => p.status === 'IN_PROGRESS').length
  const completed = projects.value.filter(p => p.status === 'COMPLETED').length
  const overdue = projects.value.filter(p => {
    const endDate = new Date(p.endDate)
    return endDate < new Date() && p.status !== 'COMPLETED'
  }).length
  const budgetUsed = projects.value.reduce((sum, p) => sum + (p.budgetUsed || 0), 0)

  quickStats.value[0].value = active.toString()
  quickStats.value[1].value = completed.toString()
  quickStats.value[2].value = overdue.toString()
  quickStats.value[3].value = formatCurrency(budgetUsed)
}

// Format functions
const formatStatus = (status) => {
  const statusMap = {
    'PLANNED': 'Planned',
    'IN_PROGRESS': 'In Progress',
    'COMPLETED': 'Completed',
    'CANCELLED': 'Cancelled'
  }
  return statusMap[status] || status
}

const formatPriority = (priority) => {
  const priorityMap = {
    'LOW': 'Low',
    'MEDIUM': 'Medium',
    'HIGH': 'High',
    'CRITICAL': 'Critical'
  }
  return priorityMap[priority] || priority
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
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

const getPriorityIcon = (priority) => {
  const icons = {
    'LOW': Circle,
    'MEDIUM': AlertCircle,
    'HIGH': AlertTriangle,
    'CRITICAL': Zap
  }
  return icons[priority] || Circle
}

const getPriorityColor = (priority) => {
  const colors = {
    'LOW': 'text-green-500',
    'MEDIUM': 'text-yellow-500',
    'HIGH': 'text-orange-500',
    'CRITICAL': 'text-red-500'
  }
  return colors[priority] || 'text-gray-500'
}

const getBudgetStatus = (project) => {
  if (!project.budget) return 'No budget'
  const used = project.budgetUsed || 0
  const percentage = (used / project.budget) * 100
  return `${percentage.toFixed(0)}% used`
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

// Modal functions
const openCreateModal = () => {
  selectedProject.value = null
  isEditing.value = false
  showModal.value = true
}

const editProject = (project) => {
  selectedProject.value = { ...project }
  isEditing.value = true
  showModal.value = true
}

const viewProject = (project) => {
  selectedProject.value = project
  showViewModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedProject.value = null
}

const closeViewModal = () => {
  showViewModal.value = false
  selectedProject.value = null
}

// CRUD operations
const handleSave = async (projectData) => {
  try {
    if (isEditing.value) {
      await axios.put(`/api/projects/${selectedProject.value.id}`, projectData)
      toastStore.showToast('Project updated successfully', 'success')
    } else {
      await axios.post('/api/projects', { ...projectData, departmentId: departmentInfo.value.id })
      toastStore.showToast('Project created successfully', 'success')
    }
    
    await fetchDepartmentProjects()
    closeModal()
  } catch (error) {
    console.error('Error saving project:', error)
    toastStore.showToast('Error saving project', 'error')
  }
}

// Status change operations
const startProject = async (project) => {
  try {
    await axios.post(`/api/projects/${project.id}/start`)
    project.status = 'IN_PROGRESS'
    updateQuickStats()
    toastStore.showToast('Project started successfully', 'success')
  } catch (error) {
    console.error('Error starting project:', error)
    toastStore.showToast('Error starting project', 'error')
  }
}

const completeProject = async (project) => {
  if (!confirm(`Are you sure you want to mark "${project.name}" as completed?`)) return
  
  try {
    await axios.post(`/api/projects/${project.id}/complete`)
    project.status = 'COMPLETED'
    updateQuickStats()
    toastStore.showToast('Project completed successfully', 'success')
  } catch (error) {
    console.error('Error completing project:', error)
    toastStore.showToast('Error completing project', 'error')
  }
}

const clearFilters = () => {
  filters.value = {
    search: '',
    status: '',
    priority: '',
    dateRange: ''
  }
}

// Watch for filter changes
watch(filters, () => {
  // Filters are reactive, no need for additional logic
}, { deep: true })

onMounted(() => {
  fetchDepartmentProjects()
})

</script>

