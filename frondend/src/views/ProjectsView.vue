<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-5 gap-6">
      <div
        v-for="stat in headerStats"
        :key="stat.title"
        class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm card-hover"
      >
        <div class="flex flex-col">
          <div class="flex justify-between items-center mb-2">
            <p class="text-sm text-gray-600 dark:text-gray-400">{{ stat.title }}</p>
            <div :class="stat.iconBg" class="w-8 h-8 rounded-lg flex items-center justify-center">
              <component :is="stat.icon" class="w-4 h-4 text-white" />
            </div>
          </div>
          <p class="text-2xl font-bold text-gray-800 dark:text-white break-all">
            {{ stat.value }}
          </p>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl p-4">
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-lg font-semibold text-red-800 dark:text-red-200">Overdue Projects</h3>
          <AlertTriangle class="w-5 h-5 text-red-600 dark:text-red-400" />
        </div>
        <p class="text-2xl font-bold text-red-900 dark:text-red-100">{{ overdueProjects.length }}</p>
        <button
          @click="showOverdueProjects"
          class="text-sm text-red-600 dark:text-red-400 hover:text-red-800 dark:hover:text-red-200 mt-2"
        >
          View Details →
        </button>
      </div>

      <div class="bg-orange-50 dark:bg-orange-900/20 border border-orange-200 dark:border-orange-800 rounded-xl p-4">
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-lg font-semibold text-orange-800 dark:text-orange-200">Urgent Projects</h3>
          <Zap class="w-5 h-5 text-orange-600 dark:text-orange-400" />
        </div>
        <p class="text-2xl font-bold text-orange-900 dark:text-orange-100">{{ urgentProjects.length }}</p>
        <button
          @click="showUrgentProjects"
          class="text-sm text-orange-600 dark:text-orange-400 hover:text-orange-800 dark:hover:text-orange-200 mt-2"
        >
          View Details →
        </button>
      </div>

      <div class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-xl p-4">
        <div class="flex items-center justify-between mb-3">
          <h3 class="text-lg font-semibold text-blue-800 dark:text-blue-200">Ending This Week</h3>
          <Calendar class="w-5 h-5 text-blue-600 dark:text-blue-400" />
        </div>
        <p class="text-2xl font-bold text-blue-900 dark:text-blue-100">{{ endingThisWeekProjects.length }}</p>
        <button
          @click="showEndingThisWeekProjects"
          class="text-sm text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-200 mt-2"
        >
          View Details →
        </button>
      </div>
    </div>

    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white">Filters & Search</h3>
        <button
          @click="clearFilters"
          class="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200"
        >
          Clear All
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Search</label>
          <div class="relative">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              v-model="filters.name"
              type="text"
              placeholder="Search projects..."
              class="pl-10 w-full form-input"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Department</label>
          <select v-model="filters.departmentId" class="form-input">
            <option value="">All Departments</option>
            <option v-for="dept in departments" :key="dept.id" :value="dept.id">
              {{ dept.name }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
          <select v-model="filters.status" class="form-input">
            <option value="">All Status</option>
            <option v-for="status in projectStatuses" :key="status" :value="status">
              {{ formatStatus(status) }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Priority</label>
          <select v-model="filters.priority" class="form-input">
            <option value="">All Priorities</option>
            <option v-for="priority in projectPriorities" :key="priority" :value="priority">
              {{ formatPriority(priority) }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Start Date From</label>
          <input v-model="filters.startDate" type="date" class="form-input" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">End Date Until</label>
          <input v-model="filters.endDate" type="date" class="form-input" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Min Budget</label>
          <input v-model.number="filters.minBudget" type="number" min="0" step="0.01" class="form-input" />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Max Budget</label>
          <input v-model.number="filters.maxBudget" type="number" min="0" step="0.01" class="form-input" />
        </div>
      </div>
      <div class="mt-4">
        <button
          @click="generatePdf"
          class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none"
               viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 4v16m8-8H4" />
          </svg>
          Generar PDF
        </button>
      </div>
    </div>

    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm">
      <div class="p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          <div>
            <h2 class="text-xl font-semibold text-gray-800 dark:text-white">Projects</h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Manage your organization projects</p>
          </div>

          <div class="flex items-center gap-2">
            <button 
                @click="showUploadModal = true"
                class="flex items-center space-x-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors duration-200"
                title="Subir archivo CSV"
            >
                <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                </svg>
                <span>Carga CSV</span>
            </button>

            <button 
                @click="showExcelUploadModal = true"
                class="flex items-center space-x-2 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg transition-colors duration-200"
                title="Subir archivo Excel"
            >
                <FileSpreadsheet class="w-4 h-4" />
                <span>Carga Excel</span>
            </button>

            <button
              @click="openCreateModal"
              class="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
            >
              <Plus class="w-4 h-4" />
              <span>Add Project</span>
            </button>
          </div>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Project
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Department
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Priority
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Dates
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Budget
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
                  <div class="w-10 h-10 bg-gradient-to-r from-green-500 to-blue-600 rounded-lg flex items-center justify-center">
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
                <div class="text-sm text-gray-900 dark:text-white">{{ project.department?.name || 'N/A' }}</div>
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
                <div class="text-sm text-gray-900 dark:text-white">
                  {{ formatDate(project.startDate) }}
                </div>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  {{ formatDate(project.endDate) }}
                </div>
                <div v-for="warning in getProjectWarnings(project)" :key="warning.type" :class="warning.class" class="text-xs mt-1">
                  {{ warning.message }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-white">
                  {{ formatCurrency(project.budget) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center space-x-2">
                  <button
                    @click="viewProject(project)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
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

                  <button
                    v-if="project.status === 'PLANNED'"
                    @click="startProject(project)"
                    :disabled="!canStartProject(project)"
                    :class="{
                      'text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300': canStartProject(project),
                      'text-gray-400 cursor-not-allowed': !canStartProject(project)
                    }"
                    :title="canStartProject(project) ? 'Start Project' : `Cannot start until ${formatDate(project.startDate)}`"
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

                  <button
                    v-if="['PLANNED', 'IN_PROGRESS'].includes(project.status)"
                    @click="cancelProject(project)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                    title="Cancel Project"
                  >
                    <XCircle class="w-4 h-4" />
                  </button>
                  
                  <button
                    @click="deleteProject(project)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300 ml-2"
                    title="Delete Project"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>

                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="filteredProjects.length === 0" class="text-center py-12">
        <FolderOpen class="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">No projects found</h3>
        <p class="text-gray-500 dark:text-gray-400 mb-4">Get started by creating your first project.</p>
        <button
          @click="openCreateModal"
          class="inline-flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
        >
          <Plus class="w-4 h-4" />
          <span>Add Project</span>
        </button>
      </div>
    </div>

    <transition name="fade">
      <ProjectViewModal
        v-if="showViewModal"
        :project="selectedProject"
        @close="closeViewModal"
        @edit="handleEditFromView"
      />
    </transition>

    <transition name="fade">
      <ProjectModal
        v-if="showModal"
        :project="selectedProject"
        :is-editing="isEditing"
        :departments="departments"
        @close="closeModal"
        @save="handleSave"
      />
    </transition>

    <CancelProjectModal
      v-if="showCancelModal"
      :project="selectedProject"
      @close="closeCancelModal"
      @confirm="handleCancelProject"
    />

    <ProjectListModal
      v-if="showListModal"
      :projects="modalProjects"
      :title="modalTitle"
      @close="closeListModal"
    />

    <BulkUploadModal
        :is-open="showUploadModal"
        title="Carga Masiva de Proyectos (CSV)"
        :upload-service-function="projectService.uploadCsv"
        @close="showUploadModal = false"
        @success="handleUploadSuccess"
    />

    <BulkUploadModal
        :is-open="showExcelUploadModal"
        title="Carga Masiva de Proyectos (Excel)"
        :upload-service-function="projectService.uploadExcel"
        @close="showExcelUploadModal = false"
        @success="handleUploadSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  FolderOpen, Users, DollarSign, TrendingUp, CheckCircle, Search, Plus, Eye, Edit,
  Play, XCircle, Trash2, AlertTriangle, Zap, Calendar, Circle, AlertCircle, FileSpreadsheet
} from 'lucide-vue-next'
import api from '@/api' 
import ProjectModal from '../components/projects/ProjectModal.vue'
import ProjectViewModal from '../components/projects/ProjectViewModal.vue'
import CancelProjectModal from '../components/projects/CancelProjectModal.vue'
import ProjectListModal from '../components/projects/ProjectListModal.vue'
import { useToastStore } from '../stores/toast'
// Importaciones para carga masiva
import projectService from '@/services/projectService';
import BulkUploadModal from '@/components/BulkUploadModal.vue';

const route = useRoute()
const router = useRouter()
const toastStore = useToastStore()

// ... [El método generatePdf se mantiene igual] ...
const generatePdf = async () => {
  try {
    const params = new URLSearchParams();
    if (filters.value.name && filters.value.name.trim()) params.append('name', filters.value.name.trim());
    if (filters.value.departmentId && filters.value.departmentId !== '') params.append('departmentId', filters.value.departmentId);
    if (filters.value.status && filters.value.status !== '') params.append('status', filters.value.status);
    if (filters.value.priority && filters.value.priority !== '') params.append('priority', filters.value.priority);
    if (filters.value.startDate && filters.value.startDate !== '') params.append('startDate', filters.value.startDate);
    if (filters.value.endDate && filters.value.endDate !== '') params.append('endDate', filters.value.endDate);
    if (filters.value.minBudget !== null && filters.value.minBudget !== '') params.append('minBudget', filters.value.minBudget);
    if (filters.value.maxBudget !== null && filters.value.maxBudget !== '') params.append('maxBudget', filters.value.maxBudget);

    const baseUrl = api.defaults.baseURL || '/api';
    const url = `${baseUrl}/projects/report/pdf${params.toString() ? '?' + params.toString() : ''}`;

    console.log('📄 Generando PDF con URL:', url);
    const response = await api.get(url, { responseType: 'blob' });

    const blob = new Blob([response.data], { type: 'application/pdf' });
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', 'projects-report.pdf');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);

    toastStore.showToast('PDF generado exitosamente', 'success');
  } catch (error) {
    console.error('Error generating PDF:', error);
    toastStore.showToast('Error al generar el PDF', 'error');
  }
};

// Data
const projects = ref([])
const departments = ref([])
const overdueProjects = ref([])
const urgentProjects = ref([])
const endingThisWeekProjects = ref([])
const loading = ref(true)

// Modals
const showModal = ref(false)
const showViewModal = ref(false)
const showEditModal = ref(false)
const showCancelModal = ref(false)
const showListModal = ref(false)
// Estados para Carga Masiva
const showUploadModal = ref(false)
const showExcelUploadModal = ref(false) // NUEVO ESTADO

const selectedProject = ref(null)
const isEditing = ref(false)
const modalProjects = ref([])
const modalTitle = ref('')

// Enums
const projectStatuses = ref(['PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'])
const projectPriorities = ref(['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'])

// Filters
const filters = ref({
  name: '',
  departmentId: route.query.department || '',
  status: '',
  priority: '',
  startDate: '',
  endDate: '',
  minBudget: null,
  maxBudget: null
})

// Stats
const headerStats = ref([
  { title: 'Total Projects', value: '0', icon: FolderOpen, iconBg: 'bg-blue-500' },
  { title: 'In Progress', value: '0', icon: TrendingUp, iconBg: 'bg-green-500' },
  { title: 'Completed', value: '0', icon: CheckCircle, iconBg: 'bg-purple-500' },
  { title: 'Total Budget', value: '$0', icon: DollarSign, iconBg: 'bg-orange-500' },
  { title: 'Avg. Budget', value: '$0', icon: Users, iconBg: 'bg-indigo-500' }
])

// Computed
const filteredProjects = computed(() => {
  let filtered = projects.value
  if (filters.value.name) {
    filtered = filtered.filter(project =>
      project.name.toLowerCase().includes(filters.value.name.toLowerCase())
    )
  }
  if (filters.value.departmentId) {
    filtered = filtered.filter(project => project.department?.id == filters.value.departmentId)
  }
  if (filters.value.status) {
    filtered = filtered.filter(project => project.status === filters.value.status)
  }
  if (filters.value.priority) {
    filtered = filtered.filter(project => project.priority === filters.value.priority)
  }
  if (filters.value.startDate) {
    filtered = filtered.filter(project => project.startDate >= filters.value.startDate)
  }
  if (filters.value.endDate) {
    filtered = filtered.filter(project => project.endDate <= filters.value.endDate)
  }
  if (filters.value.minBudget !== null && filters.value.minBudget !== '') {
    filtered = filtered.filter(project => (project.budget || 0) >= filters.value.minBudget)
  }
  if (filters.value.maxBudget !== null && filters.value.maxBudget !== '') {
    filtered = filtered.filter(project => (project.budget || 0) <= filters.value.maxBudget)
  }
  return filtered
})

// Methods
const fetchProjects = async () => {
  try {
    loading.value = true
    let url = '/projects' 
    if (route.query.department) {
      url += `?departmentId=${route.query.department}`
    }
    const response = await api.get(url)
    projects.value = response.data
    updateStats()
  } catch (error) {
    handleError(error, 'Error loading projects')
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const response = await api.get('/departments')
    departments.value = response.data
  } catch (error) {
    handleError(error, 'Error loading departments')
  }
}

const fetchSpecialProjects = async () => {
  try {
    const [overdueRes, urgentRes, endingRes] = await Promise.all([
      api.get('/projects/overdue'),
      api.get('/projects/urgent'),
      api.get('/projects/ending-this-week')
    ]);

    overdueProjects.value = overdueRes.data;
    urgentProjects.value = urgentRes.data;
    endingThisWeekProjects.value = endingRes.data;
  } catch (error) {
    handleError(error, 'Error loading project data')
  }
};

// Manejar éxito de carga masiva (cierra ambos modales)
const handleUploadSuccess = async () => {
    showUploadModal.value = false;
    showExcelUploadModal.value = false;
    await fetchProjects();
    await fetchSpecialProjects();
};

const updateStats = () => {
  const total = projects.value.length
  const inProgress = projects.value.filter(p => p.status === 'IN_PROGRESS').length
  const completed = projects.value.filter(p => p.status === 'COMPLETED').length
  const totalBudget = projects.value.reduce((sum, p) => sum + (p.budget || 0), 0)
  const avgBudget = total > 0 ? totalBudget / total : 0

  headerStats.value[0].value = total.toString()
  headerStats.value[1].value = inProgress.toString()
  headerStats.value[2].value = completed.toString()
  headerStats.value[3].value = formatCurrency(totalBudget)
  headerStats.value[4].value = formatCurrency(avgBudget)
}

const handleError = (error, message) => {
  console.error(message, error)
  let errorMessage = message
  if (error.response) {
    console.error('Error details:', error.response.data)
    if (error.response.data?.message) {
      errorMessage = error.response.data.message
    } else if (typeof error.response.data === 'string') {
      errorMessage = error.response.data
    }
  }
  toastStore.showToast(errorMessage, 'error')
}

const getProjectWarnings = (project) => {
  const warnings = []
  if (!canStartProject(project) && project.status === 'PLANNED') {
    warnings.push({
      type: 'start-date',
      message: `Cannot start until ${formatDate(project.startDate)}`,
      class: 'text-orange-600 dark:text-orange-400'
    })
  }
  if (project.endDate && new Date() > new Date(project.endDate) && project.status !== 'COMPLETED') {
    warnings.push({
      type: 'overdue',
      message: 'Overdue',
      class: 'text-red-600 dark:text-red-400'
    })
  }
  return warnings
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
  const icons = { 'LOW': Circle, 'MEDIUM': AlertCircle, 'HIGH': AlertTriangle, 'CRITICAL': Zap }
  return icons[priority] || Circle
}

const getPriorityColor = (priority) => {
  const colors = { 'LOW': 'text-green-500', 'MEDIUM': 'text-yellow-500', 'HIGH': 'text-orange-500', 'CRITICAL': 'text-red-500' }
  return colors[priority] || 'text-gray-500'
}

// Modal functions
const openCreateModal = () => { selectedProject.value = null; isEditing.value = false; showModal.value = true }
const editProject = (project) => { selectedProject.value = { ...project }; isEditing.value = true; showModal.value = true }
const viewProject = (project) => { selectedProject.value = project; showViewModal.value = true }
const handleEditFromView = (project) => { showViewModal.value = false; nextTick(() => { editProject(project) }) }
const closeModal = () => { showModal.value = false; selectedProject.value = null }
const closeViewModal = () => { showViewModal.value = false; selectedProject.value = null }
const closeCancelModal = () => { showCancelModal.value = false; selectedProject.value = null }
const closeListModal = () => { showListModal.value = false; modalProjects.value = []; modalTitle.value = '' }

// CRUD operations
const handleSave = async (projectData) => {
  try {
    const cleanedData = {
      ...projectData,
      description: projectData.description || '',
      objectives: projectData.objectives || '',
      departmentId: projectData.departmentId ? parseInt(projectData.departmentId) : null,
      budget: projectData.budget ? parseFloat(projectData.budget) : 0,
      startDate: projectData.startDate || null,
      endDate: projectData.endDate || null
    }

    Object.keys(cleanedData).forEach(key => {
      if (cleanedData[key] === null || cleanedData[key] === undefined) {
        if (!['description', 'objectives', 'startDate', 'endDate'].includes(key)) {
          delete cleanedData[key]
        }
      }
    })

    if (isEditing.value) {
      await api.put(`/projects/${selectedProject.value.id}`, cleanedData)
      toastStore.showToast('Project updated successfully', 'success')
    } else {
      await api.post('/projects', cleanedData)
      toastStore.showToast('Project created successfully', 'success')
    }

    await fetchProjects()
    await fetchSpecialProjects()
    closeModal()
  } catch (error) {
    console.error('Error saving project:', error)
    if (error.response) {
      const errorMessage = error.response.data?.message || error.response.data?.error || 'Error saving project'
      toastStore.showToast(errorMessage, 'error')
    } else {
      toastStore.showToast('Error saving project', 'error')
    }
  }
}

const deleteProject = async (project) => {
  if (!confirm(`Are you sure you want to delete "${project.name}"?`)) return
  try {
    await api.delete(`/projects/${project.id}`)
    projects.value = projects.value.filter(p => p.id !== project.id)
    updateStats()
    await fetchSpecialProjects()
    toastStore.showToast('Project deleted successfully', 'success')
  } catch (error) {
    handleError(error, 'Error deleting project')
  }
}

const canStartProject = (project) => {
  if (!project.startDate) return true
  const today = new Date(); today.setHours(0, 0, 0, 0)
  const startDate = new Date(project.startDate); startDate.setHours(0, 0, 0, 0)
  return today >= startDate
}

const canCompleteProject = (project) => project.status === 'IN_PROGRESS'

const startProject = async (project) => {
  if (!canStartProject(project)) {
    const startDate = new Date(project.startDate).toLocaleDateString()
    toastStore.showToast(`Cannot start project before its start date (${startDate})`, 'warning')
    return
  }
  if (project.priority === 'CRITICAL' && !window.confirm(`Are you sure you want to start the critical project "${project.name}"?`)) return

  try {
    await api.post(`/projects/${project.id}/start`)
    const projectIndex = projects.value.findIndex(p => p.id === project.id)
    if (projectIndex !== -1) projects.value[projectIndex].status = 'IN_PROGRESS'
    updateStats()
    await fetchSpecialProjects()
    toastStore.showToast('Project started successfully', 'success')
  } catch (error) {
    handleError(error, 'Error starting project')
  }
}

const completeProject = async (project) => {
  if (!canCompleteProject(project)) {
    toastStore.showToast('Project must be in progress to be completed', 'warning')
    return
  }
  if (!confirm(`Are you sure you want to mark "${project.name}" as completed?`)) return

  try {
    await api.post(`/projects/${project.id}/complete`)
    const projectIndex = projects.value.findIndex(p => p.id === project.id)
    if (projectIndex !== -1) projects.value[projectIndex].status = 'COMPLETED'
    updateStats()
    await fetchSpecialProjects()
    toastStore.showToast('Project completed successfully', 'success')
  } catch (error) {
    handleError(error, 'Error completing project')
  }
}

const cancelProject = (project) => {
  selectedProject.value = project
  showCancelModal.value = true
}

const handleCancelProject = async (reason) => {
  try {
    await api.post(`/projects/${selectedProject.value.id}/cancel`, { reason })
    selectedProject.value.status = 'CANCELLED'
    updateStats()
    await fetchSpecialProjects()
    closeCancelModal()
    toastStore.showToast('Project cancelled successfully', 'success')
  } catch (error) {
    handleError(error, 'Error cancelling project')
  }
}

const showOverdueProjects = () => { modalProjects.value = overdueProjects.value; modalTitle.value = 'Overdue Projects'; showListModal.value = true }
const showUrgentProjects = () => { modalProjects.value = urgentProjects.value; modalTitle.value = 'Urgent Projects'; showListModal.value = true }
const showEndingThisWeekProjects = () => { modalProjects.value = endingThisWeekProjects.value; modalTitle.value = 'Projects Ending This Week'; showListModal.value = true }

const clearFilters = () => {
  filters.value = { name: '', departmentId: '', status: '', priority: '', startDate: '', endDate: '', minBudget: null, maxBudget: null }
  router.replace({ query: null })
}

watch(() => route.query.department, (newDepartmentId) => {
  filters.value.departmentId = newDepartmentId || ''
}, { immediate: true })

watch(() => filters.value.departmentId, (newDepartmentId) => {
  if (newDepartmentId) router.replace({ query: { department: newDepartmentId } })
  else router.replace({ query: null })
})

onMounted(() => {
  fetchProjects()
  fetchDepartments()
  fetchSpecialProjects()
})
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>