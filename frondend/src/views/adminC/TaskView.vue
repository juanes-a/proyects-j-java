<template>
  <div class="min-h-screen bg-slate-50 dark:bg-slate-900 p-4 sm:p-6 lg:p-8 transition-all duration-300">
    
    <div class="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4">
      <div>
        <h1 class="text-3xl font-bold text-slate-800 dark:text-white tracking-tight">
          Mis Tareas
          <span v-if="currentProjectName" class="text-indigo-600 dark:text-indigo-400 text-lg font-medium ml-2">
            / {{ currentProjectName }}
          </span>
        </h1>
        <p class="text-slate-500 dark:text-slate-400 mt-1">Gestiona y organiza tus actividades diarias.</p>
      </div>
      
      <div class="flex gap-3">
         <button 
            @click="downloadTemplate"
            class="px-4 py-2 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-200 border border-slate-200 dark:border-slate-700 rounded-xl font-medium hover:bg-slate-50 dark:hover:bg-slate-700 transition-all shadow-sm flex items-center gap-2"
        >
            <i class="fas fa-file-csv text-green-600"></i>
            <span class="hidden sm:inline">Plantilla</span>
        </button>

        <button 
            @click="showTaskUploadModal = true"
            class="px-4 py-2 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-200 border border-slate-200 dark:border-slate-700 rounded-xl font-medium hover:bg-slate-50 dark:hover:bg-slate-700 transition-all shadow-sm flex items-center gap-2"
        >
            <i class="fas fa-cloud-upload-alt text-blue-500"></i>
            <span class="hidden sm:inline">Importar</span>
        </button>

        <button 
          @click="openCreateModal" 
          class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-semibold shadow-lg shadow-indigo-200 dark:shadow-none transform hover:-translate-y-0.5 transition-all flex items-center gap-2"
        >
          <i class="fas fa-plus"></i>
          <span>Nueva Tarea</span>
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div v-for="(stat, index) in headerStats" :key="index" 
           class="bg-white dark:bg-slate-800 rounded-2xl p-5 border border-slate-100 dark:border-slate-700 shadow-sm hover:shadow-md transition-all group">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-slate-500 dark:text-slate-400 text-sm font-medium mb-1">{{ stat.title }}</p>
            <h3 class="text-3xl font-bold text-slate-800 dark:text-white group-hover:text-indigo-600 transition-colors">
              {{ stat.value }}
            </h3>
          </div>
          <div :class="`p-3 rounded-xl ${stat.bgClass} bg-opacity-10 dark:bg-opacity-20`">
            <i :class="`${stat.icon} ${stat.textClass} text-xl`"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="relative col-span-1 md:col-span-2">
          <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"></i>
          <input 
            v-model="filters.name"
            type="text" 
            placeholder="Buscar tarea..." 
            class="w-full pl-10 pr-4 py-2.5 bg-slate-50 dark:bg-slate-900 border-none rounded-xl text-slate-700 dark:text-white focus:ring-2 focus:ring-indigo-500 transition-all placeholder-slate-400"
          >
        </div>

        <select v-model="filters.status" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border-none rounded-xl text-slate-700 dark:text-white focus:ring-2 focus:ring-indigo-500 cursor-pointer">
          <option value="">Todos los Estados</option>
          <option v-for="status in taskStatuses" :key="status" :value="status">{{ formatStatus(status) }}</option>
        </select>

        <select v-model="filters.priority" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border-none rounded-xl text-slate-700 dark:text-white focus:ring-2 focus:ring-indigo-500 cursor-pointer">
          <option value="">Todas las Prioridades</option>
          <option v-for="priority in taskPriorities" :key="priority" :value="priority">{{ formatPriority(priority) }}</option>
        </select>
      </div>
    </div>

    <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 overflow-hidden">
      
      <div v-if="loading" class="p-12 text-center">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600 mx-auto mb-4"></div>
        <p class="text-slate-500">Cargando tus tareas...</p>
      </div>

      <div v-else-if="filteredTasks.length === 0" class="p-16 text-center flex flex-col items-center">
        <div class="w-20 h-20 bg-indigo-50 dark:bg-slate-700 rounded-full flex items-center justify-center mb-4">
          <i class="fas fa-clipboard-check text-3xl text-indigo-300 dark:text-slate-500"></i>
        </div>
        <h3 class="text-lg font-bold text-slate-800 dark:text-white">Sin tareas pendientes</h3>
        <p class="text-slate-500 max-w-xs mx-auto mt-2">No se encontraron tareas con los filtros actuales o aún no tienes ninguna asignada.</p>
        <button @click="clearFilters" v-if="filters.name || filters.status" class="mt-4 text-indigo-600 font-medium hover:underline">
          Limpiar filtros
        </button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="border-b border-slate-100 dark:border-slate-700 bg-slate-50/50 dark:bg-slate-800/50">
              <th class="p-4 pl-6 font-semibold text-slate-500 dark:text-slate-400 text-sm">Tarea</th>
              <th class="p-4 font-semibold text-slate-500 dark:text-slate-400 text-sm">Estado</th>
              <th class="p-4 font-semibold text-slate-500 dark:text-slate-400 text-sm">Prioridad</th>
              <th class="p-4 font-semibold text-slate-500 dark:text-slate-400 text-sm">Fechas</th>
              <th class="p-4 font-semibold text-slate-500 dark:text-slate-400 text-sm">Horas</th>
              <th class="p-4 font-semibold text-slate-500 dark:text-slate-400 text-sm">Asignado</th>
              <th class="p-4 pr-6 text-right font-semibold text-slate-500 dark:text-slate-400 text-sm">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 dark:divide-slate-700">
            <tr v-for="task in filteredTasks" :key="task.id" class="group hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors">
              
              <td class="p-4 pl-6">
                <div class="flex items-start gap-3">
                  <div class="mt-1 w-2 h-2 rounded-full bg-indigo-500 flex-shrink-0"></div>
                  <div>
                    <p class="font-bold text-slate-800 dark:text-slate-200">{{ task.name }}</p>
                    <p class="text-sm text-slate-500 dark:text-slate-400 line-clamp-1 max-w-[200px]">{{ task.description || 'Sin descripción' }}</p>
                  </div>
                </div>
              </td>

              <td class="p-4">
                <span :class="getStatusBadgeClass(task.status)" class="px-3 py-1 rounded-full text-xs font-bold border">
                  {{ formatStatus(task.status) }}
                </span>
              </td>

              <td class="p-4">
                <div class="flex items-center gap-2">
                  <i :class="getPriorityIcon(task.priority)"></i>
                  <span class="text-sm font-medium text-slate-700 dark:text-slate-300">{{ formatPriority(task.priority) }}</span>
                </div>
              </td>

              <td class="p-4">
                <div class="flex flex-col text-xs">
                  <span class="text-slate-500 mb-1"><i class="far fa-calendar mr-1"></i>{{ formatDate(task.startDate) }}</span>
                  <span :class="isOverdue(task) ? 'text-red-500 font-bold' : 'text-slate-500'">
                    <i class="far fa-flag mr-1"></i>{{ formatDate(task.endDate) }}
                  </span>
                </div>
              </td>

              <td class="p-4">
                <div class="text-xs font-mono text-slate-600 dark:text-slate-300">
                   <div class="flex justify-between gap-2"><span>Est:</span> <span class="font-bold">{{ task.estimatedHours || 0 }}h</span></div>
                   <div class="flex justify-between gap-2 opacity-70"><span>Real:</span> <span>{{ task.actualHours || 0 }}h</span></div>
                </div>
              </td>

              <td class="p-4">
                <div v-if="task.assignedUserName" class="flex items-center gap-2">
                    <div class="w-6 h-6 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center text-xs font-bold">
                        {{ task.assignedUserName.charAt(0).toUpperCase() }}
                    </div>
                    <span class="text-sm text-slate-700 dark:text-slate-300 truncate max-w-[100px]" :title="task.assignedUserName">
                        {{ task.assignedUserName }}
                    </span>
                </div>
                <span v-else class="text-xs text-slate-400 italic pl-2">-- Libre --</span>
              </td>

              <td class="p-4 pr-6 text-right">
                <div class="flex justify-end gap-2">
                  <button @click="editTask(task)" class="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 hover:text-blue-700 transition-colors flex items-center justify-center" title="Editar">
                    <i class="fas fa-pencil-alt text-sm"></i>
                  </button>
                  <button @click="confirmDelete(task)" class="w-8 h-8 rounded-lg bg-red-50 text-red-600 hover:bg-red-100 hover:text-red-700 transition-colors flex items-center justify-center" title="Eliminar">
                    <i class="fas fa-trash text-sm"></i>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <TransitionRoot appear :show="isModalOpen" as="template">
      <Dialog as="div" @close="closeModal" class="relative z-50">
        <TransitionChild as="template" enter="duration-300 ease-out" enter-from="opacity-0" enter-to="opacity-100" leave="duration-200 ease-in" leave-from="opacity-100" leave-to="opacity-0">
          <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" />
        </TransitionChild>

        <div class="fixed inset-0 overflow-y-auto">
          <div class="flex min-h-full items-center justify-center p-4">
            <TransitionChild as="template" enter="duration-300 ease-out" enter-from="opacity-0 scale-95" enter-to="opacity-100 scale-100" leave="duration-200 ease-in" leave-from="opacity-100 scale-100" leave-to="opacity-0 scale-95">
              <DialogPanel class="w-full max-w-2xl bg-white dark:bg-slate-800 rounded-2xl shadow-xl overflow-hidden border border-slate-100 dark:border-slate-700">
                
                <div class="bg-slate-50 dark:bg-slate-700/50 px-8 py-5 border-b border-slate-100 dark:border-slate-700 flex justify-between items-center">
                    <h3 class="text-xl font-bold text-slate-800 dark:text-white">
                        {{ isEditing ? 'Editar Tarea' : 'Nueva Tarea' }}
                    </h3>
                    <button @click="closeModal" class="text-slate-400 hover:text-slate-600 transition-colors">
                        <i class="fas fa-times text-xl"></i>
                    </button>
                </div>
                
                <form @submit.prevent="saveTask" class="p-8 space-y-6">
                  <div>
                    <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Nombre de la tarea</label>
                    <input v-model="form.name" type="text" required class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all" placeholder="Ej. Diseño de interfaz" />
                  </div>

                  <div>
                    <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Descripción</label>
                    <textarea v-model="form.description" rows="3" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none" placeholder="Detalles adicionales..."></textarea>
                  </div>

                  <div class="grid grid-cols-2 gap-6">
                     <div class="col-span-2">
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Proyecto Asociado</label>
                        <select v-model="form.projectId" required class="w-full px-4 py-2.5 bg-slate-100 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl text-slate-600 cursor-not-allowed" disabled>
                            <option v-for="project in projects" :key="project.id" :value="project.id">{{ project.name }}</option>
                        </select>
                    </div>

                    <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Estado</label>
                        <select v-model="form.status" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none">
                             <option v-for="status in taskStatuses" :key="status" :value="status">{{ formatStatus(status) }}</option>
                        </select>
                    </div>

                    <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Prioridad</label>
                        <select v-model="form.priority" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none">
                            <option v-for="priority in taskPriorities" :key="priority" :value="priority">{{ formatPriority(priority) }}</option>
                        </select>
                    </div>

                    <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Fecha Inicio</label>
                        <input type="datetime-local" v-model="form.startDate" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none" />
                    </div>
                    
                    <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Fecha Fin</label>
                        <input type="datetime-local" v-model="form.endDate" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none" />
                    </div>

                     <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Horas Estimadas</label>
                         <input type="number" v-model.number="form.estimatedHours" min="0" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none" />
                     </div>
                     <div>
                        <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Horas Reales</label>
                        <input type="number" v-model.number="form.actualHours" min="0" class="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-600 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none" />
                     </div>

                     <div class="col-span-2" v-if="!isEditing || (isEditing && !currentTaskHasAssignee)">
                        <div class="p-4 bg-indigo-50 dark:bg-indigo-900/20 rounded-xl border border-indigo-100 dark:border-indigo-800">
                            <label class="block text-sm font-bold text-indigo-700 dark:text-indigo-300 mb-2">
                                <i class="fas fa-user-plus mr-2"></i>Asignar Responsable (Opcional)
                            </label>
                            <input 
                                v-model="form.assignedUserEmail" 
                                type="text" 
                                class="w-full px-4 py-2.5 bg-white dark:bg-slate-900 border border-indigo-200 dark:border-indigo-700 rounded-xl focus:ring-2 focus:ring-indigo-500 outline-none placeholder-slate-400" 
                                placeholder="Escribe el email o usuario..."
                            />
                        </div>
                    </div>
                  </div>

                  <div class="flex justify-end gap-3 pt-4 border-t border-slate-100 dark:border-slate-700">
                    <button type="button" class="px-6 py-2.5 text-slate-600 font-semibold hover:bg-slate-100 rounded-xl transition-colors" @click="closeModal">Cancelar</button>
                    <button type="submit" class="px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all" :disabled="loading">
                       <span v-if="loading"><i class="fas fa-spinner fa-spin mr-2"></i>Guardando...</span>
                       <span v-else>{{ isEditing ? 'Guardar Cambios' : 'Crear Tarea' }}</span>
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
                <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" />
            </TransitionChild>
            <div class="fixed inset-0 overflow-y-auto">
                <div class="flex min-h-full items-center justify-center p-4">
                    <TransitionChild as="template" enter="duration-300 ease-out" enter-from="opacity-0 scale-95" enter-to="opacity-100 scale-100" leave="duration-200 ease-in" leave-from="opacity-100 scale-100" leave-to="opacity-0 scale-95">
                        <DialogPanel class="w-full max-w-md bg-white dark:bg-slate-800 rounded-2xl p-6 shadow-xl border border-slate-100 dark:border-slate-700">
                            <DialogTitle as="h3" class="text-lg font-bold text-slate-900 dark:text-white mb-3 flex items-center gap-2">
                                <div class="w-10 h-10 rounded-full bg-red-100 flex items-center justify-center">
                                    <i class="fas fa-exclamation-triangle text-red-600"></i>
                                </div>
                                Confirmar Eliminación
                            </DialogTitle>
                            <p class="text-slate-500 dark:text-slate-400 ml-12">
                                ¿Estás seguro de eliminar la tarea <strong>"{{ taskToDelete?.name }}"</strong>? Esta acción no se puede deshacer.
                            </p>
                            <div class="mt-6 flex justify-end gap-3">
                                <button type="button" class="px-4 py-2 text-slate-600 font-medium hover:bg-slate-100 rounded-lg transition-colors" @click="closeDeleteModal">Cancelar</button>
                                <button type="button" class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white font-semibold rounded-lg shadow-sm" @click="deleteTask">
                                    Sí, eliminar
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
        title="Carga Masiva de Tareas (CSV)"
        :upload-service-function="taskService.uploadCsv"
        @close="showTaskUploadModal = false"
        @success="handleTaskUploadSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { useToastStore } from '@/stores/toast'
import taskService from '@/services/taskService'
import projectService from '@/services/projectService'
import BulkUploadModal from '@/components/BulkUploadModal.vue'
import api from '@/api'

const toast = useToastStore()

// State
const tasks = ref([])
const projects = ref([]) 
const loading = ref(false)
const isModalOpen = ref(false)
const isDeleteModalOpen = ref(false)
const isEditing = ref(false)
const taskToDelete = ref(null)
const currentTaskHasAssignee = ref(false)
const showTaskUploadModal = ref(false)
const currentProjectName = ref('')

const filters = ref({
  name: '',
  status: '',
  priority: ''
})

const form = ref({
  id: null, name: '', description: '', status: 'PENDING', priority: 'MEDIUM',
  projectId: null, startDate: '', endDate: '', estimatedHours: 0, actualHours: 0, assignedUserEmail: ''
})

const taskStatuses = ['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED', 'CANCELLED']
const taskPriorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']

// Stats Icons Setup
const headerStats = ref([
  { title: 'Total Tareas', value: '0', icon: 'fas fa-layer-group', bgClass: 'bg-blue-500', textClass: 'text-blue-600' },
  { title: 'Pendientes', value: '0', icon: 'fas fa-clock', bgClass: 'bg-amber-500', textClass: 'text-amber-600' },
  { title: 'En Progreso', value: '0', icon: 'fas fa-spinner', bgClass: 'bg-indigo-500', textClass: 'text-indigo-600' },
  { title: 'Completadas', value: '0', icon: 'fas fa-check-circle', bgClass: 'bg-emerald-500', textClass: 'text-emerald-600' },
])

// Computed
const filteredTasks = computed(() => {
  return tasks.value.filter(task => {
    const matchName = task.name.toLowerCase().includes(filters.value.name.toLowerCase())
    const matchStatus = !filters.value.status || task.status === filters.value.status
    const matchPriority = !filters.value.priority || task.priority === filters.value.priority
    return matchName && matchStatus && matchPriority
  })
})

// Methods
const loadUserContext = async () => {
    loading.value = true
    try {
        const user = JSON.parse(localStorage.getItem('user')) || {}
        const email = user.email || user.username;

        if (!email) return;

        // Usamos el endpoint que devuelve tu proyecto asignado y tareas
        const response = await api.get(`/projects/assing-project/${email}`).catch(() => null)
        
        if (response && response.data) {
            const data = response.data
            
            // 1. Setear Tareas
            tasks.value = data.tasks || []
            
            // 2. Setear Proyecto (Solo el asignado)
            if (data.projectId) {
                projects.value = [{ id: data.projectId, name: data.projectName }]
                currentProjectName.value = data.projectName
            } else {
                 // Caso Admin Global (si aplica) o sin proyecto
                projects.value = []
            }
            updateStats()
        }
    } catch (error) {
        console.error("Error cargando contexto de usuario", error)
    } finally {
        loading.value = false
    }
}

const downloadTemplate = () => {
  const headers = 'name,description,status,priority,startDate,endDate,estimatedHours,projectId';
  const example = `Diseño UI,Pantallas Login,PENDING,HIGH,2024-05-01T09:00:00,2024-05-05T18:00:00,20,${projects.value[0]?.id || 'ID_PROYECTO'}`;
  const csvContent = "data:text/csv;charset=utf-8," + headers + "\n" + example;
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", "plantilla_tareas.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
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

const handleTaskUploadSuccess = async () => {
    showTaskUploadModal.value = false;
    await loadUserContext(); // Recargar todo
}

const openCreateModal = () => {
  isEditing.value = false
  currentTaskHasAssignee.value = false;
  
  form.value = {
    id: null, name: '', description: '', status: 'PENDING', priority: 'MEDIUM',
    projectId: projects.value.length > 0 ? projects.value[0].id : null, // Auto-seleccionar
    startDate: '', endDate: '', estimatedHours: 0, actualHours: 0, assignedUserEmail: ''
  }
  isModalOpen.value = true
}

const editTask = (task) => {
  isEditing.value = true
  currentTaskHasAssignee.value = !!task.assignedUserId;
  
  const formatDate = (d) => d ? new Date(d).toISOString().slice(0, 16) : '';

  form.value = { 
      ...task,
      startDate: formatDate(task.startDate),
      endDate: formatDate(task.endDate),
      projectId: task.projectId || (projects.value[0]?.id), // Asegurar ID de proyecto
      assignedUserEmail: '' 
  }
  isModalOpen.value = true
}

const closeModal = () => isModalOpen.value = false

const saveTask = async () => {
  try {
    loading.value = true
    
    if(!form.value.projectId) {
        toast.showToast('No hay un proyecto asociado para crear la tarea', 'warning');
        loading.value = false;
        return;
    }

    const payload = {
        ...form.value,
        startDate: form.value.startDate ? new Date(form.value.startDate).toISOString() : null,
        endDate: form.value.endDate ? new Date(form.value.endDate).toISOString() : null
    }

    let savedTaskId = null;

    if (isEditing.value) {
      const response = await taskService.updateTask(form.value.id, payload)
      savedTaskId = form.value.id;
      toast.showToast('Tarea actualizada correctamente', 'success')
    } else {
      const response = await taskService.createTask(payload)
      savedTaskId = response.data.id;
      toast.showToast('Tarea creada correctamente', 'success')
    }

    // Asignar usuario si se indicó
    if (form.value.assignedUserEmail) {
        if (!isEditing.value || (isEditing.value && !currentTaskHasAssignee.value)) {
            try {
                await api.post('/tasks/assign-user', {
                    usernameOrEmail: form.value.assignedUserEmail,
                    taskId: savedTaskId,
                    role: 'COLLAB'
                });
                toast.showToast('Usuario asignado', 'success');
            } catch (e) {
                console.error(e);
                toast.showToast('Tarea guardada, pero falló la asignación', 'warning');
            }
        }
    }

    await loadUserContext()
    closeModal()
  } catch (error) {
    console.error(error)
    toast.showToast('Error al guardar la tarea', 'error')
  } finally {
    loading.value = false
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
    await loadUserContext()
    toast.showToast('Tarea eliminada', 'success')
    closeDeleteModal()
  } catch (error) {
    console.error(error)
    toast.showToast('Error al eliminar', 'error')
  }
}

const clearFilters = () => filters.value = { name: '', status: '', priority: '' }

// Formatters & UI Helpers
const formatStatus = (s) => s ? {
    'PENDING': 'Pendiente', 'IN_PROGRESS': 'En Progreso', 
    'IN_REVIEW': 'En Revisión', 'COMPLETED': 'Completada', 'CANCELLED': 'Cancelada'
}[s] || s : ''

const formatPriority = (p) => p ? {
    'LOW': 'Baja', 'MEDIUM': 'Media', 'HIGH': 'Alta', 'URGENT': 'Urgente'
}[p] || p : ''

const getStatusBadgeClass = (s) => ({
    'PENDING': 'bg-slate-100 text-slate-600 border-slate-200',
    'IN_PROGRESS': 'bg-blue-50 text-blue-600 border-blue-200',
    'IN_REVIEW': 'bg-purple-50 text-purple-600 border-purple-200',
    'COMPLETED': 'bg-emerald-50 text-emerald-600 border-emerald-200',
    'CANCELLED': 'bg-red-50 text-red-600 border-red-200'
}[s] || 'bg-gray-100 text-gray-600')

const getPriorityIcon = (p) => ({
    'LOW': 'fas fa-arrow-down text-emerald-500',
    'MEDIUM': 'fas fa-minus text-blue-500',
    'HIGH': 'fas fa-arrow-up text-orange-500',
    'URGENT': 'fas fa-exclamation-circle text-red-500'
}[p] || '')

const formatDate = (d) => d ? new Date(d).toLocaleDateString('es-ES', { day: 'numeric', month: 'short' }) : '-'
const isOverdue = (t) => t.endDate && new Date(t.endDate) < new Date() && t.status !== 'COMPLETED'

onMounted(() => {
  loadUserContext()
})
</script>