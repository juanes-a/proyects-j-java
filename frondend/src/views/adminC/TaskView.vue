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
            <span class="hidden sm:inline">Plantilla</span>
        </button>

        <button 
            @click="showTaskUploadModal = true"
            class="px-4 py-2 bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-200 border border-slate-200 dark:border-slate-700 rounded-xl font-medium hover:bg-slate-50 dark:hover:bg-slate-700 transition-all shadow-sm flex items-center gap-2"
        >
            <span class="hidden sm:inline">Importar</span>
        </button>

        <button 
          @click="openCreateModal" 
          class="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-semibold shadow-lg shadow-indigo-200 dark:shadow-none transform hover:-translate-y-0.5 transition-all flex items-center gap-2"
        >
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
          </svg>
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
            <span :class="`${stat.textClass} text-xl font-bold`">#</span>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700 p-4 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="relative col-span-1 md:col-span-2">
          <input 
            v-model="filters.name"
            type="text" 
            placeholder="Buscar tarea..." 
            class="w-full pl-4 pr-4 py-2.5 bg-slate-50 dark:bg-slate-900 border-none rounded-xl text-slate-700 dark:text-white focus:ring-2 focus:ring-indigo-500 transition-all placeholder-slate-400"
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
                  <span class="text-sm font-medium text-slate-700 dark:text-slate-300">{{ formatPriority(task.priority) }}</span>
                </div>
              </td>

              <td class="p-4">
                <div class="flex flex-col text-xs">
                  <span class="text-slate-500 mb-1">{{ formatDate(task.startDate) }}</span>
                  <span :class="isOverdue(task) ? 'text-red-500 font-bold' : 'text-slate-500'">
                    {{ formatDate(task.endDate) }}
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
                  <button @click="editTask(task)" class="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 hover:text-blue-700 transition-colors flex items-center justify-center border border-blue-200" title="Editar">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                  <button @click="confirmDelete(task)" class="w-8 h-8 rounded-lg bg-red-50 text-red-600 hover:bg-red-100 hover:text-red-700 transition-colors flex items-center justify-center border border-red-200" title="Eliminar">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
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
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                        </svg>
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
                                <span class="mr-2 inline-block align-middle">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
                                    </svg>
                                </span>
                                Asignar Responsable (Opcional)
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
                       <span v-if="loading">Guardando...</span>
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
                                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                    </svg>
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
import { ref, onMounted, computed } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'
import { useToastStore } from '@/stores/toast'
import taskService from '@/services/taskService'
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

// Stats Setup
const headerStats = ref([
  { title: 'Total Tareas', value: '0', bgClass: 'bg-blue-500', textClass: 'text-blue-600' },
  { title: 'Pendientes', value: '0', bgClass: 'bg-amber-500', textClass: 'text-amber-600' },
  { title: 'En Progreso', value: '0', bgClass: 'bg-indigo-500', textClass: 'text-indigo-600' },
  { title: 'Completadas', value: '0', bgClass: 'bg-emerald-500', textClass: 'text-emerald-600' },
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
        // Lógica actualizada basada en tu script "que funciona"
        const user = JSON.parse(localStorage.getItem('user')) || {}
        const email = user.email || user.username || localStorage.getItem('userEmail');

        if (!email) {
            console.error('Email de usuario no encontrado');
            return;
        }

        const response = await api.get(`/projects/assing-project/${email}`);
        
        if (response && response.data) {
            const data = response.data
            
            // Setear Tareas
            tasks.value = data.tasks || []
            
            // Lógica para "All projects" vs Proyecto único (según script guía)
            if (data.projectName === 'All projects') {
                const allProjectsResponse = await api.get('/projects');
                projects.value = [
                    { id: null, name: 'Todos los proyectos' },
                    ...allProjectsResponse.data.map(p => ({ id: p.id, name: p.name }))
                ];
                currentProjectName.value = 'Todos los proyectos';
            } else {
                projects.value = [{ id: data.projectId, name: data.projectName }];
                currentProjectName.value = data.projectName;
            }

            updateStats()
        }
    } catch (error) {
        console.error("Error cargando tareas:", error)
        tasks.value = []
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
    await loadUserContext(); 
}

const openCreateModal = () => {
  isEditing.value = false
  currentTaskHasAssignee.value = false;
  
  // Encontrar ID de proyecto válido (saltar 'All projects' que tiene ID null)
  let defaultProjId = null;
  if (projects.value.length > 0) {
      const validProject = projects.value.find(p => p.id !== null);
      if (validProject) defaultProjId = validProject.id;
  }

  form.value = {
    id: null, name: '', description: '', status: 'PENDING', priority: 'MEDIUM',
    projectId: defaultProjId,
    startDate: '', endDate: '', estimatedHours: 0, actualHours: 0, assignedUserEmail: ''
  }
  isModalOpen.value = true
}

// Helper para inputs datetime-local (YYYY-MM-DDThh:mm)
const formatDateForInput = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day}T${hours}:${minutes}`
}

const editTask = (task) => {
  isEditing.value = true
  currentTaskHasAssignee.value = !!task.assignedUserId;

  form.value = { 
      ...task,
      startDate: task.startDate ? formatDateForInput(task.startDate) : '',
      endDate: task.endDate ? formatDateForInput(task.endDate) : '',
      projectId: task.projectId || (projects.value.find(p => p.id)?.id),
      assignedUserEmail: '' 
  }
  isModalOpen.value = true
}

const closeModal = () => isModalOpen.value = false

// Lógica de guardado basada en el script que funciona
const saveTask = async () => {
  try {
    loading.value = true
    
    // Validar proyecto
    if(!form.value.projectId) {
        toast.showToast('No hay un proyecto seleccionado para la tarea', 'warning');
        loading.value = false;
        return;
    }

    const taskData = {
        name: form.value.name,
        description: form.value.description,
        status: form.value.status,
        priority: form.value.priority,
        startDate: form.value.startDate || null,
        endDate: form.value.endDate || null,
        estimatedHours: form.value.estimatedHours,
        actualHours: form.value.actualHours,
        projectId: form.value.projectId,
        // 👇 IMPORTANTE: Enviar el ID actual si existe (para mantener asignación si no cambia)
        assignedUserId: form.value.assignedUserId 
    };

    let response;
    let taskId; // Variable para guardar el ID y usarlo en la asignación

    if (isEditing.value) {
      // Endpoint UPDATE
      response = await api.put(`/tasks/${form.value.id}`, taskData)
      taskId = form.value.id; // Usamos el ID del formulario
      toast.showToast('Tarea actualizada correctamente', 'success')
    } else {
      // Endpoint CREATE
      response = await api.post('/tasks', taskData)
      taskId = response.data.id; // Usamos el ID que devuelve el backend
      toast.showToast('Tarea creada correctamente', 'success')
    }

    // 👇 LÓGICA DE ASIGNACIÓN (AHORA SE EJECUTA PARA AMBOS: CREAR Y EDITAR)
    // Si el usuario escribió un correo en el campo "Asignar Responsable"
    if (form.value.assignedUserEmail && form.value.assignedUserEmail.trim() !== '') {
        try {
            await api.post('/tasks/assign-task', {
                usernameOrEmail: form.value.assignedUserEmail,
                taskId: taskId, // Usamos el ID capturado arriba
                role: 'COLLAB'
            });
            // Mensaje específico si se asignó
            toast.showToast('Usuario asignado correctamente', 'success');
        } catch (e) {
            console.error(e);
            toast.showToast('Tarea guardada, pero falló la asignación (verifique el correo)', 'warning');
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
    await api.delete(`/tasks/${taskToDelete.value.id}`)
    await loadUserContext()
    toast.showToast('Tarea eliminada', 'success')
    closeDeleteModal()
  } catch (error) {
    console.error(error)
    toast.showToast('Error al eliminar', 'error')
  }
}

const clearFilters = () => filters.value = { name: '', status: '', priority: '' }

// Formatters
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

const formatDate = (d) => d ? new Date(d).toLocaleDateString('es-ES', { day: 'numeric', month: 'short' }) : '-'
const isOverdue = (t) => t.endDate && new Date(t.endDate) < new Date() && t.status !== 'COMPLETED'

onMounted(() => {
  loadUserContext()
})
</script>