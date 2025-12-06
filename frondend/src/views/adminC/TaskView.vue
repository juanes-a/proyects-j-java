<template>
  <div class="space-y-6 bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 min-h-screen p-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="stat in headerStats" :key="stat.title" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm card-hover border border-slate-100 dark:border-gray-700">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-slate-600 dark:text-gray-300 mb-1">{{ stat.title }}</p>
            <p class="text-3xl font-bold text-slate-800 dark:text-white">{{ stat.value }}</p>
            <p class="text-sm text-slate-500 dark:text-gray-400 mt-1">+2 this month</p>
          </div>
          <div :class="stat.iconBg" class="w-12 h-12 rounded-xl flex items-center justify-center shadow-md">
            <i :class="stat.icon" class="text-white text-lg"></i>
          </div>
        </div>
      </div>
    </div>

   <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <div class="lg:col-span-1">
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 sticky top-6 border border-slate-100 dark:border-gray-700">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-bold text-slate-800 dark:text-white flex items-center gap-2">
              <i class="fas fa-filter text-indigo-500"></i> Filters
            </h3>
            <button
              @click="clearFilters"
              class="text-sm text-slate-500 hover:text-indigo-600 dark:text-gray-400 dark:hover:text-indigo-400 font-medium transition-colors"
            >
              Reset All
            </button>
          </div>

          <div class="space-y-5">
            <div>
              <label class="form-label">Search</label>
              <div class="relative">
                <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
                <input
                  v-model="filters.name"
                  type="text"
                  placeholder="Task name..."
                  class="form-input pl-10"
                />
              </div>
            </div>

             <div>
              <label class="form-label">Project</label>
              <select v-model="filters.projectId" class="form-input">
                <option value="">All Projects</option>
                <option v-for="project in projects" :key="project.id" :value="project.id">
                  {{ project.name }}
                </option>
              </select>
            </div>


            <div>
              <label class="form-label">Status</label>
              <div class="space-y-2">
                <div v-for="status in taskStatuses" :key="status" class="flex items-center">
                  <input
                    type="radio"
                    :id="status"
                    :value="status"
                    v-model="filters.status"
                    class="text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                  <label :for="status" class="ml-2 text-sm text-slate-600 dark:text-gray-300 cursor-pointer">
                    {{ formatStatus(status) }}
                  </label>
                </div>
                 <div class="flex items-center">
                  <input
                    type="radio"
                    id="all-status"
                    value=""
                    v-model="filters.status"
                    class="text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                  <label for="all-status" class="ml-2 text-sm text-slate-600 dark:text-gray-300 cursor-pointer">
                    All Statuses
                  </label>
                </div>
              </div>
            </div>

            <div>
              <label class="form-label">Priority</label>
              <select v-model="filters.priority" class="form-input">
                <option value="">All Priorities</option>
                <option v-for="priority in taskPriorities" :key="priority" :value="priority">
                  {{ formatPriority(priority) }}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <div class="lg:col-span-3 space-y-6">
        <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-slate-100 dark:border-gray-700 overflow-hidden">
          <div class="p-6 border-b border-slate-100 dark:border-gray-700">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div>
                <h2 class="text-xl font-bold text-slate-800 dark:text-white">Tasks</h2>
                <p class="text-sm text-slate-500 dark:text-gray-400">Manage and track your project tasks</p>
              </div>
              
              <div class="flex items-center gap-3">
                 <button 
                    @click="showTaskUploadModal = true"
                    class="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded-lg transition-all duration-200 shadow-md hover:shadow-lg flex items-center gap-2"
                >
                    <i class="fas fa-file-upload"></i>
                    <span>Carga Masiva</span>
                </button>

                 <button @click="openCreateModal" class="btn-primary flex items-center gap-2 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200">
                  <i class="fas fa-plus"></i>
                  <span>Add Task</span>
                </button>
              </div>
            </div>
          </div>

           <div v-if="loading" class="p-12 text-center">
                <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
                <p class="mt-4 text-slate-500 dark:text-gray-400">Loading tasks...</p>
            </div>

             <div v-else-if="filteredTasks.length === 0" class="p-12 text-center">
                <div class="w-24 h-24 bg-slate-50 dark:bg-gray-700 rounded-full flex items-center justify-center mx-auto mb-4">
                    <i class="fas fa-tasks text-4xl text-slate-300 dark:text-gray-500"></i>
                </div>
                <h3 class="text-lg font-medium text-slate-900 dark:text-white">No tasks found</h3>
                <p class="text-slate-500 dark:text-gray-400 mt-2">Try adjusting your filters or create a new task.</p>
            </div>

          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead class="bg-slate-50 dark:bg-gray-700/50">
                <tr>
                  <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Task Info</th>
                  <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Project</th>
                  <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Priority</th>
                   <th class="px-6 py-4 text-left text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Timeline</th> <th class="px-6 py-4 text-right text-xs font-semibold text-slate-500 dark:text-gray-400 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100 dark:divide-gray-700">
                <tr v-for="task in filteredTasks" :key="task.id" class="group hover:bg-slate-50 dark:hover:bg-gray-700/50 transition-colors duration-150">
                  <td class="px-6 py-4">
                    <div class="flex items-start">
                      <div class="flex-shrink-0 mt-1">
                        <div class="w-8 h-8 rounded-lg bg-indigo-100 dark:bg-indigo-900/50 flex items-center justify-center text-indigo-600 dark:text-indigo-400 font-bold text-xs">
                          #{{ task.id }}
                        </div>
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-semibold text-slate-900 dark:text-white group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                          {{ task.name }}
                        </div>
                        <div class="text-sm text-slate-500 dark:text-gray-400 line-clamp-1 max-w-xs" :title="task.description">
                          {{ task.description || 'No description' }}
                        </div>
                         <div class="mt-1 flex items-center gap-2 text-xs text-slate-400 dark:text-gray-500">
                             <span v-if="task.estimatedHours" title="Estimated Hours">
                                <i class="fas fa-clock mr-1"></i>Est: {{ task.estimatedHours }}h
                             </span>
                             <span v-if="task.actualHours" title="Actual Hours">
                                <i class="fas fa-history mr-1"></i>Act: {{ task.actualHours }}h
                             </span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                     <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 border border-blue-100 dark:border-blue-800">
                        {{ task.projectName || 'Unassigned' }} </span>
                  </td>
                  <td class="px-6 py-4">
                    <span :class="getStatusClass(task.status)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border">
                      <span class="w-1.5 h-1.5 rounded-full bg-current mr-1.5"></span>
                      {{ formatStatus(task.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4">
                    <div class="flex items-center">
                      <i :class="getPriorityIcon(task.priority)" class="mr-2 text-sm"></i>
                      <span :class="getPriorityClass(task.priority)" class="text-sm font-medium">
                        {{ formatPriority(task.priority) }}
                      </span>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                        <div class="text-xs text-slate-600 dark:text-gray-300">
                            <div v-if="task.startDate">
                                <span class="font-medium text-slate-400">Start:</span> {{ formatDate(task.startDate) }}
                            </div>
                            <div v-if="task.endDate" :class="{'text-red-500 font-semibold': isOverdue(task)}">
                                <span class="font-medium text-slate-400">End:</span> {{ formatDate(task.endDate) }}
                                 <i v-if="isOverdue(task)" class="fas fa-exclamation-circle ml-1" title="Overdue"></i>
                            </div>
                            <div v-if="!task.startDate && !task.endDate" class="text-slate-400 italic">
                                No dates set
                            </div>
                        </div>
                  </td>

                  <td class="px-6 py-4 text-right text-sm font-medium">
                    <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button @click="editTask(task)" class="p-2 text-slate-400 hover:text-indigo-600 dark:hover:text-indigo-400 transition-colors rounded-lg hover:bg-slate-100 dark:hover:bg-gray-700" title="Edit">
                        <i class="fas fa-edit"></i>
                      </button>
                      <button @click="confirmDelete(task)" class="p-2 text-slate-400 hover:text-red-600 dark:hover:text-red-400 transition-colors rounded-lg hover:bg-slate-100 dark:hover:bg-gray-700" title="Delete">
                        <i class="fas fa-trash-alt"></i>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
           <div class="bg-slate-50 dark:bg-gray-700/50 px-6 py-4 border-t border-slate-100 dark:border-gray-700 flex items-center justify-between">
                <span class="text-sm text-slate-500 dark:text-gray-400">
                    Showing {{ filteredTasks.length }} tasks
                </span>
                </div>
        </div>
      </div>
    </div>

    <TransitionRoot appear :show="isModalOpen" as="template">
      <Dialog as="div" @close="closeModal" class="relative z-50">
        <TransitionChild
          as="template"
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black/25 backdrop-blur-sm" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4 text-center">
            <TransitionChild
              as="template"
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
            >
              <DialogPanel class="w-full max-w-2xl transform overflow-hidden rounded-2xl bg-white dark:bg-gray-800 p-8 text-left align-middle shadow-xl transition-all border border-slate-100 dark:border-gray-700">
                <DialogTitle as="h3" class="text-2xl font-bold leading-6 text-slate-900 dark:text-white mb-6 flex items-center gap-3">
                    <div class="w-10 h-10 rounded-lg bg-indigo-100 dark:bg-indigo-900/50 flex items-center justify-center text-indigo-600 dark:text-indigo-400">
                        <i :class="isEditing ? 'fas fa-edit' : 'fas fa-plus'"></i>
                    </div>
                  {{ isEditing ? 'Edit Task' : 'Create New Task' }}
                </DialogTitle>
                
                <form @submit.prevent="saveTask" class="space-y-6">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="col-span-2">
                        <label class="form-label">Task Name</label>
                        <input v-model="form.name" type="text" required class="form-input" placeholder="Enter task name" />
                    </div>

                    <div class="col-span-2">
                        <label class="form-label">Description</label>
                        <textarea v-model="form.description" rows="3" class="form-input" placeholder="Enter task description"></textarea>
                    </div>

                    <div class="col-span-2">
                        <label class="form-label">Project</label>
                        <select v-model="form.projectId" required class="form-input">
                            <option :value="null" disabled>Select a project</option>
                            <option v-for="project in projects" :key="project.id" :value="project.id">
                                {{ project.name }}
                            </option>
                        </select>
                        <p v-if="!projects.length" class="text-xs text-amber-600 mt-1">No projects available. Create a project first.</p>
                    </div>

                    <div>
                        <label class="form-label">Status</label>
                        <select v-model="form.status" class="form-input">
                             <option v-for="status in taskStatuses" :key="status" :value="status">
                                {{ formatStatus(status) }}
                            </option>
                        </select>
                    </div>

                    <div>
                        <label class="form-label">Priority</label>
                        <select v-model="form.priority" class="form-input">
                            <option v-for="priority in taskPriorities" :key="priority" :value="priority">
                                {{ formatPriority(priority) }}
                            </option>
                        </select>
                    </div>
                     <div>
                        <label class="form-label">Start Date</label>
                        <input type="datetime-local" v-model="form.startDate" class="form-input" />
                    </div>
                    
                    <div>
                        <label class="form-label">End Date</label>
                        <input type="datetime-local" v-model="form.endDate" class="form-input" />
                    </div>

                     <div>
                        <label class="form-label">Estimated Hours</label>
                         <input type="number" v-model.number="form.estimatedHours" min="0" class="form-input" />
                     </div>
                     <div>
                        <label class="form-label">Actual Hours</label>
                        <input type="number" v-model.number="form.actualHours" min="0" class="form-input" />
                     </div>

                  </div>

                  <div class="mt-8 flex justify-end gap-3">
                    <button type="button" class="btn-secondary" @click="closeModal">Cancel</button>
                    <button type="submit" class="btn-primary" :disabled="loading">
                       <span v-if="loading"><i class="fas fa-spinner fa-spin mr-2"></i>Saving...</span>
                       <span v-else>{{ isEditing ? 'Update Task' : 'Create Task' }}</span>
                    </button>
                  </div>
                </form>
              </DialogPanel>
            </TransitionChild>
          </div>
        </div>
      </Dialog>
    </TransitionRoot>

    <TransitionRoot appear :show="isDeleteModalOpen" as="template">
        <Dialog as="div" @close="closeDeleteModal" class="relative z-50">
             <TransitionChild as="template" enter="duration-300 ease-out" enter-from="opacity-0" enter-to="opacity-100" leave="duration-200 ease-in" leave-from="opacity-100" leave-to="opacity-0">
                <div class="fixed inset-0 bg-black/25 backdrop-blur-sm" />
            </TransitionChild>
            <div class="fixed inset-0 overflow-y-auto">
                <div class="flex min-h-full items-center justify-center p-4 text-center">
                    <TransitionChild as="template" enter="duration-300 ease-out" enter-from="opacity-0 scale-95" enter-to="opacity-100 scale-100" leave="duration-200 ease-in" leave-from="opacity-100 scale-100" leave-to="opacity-0 scale-95">
                        <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-2xl bg-white dark:bg-gray-800 p-6 text-left align-middle shadow-xl transition-all border border-slate-100 dark:border-gray-700">
                            <DialogTitle as="h3" class="text-lg font-bold leading-6 text-slate-900 dark:text-white mb-2 text-red-600 flex items-center gap-2">
                                <i class="fas fa-exclamation-triangle"></i> Delete Task
                            </DialogTitle>
                            <div class="mt-2">
                                <p class="text-sm text-slate-500 dark:text-gray-400">
                                    Are you sure you want to delete task <strong>"{{ taskToDelete?.name }}"</strong>? This action cannot be undone.
                                </p>
                            </div>
                            <div class="mt-6 flex justify-end gap-3">
                                <button type="button" class="btn-secondary" @click="closeDeleteModal">Cancel</button>
                                <button type="button" class="bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors shadow-md" @click="deleteTask">
                                    Delete
                                </button>
                            </div>
                        </DialogPanel>
                    </TransitionChild>
                </div>
            </div>
        </Dialog>
    </TransitionRoot>

    <BulkUploadModal
        :is-open="showTaskUploadModal"
        title="Carga Masiva de Tareas"
        :upload-service-function="taskService.uploadCsv"
        @close="showTaskUploadModal = false"
        @success="handleTaskUploadSuccess"
    />

  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { useToastStore } from '@/stores/toast'
import taskService from '@/services/taskService'
import projectService from '@/services/projectService'
// Importar componente y servicio para carga masiva
import BulkUploadModal from '@/components/BulkUploadModal.vue'

const toast = useToastStore()

// State
const tasks = ref([])
const projects = ref([]) // Lista de proyectos para el dropdown
const loading = ref(false)
const isModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const isEditing = ref(false)
const taskToDelete = ref(null)

// Estado para Carga Masiva
const showTaskUploadModal = ref(false)

const filters = ref({
  name: '',
  projectId: '',
  status: '',
  priority: ''
})

const headerStats = ref([
  { title: 'Total Tasks', value: '0', icon: 'fas fa-clipboard-list', iconBg: 'bg-blue-500' },
  { title: 'Pending', value: '0', icon: 'fas fa-clock', iconBg: 'bg-amber-500' },
  { title: 'In Progress', value: '0', icon: 'fas fa-spinner', iconBg: 'bg-indigo-500' },
  { title: 'Completed', value: '0', icon: 'fas fa-check-circle', iconBg: 'bg-emerald-500' },
])

const form = ref({
  id: null,
  name: '',
  description: '',
  status: 'PENDING',
  priority: 'MEDIUM',
  projectId: null, // ID del proyecto seleccionado
  startDate: '',
  endDate: '',
  estimatedHours: 0,
  actualHours: 0
})

const taskStatuses = ['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED', 'CANCELLED']
const taskPriorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']

// Computed
const filteredTasks = computed(() => {
  return tasks.value.filter(task => {
    const matchName = task.name.toLowerCase().includes(filters.value.name.toLowerCase())
    const matchProject = !filters.value.projectId || task.projectId === filters.value.projectId // Filtrar por ID de proyecto
    const matchStatus = !filters.value.status || task.status === filters.value.status
    const matchPriority = !filters.value.priority || task.priority === filters.value.priority
    return matchName && matchProject && matchStatus && matchPriority
  })
})

// Methods
const fetchTasks = async () => {
  loading.value = true
  try {
    const response = await taskService.getAllTasks()
    tasks.value = response.data
    updateStats()
  } catch (error) {
    console.error('Error fetching tasks:', error)
    toast.showToast('Error loading tasks', 'error')
  } finally {
    loading.value = false
  }
}

// Cargar proyectos para el selector
const fetchProjects = async () => {
    try {
        const response = await projectService.getAllProjects() // Asumiendo que existe este método
        // Si projectService devuelve paginado, asegúrate de obtener la lista.
        // Aquí asumo que devuelve un array directo o dentro de .data
        projects.value = Array.isArray(response.data) ? response.data : (response.data.content || [])
    } catch (error) {
        console.error("Error fetching projects", error)
    }
}

const updateStats = () => {
  const total = tasks.value.length
  const pending = tasks.value.filter(t => t.status === 'PENDING').length
  const inProgress = tasks.value.filter(t => t.status === 'IN_PROGRESS').length
  const completed = tasks.value.filter(t => t.status === 'COMPLETED').length

  headerStats.value[0].value = total
  headerStats.value[1].value = pending
  headerStats.value[2].value = inProgress
  headerStats.value[3].value = completed
}

// Manejador de éxito para carga masiva
const handleTaskUploadSuccess = async () => {
    showTaskUploadModal.value = false;
    await fetchTasks();
    // fetchProjects no es estrictamente necesario recargarlo, pero fetchTasks sí.
}

const openCreateModal = () => {
  isEditing.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    status: 'PENDING',
    priority: 'MEDIUM',
    projectId: null,
    startDate: '',
    endDate: '',
    estimatedHours: 0,
    actualHours: 0
  }
  isModalOpen.value = true
}

const editTask = (task) => {
  isEditing.value = true
  // Formatear fechas para input datetime-local (YYYY-MM-DDTHH:mm)
  const formatDateForInput = (dateStr) => {
      if(!dateStr) return '';
      return new Date(dateStr).toISOString().slice(0, 16);
  }

  form.value = { 
      ...task,
      startDate: formatDateForInput(task.startDate),
      endDate: formatDateForInput(task.endDate)
  }
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
}

const saveTask = async () => {
  try {
    if(!form.value.projectId) {
        toast.showToast('Please select a project', 'warning');
        return;
    }

    const payload = {
        ...form.value,
        // Asegurarse de enviar fechas en formato ISO si el backend lo requiere, o null si están vacías
        startDate: form.value.startDate ? new Date(form.value.startDate).toISOString() : null,
        endDate: form.value.endDate ? new Date(form.value.endDate).toISOString() : null
    }


    if (isEditing.value) {
      const response = await taskService.updateTask(form.value.id, payload)
      // Actualizar localmente
      const index = tasks.value.findIndex(t => t.id === form.value.id)
      if (index !== -1) tasks.value[index] = response.data
      toast.showToast('Task updated successfully', 'success')
    } else {
      const response = await taskService.createTask(payload) // Backend debe manejar creación con projectId
      tasks.value.push(response.data)
      toast.showToast('Task created successfully', 'success')
    }
    updateStats()
    closeModal()
    fetchTasks() // Recargar para asegurar consistencia (ej. nombres de proyecto)
  } catch (error) {
    console.error('Error saving task:', error)
    toast.showToast('Error saving task', 'error')
  }
}

const confirmDelete = (task) => {
  taskToDelete.value = task
  isDeleteModalOpen.value = true
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
  taskToDelete.value = null
}

const deleteTask = async () => {
  if (!taskToDelete.value) return
  
  try {
    await taskService.deleteTask(taskToDelete.value.id)
    tasks.value = tasks.value.filter(t => t.id !== taskToDelete.value.id)
    updateStats()
    toast.showToast('Task deleted successfully', 'success')
    closeDeleteModal()
  } catch (error) {
    console.error('Error deleting task:', error)
    toast.showToast('Error deleting task', 'error')
  }
}

const clearFilters = () => {
  filters.value = {
    name: '',
    projectId: '',
    status: '',
    priority: ''
  }
}

// Helpers
const formatStatus = (status) => {
  return status.replace('_', ' ').charAt(0) + status.replace('_', ' ').slice(1).toLowerCase()
}

const formatPriority = (priority) => {
  return priority.charAt(0) + priority.slice(1).toLowerCase()
}

const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString() + ' ' + new Date(dateStr).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
}

const isOverdue = (task) => {
    if (!task.endDate || task.status === 'COMPLETED' || task.status === 'CANCELLED') return false;
    return new Date(task.endDate) < new Date();
}

const getStatusClass = (status) => {
  const classes = {
    'PENDING': 'bg-slate-100 text-slate-700 border-slate-200 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-600',
    'IN_PROGRESS': 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-900/30 dark:text-indigo-300 dark:border-indigo-800',
    'IN_REVIEW': 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-900/30 dark:text-amber-300 dark:border-amber-800',
    'COMPLETED': 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-900/30 dark:text-emerald-300 dark:border-emerald-800',
    'CANCELLED': 'bg-red-50 text-red-700 border-red-200 dark:bg-red-900/30 dark:text-red-300 dark:border-red-800'
  }
  return classes[status] || classes['PENDING']
}

const getPriorityIcon = (priority) => {
    const icons = {
        'LOW': 'fas fa-arrow-down',
        'MEDIUM': 'fas fa-minus',
        'HIGH': 'fas fa-arrow-up',
        'URGENT': 'fas fa-exclamation-triangle'
    }
    return icons[priority] || 'fas fa-circle'
}

const getPriorityClass = (priority) => {
    const classes = {
        'LOW': 'text-emerald-600 dark:text-emerald-400',
        'MEDIUM': 'text-blue-600 dark:text-blue-400',
        'HIGH': 'text-orange-600 dark:text-orange-400',
        'URGENT': 'text-red-600 dark:text-red-400'
    }
    return classes[priority] || 'text-gray-600'
}

// Lifecycle
onMounted(() => {
  fetchTasks()
  fetchProjects() // Cargar proyectos al montar
})
</script>

<style scoped>
/* Tus estilos personalizados adicionales aquí */
.card-hover {
  @apply transition-all duration-300 hover:shadow-lg hover:-translate-y-1;
}

.form-label {
    @apply block text-sm font-medium text-slate-700 dark:text-gray-300 mb-1;
}

.form-input {
    @apply block w-full rounded-lg border-slate-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-slate-900 dark:text-white shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-2 px-3;
}

.btn-primary {
    @apply bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg transition-colors shadow-md;
}

.btn-secondary {
    @apply bg-white dark:bg-gray-700 text-slate-700 dark:text-gray-200 border border-slate-300 dark:border-gray-600 hover:bg-slate-50 dark:hover:bg-gray-600 font-semibold py-2 px-4 rounded-lg transition-colors;
}
</style>