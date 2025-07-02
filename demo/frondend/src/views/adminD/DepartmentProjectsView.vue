<template>
  <div class="space-y-6 pl-4 pr-4">
    <!-- Quick Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <div 
        v-for="(stat, index) in quickStats" 
        :key="index"
        class="bg-white dark:bg-gray-800 p-4 rounded-lg border-l-4 shadow-sm"
        :class="stat.borderColor"
      >
        <div class="flex items-center gap-3">
          <div class="p-2 rounded-full bg-opacity-20" :class="stat.iconColor">
            <component :is="stat.icon" class="w-5 h-5" :class="stat.iconColor" />
          </div>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">{{ stat.title }}</p>
            <p class="text-xl font-semibold">{{ stat.value }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="bg-white dark:bg-gray-800 p-4 rounded-lg shadow-sm">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Search</label>
          <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search class="w-5 h-5 text-gray-400" />
            </div>
            <input
              v-model="filters.search"
              type="text"
              placeholder="Project name..."
              class="pl-10 w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Status</label>
          <select
            v-model="filters.status"
            class="w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">All Statuses</option>
            <option value="PLANNED">Planned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Priority</label>
          <select
            v-model="filters.priority"
            class="w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">All Priorities</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="CRITICAL">Critical</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Time Frame</label>
          <select
            v-model="filters.dateRange"
            class="w-full rounded-md border-gray-300 dark:border-gray-600 dark:bg-gray-700 shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
          >
            <option value="">All Time</option>
            <option value="this_month">This Month</option>
            <option value="next_month">Next Month</option>
            <option value="this_quarter">This Quarter</option>
            <option value="overdue">Overdue</option>
          </select>
        </div>
      </div>

      <div class="flex justify-between mt-3">
        <button
          @click="clearFilters"
          class="text-sm text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-300"
        >
          Clear Filters
        </button>
        <button 
          @click="openCreateModal"
          class="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition-colors text-sm"
        >
          <Plus class="w-4 h-4" />
          <span>New Project</span>
        </button>
      </div>
    </div>

    <!-- Projects Table -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm overflow-hidden">
      <div v-if="loading" class="p-8 flex justify-center">
        <div class="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-indigo-500"></div>
      </div>

      <div v-else-if="filteredProjects.length === 0" class="p-8 text-center text-gray-500">
        No projects found matching your criteria
      </div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Name</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Status</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Priority</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Dates</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Budget</th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Progress</th>
              <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="project in filteredProjects" :key="project.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10 bg-indigo-100 dark:bg-indigo-900 rounded-lg flex items-center justify-center">
                    <FolderOpen class="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900 dark:text-white">{{ project.name }}</div>
                    <div class="text-sm text-gray-500 dark:text-gray-400">{{ project.description || 'No description' }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" :class="getStatusClass(project.status)">
                  {{ formatStatus(project.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <component :is="getPriorityIcon(project.priority)" class="w-4 h-4 mr-1" :class="getPriorityColor(project.priority)" />
                  <span class="text-sm">{{ formatPriority(project.priority) }}</span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                <div>{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</div>
                <div v-if="project.status === 'IN_PROGRESS'" class="text-xs text-blue-500">
                  {{ daysRemaining(project.endDate) }} days remaining
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm">{{ formatCurrency(project.budget) }}</div>
                <div class="text-xs text-gray-500 dark:text-gray-400">{{ getBudgetStatus(project) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                  <div 
                    class="h-2 rounded-full" 
                    :class="getProgressBarClass(project.status)"
                    :style="{ width: getProgressPercentage(project) + '%' }"
                  ></div>
                </div>
                <div class="text-xs text-center mt-1">{{ getProgressPercentage(project) }}%</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end space-x-2">
                  <button 
                    @click="viewProject(project)"
                    class="text-indigo-600 dark:text-indigo-400 hover:text-indigo-900 dark:hover:text-indigo-300"
                    title="View"
                  >
                    <Eye class="w-5 h-5" />
                  </button>
                  <button 
                    @click="editProject(project)"
                    class="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-300"
                    title="Edit"
                  >
                    <Edit class="w-5 h-5" />
                  </button>
                  <button 
                    v-if="project.status === 'PLANNED'"
                    @click="startProject(project)"
                    class="text-green-600 dark:text-green-400 hover:text-green-900 dark:hover:text-green-300"
                    title="Start"
                  >
                    <Play class="w-5 h-5" />
                  </button>
                  <button 
                    v-if="project.status === 'IN_PROGRESS'"
                    @click="completeProject(project)"
                    class="text-purple-600 dark:text-purple-400 hover:text-purple-900 dark:hover:text-purple-300"
                    title="Complete"
                  >
                    <CheckCircle class="w-5 h-5" />
                  </button>
                  <button 
                    @click="openDeleteModal(project)"
                    class="text-red-600 dark:text-red-400 hover:text-red-900 dark:hover:text-red-300"
                    title="Delete"
                  >
                    <Trash2 class="w-5 h-5" />
                  </button>

                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modals -->
  <DepartmentProjectModal
    v-if="showModal && selectedProject"
    :project="selectedProject"
    :department-id="currentDepartmentId || 0"
    :is-editing="isEditing"
    @close="closeModal"
    @save="handleSave"
  />

    <DepartmentProjectViewModal
      v-if="showViewModal && selectedProject"
      :project="selectedProject"
      @close="closeViewModal"
    />

  <ConfirmDeleteModal
    :visible="showDeleteModal"
    :projectName="selectedProject?.name || ''"
    @confirm="confirmDeleteProject"
    @cancel="showDeleteModal = false"
  />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  FolderOpen, Search, Plus, Eye, Edit, Play, CheckCircle, Circle, 
  AlertCircle, AlertTriangle, Zap, TrendingUp, Clock, DollarSign, Trash2
} from 'lucide-vue-next'
import api from '../../api/index'
import DepartmentProjectModal from '../../components/departmentsDept/DepartmentProjectModal.vue'
import DepartmentProjectViewModal from '../../components/departmentsDept/DepartmentProjectViewModal.vue'
import { useToastStore } from '../../stores/toast'
import { useAuthStore } from '../../stores/auth'
import ConfirmDeleteModal from "../../components/departmentsDept/ConfirmDeleteModal.vue";

const route = useRoute()
const toastStore = useToastStore()
const authStore = useAuthStore()

// Data
const projects = ref([])
const loading = ref(true)
const currentDepartmentId = ref(null)

// Modals
const showModal = ref(false)
const showViewModal = ref(false)
const selectedProject = ref(null)
const isEditing = ref(false)
const showDeleteModal = ref(false)

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
      project.name.toLowerCase().includes(filters.value.search.toLowerCase()) ||
      project.description?.toLowerCase().includes(filters.value.search.toLowerCase())
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
    const nextMonth = new Date(now.getFullYear(), now.getMonth() + 1, 1)
    const quarterEnd = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3 + 3, 0)
    
    filtered = filtered.filter(project => {
      const startDate = new Date(project.startDate)
      const endDate = new Date(project.endDate)
      
      switch (filters.value.dateRange) {
        case 'this_month':
          return startDate.getMonth() === now.getMonth() && 
                 startDate.getFullYear() === now.getFullYear()
        case 'next_month':
          return startDate.getMonth() === nextMonth.getMonth() && 
                 startDate.getFullYear() === nextMonth.getFullYear()
        case 'this_quarter':
          return startDate <= quarterEnd && 
                 (project.status === 'PLANNED' || project.status === 'IN_PROGRESS')
        case 'overdue':
          return endDate < now && 
                 project.status !== 'COMPLETED' && 
                 project.status !== 'CANCELLED'
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
    loading.value = true;
    const userEmail = authStore.user?.email || localStorage.getItem('userEmail');
    
    if (!userEmail) {
      throw new Error('User email not available');
    }

    const response = await api.get(`/projects/user/${userEmail}`);
    
    if (!response.data) {
      throw new Error('Invalid response format');
    }

    // Nueva estructura de respuesta
    currentDepartmentId.value = response.data.departmentId || null;
    projects.value = Array.isArray(response.data.projects) ? response.data.projects : [];
    
    updateQuickStats();
    
  } catch (error) {
    console.error('Fetch error:', error);
    toastStore.showToast(error.response?.data?.message || 'Failed to load projects', 'error');
    projects.value = [];
    currentDepartmentId.value = null;
  } finally {
    loading.value = false;
  }
};

const confirmDeleteProject = async (typedName) => {
  try {
    // Usar la instancia de api que ya tienes configurada
    const response = await api.delete(`/projects/${selectedProject.value.id}`, {
      data: { name: typedName }
    })
    
    // Si la eliminación fue exitosa
    toastStore.showToast('Project deleted successfully', 'success')
    closeDeleteModal()
    await fetchDepartmentProjects() // Refrescar la lista de proyectos
    
  } catch (error) {
    console.error('Error deleting project:', error)
    const errorMessage = error.response?.data?.message || 'Error deleting project'
    toastStore.showToast(errorMessage, 'error')
  }
}

const updateQuickStats = () => {
  const active = projects.value.filter(p => p.status === 'IN_PROGRESS').length
  const completed = projects.value.filter(p => p.status === 'COMPLETED').length
  const overdue = projects.value.filter(p => {
    const endDate = new Date(p.endDate)
    return endDate < new Date() && p.status !== 'COMPLETED' && p.status !== 'CANCELLED'
  }).length
  const budgetUsed = projects.value.reduce((sum, p) => sum + (p.budgetUsed || 0), 0)

  quickStats.value[0].value = active.toString()
  quickStats.value[1].value = completed.toString()
  quickStats.value[2].value = overdue.toString()
  quickStats.value[3].value = formatCurrency(budgetUsed)
}

const daysRemaining = (endDateStr) => {
  const endDate = new Date(endDateStr)
  const now = new Date()
  const diffTime = endDate - now
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 ? diffDays : 0
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

const formatDateFromBackend = (dateString) => {
  if (!dateString) return '';
  // Si ya está en formato YYYY-MM-DD, devolver directamente
  if (/^\d{4}-\d{2}-\d{2}$/.test(dateString)) return dateString;
  // Si viene del backend con tiempo, extraer solo la fecha
  return dateString.split('T')[0];
};

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  const options = { year: 'numeric', month: 'short', day: 'numeric' }
  return new Date(dateString).toLocaleDateString(undefined, options)
}

const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
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
  if (!project.budget || project.budget === 0) return 'No budget set'
  const used = project.budgetUsed || 0
  const percentage = (used / project.budget) * 100
  return `${percentage.toFixed(0)}% used (${formatCurrency(used)} of ${formatCurrency(project.budget)})`
}

const getProgressPercentage = (project) => {
  if (project.status === 'COMPLETED') return 100
  if (project.status === 'CANCELLED') return 0
  
  if (!project.startDate || !project.endDate) return 0
  
  const start = new Date(project.startDate)
  const end = new Date(project.endDate)
  const today = new Date()
  
  if (today < start) return 0
  if (today > end) return project.status === 'IN_PROGRESS' ? 90 : 100
  
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
  if (!currentDepartmentId.value) {
    toastStore.showToast('Could not determine department. Please refresh the page.', 'error')
    return
  }
  
  selectedProject.value = {
    name: '',
    description: '',
    status: 'PLANNED',
    priority: 'MEDIUM',
    startDate: new Date().toISOString().split('T')[0],
    endDate: '',
    budget: 0,
    departmentId: currentDepartmentId.value
  }
  isEditing.value = false
  showModal.value = true
}

const editProject = (project) => {
  selectedProject.value = { 
    ...project,
    startDate: project.startDate?.split('T')[0] || '',
    endDate: project.endDate?.split('T')[0] || ''
  }
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

const openDeleteModal = (project) => {
  selectedProject.value = project
  showDeleteModal.value = true
}


const closeDeleteModal = () => {
  showDeleteModal.value = false
  selectedProject.value = null
}


// CRUD operations
const handleSave = async (projectData) => {
  try {
    if (isEditing.value) {
      await api.put(`/projects/${selectedProject.value.id}`, projectData)
      toastStore.showToast('Project updated successfully', 'success')
    } else {
      await api.post('/projects', projectData)
      toastStore.showToast('Project created successfully', 'success')
    }
    
    await fetchDepartmentProjects()
    closeModal()
  } catch (error) {
    console.error('Error saving project:', error)
    const errorMessage = error.response?.data?.message || 'Error saving project'
    toastStore.showToast(errorMessage, 'error')
  }
}

// Status change operations
const startProject = async (project) => {
  try {
    await api.post(`/projects/${project.id}/start`)
    project.status = 'IN_PROGRESS'
    updateQuickStats()
    toastStore.showToast('Project started successfully', 'success')
  } catch (error) {
    console.error('Error starting project:', error)
    const errorMessage = error.response?.data?.message || 'Error starting project'
    toastStore.showToast(errorMessage, 'error')
  }
}

const completeProject = async (project) => {
  if (!confirm(`Are you sure you want to mark "${project.name}" as completed?`)) return
  
  try {
    await api.post(`/projects/${project.id}/complete`)
    project.status = 'COMPLETED'
    updateQuickStats()
    toastStore.showToast('Project completed successfully', 'success')
  } catch (error) {
    console.error('Error completing project:', error)
    const errorMessage = error.response?.data?.message || 'Error completing project'
    toastStore.showToast(errorMessage, 'error')
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




onMounted(() => {
  fetchDepartmentProjects()
})
</script>