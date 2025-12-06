<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300 font-sans">
    <div class="w-full px-4 sm:px-6 py-6">
      
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-200 dark:border-gray-700 p-6 mb-6 transition-all duration-300">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
            <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"></path></svg>
            Filters & Search
          </h2>
          <button 
            v-if="hasActiveFilters"
            @click="clearFilters" 
            class="text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 px-3 py-1 rounded-lg transition-colors duration-200 flex items-center gap-1"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
            Clear filters
          </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div class="col-span-1 md:col-span-2 lg:col-span-1">
            <label class="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1.5 uppercase tracking-wider">Search</label>
            <div class="relative group">
              <input 
                type="text" 
                v-model="filters.keyword" 
                @input="filterTasks" 
                placeholder="Search by name..." 
                class="w-full pl-10 pr-4 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none transition-all text-gray-900 dark:text-white placeholder-gray-400 group-hover:bg-white dark:group-hover:bg-gray-700"
              >
              <svg class="absolute left-3 top-3 h-5 w-5 text-gray-400 group-focus-within:text-blue-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1.5 uppercase tracking-wider">Status</label>
              <select 
                v-model="filters.status" 
                @change="filterTasks" 
                class="w-full px-3 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none transition-all text-gray-900 dark:text-white cursor-pointer hover:bg-white dark:hover:bg-gray-700"
              >
                <option value="">All</option>
                <option value="PENDING">Pending</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
                <option value="OVERDUE">Overdue</option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1.5 uppercase tracking-wider">Priority</label>
              <select 
                v-model="filters.priority" 
                @change="filterTasks" 
                class="w-full px-3 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none transition-all text-gray-900 dark:text-white cursor-pointer hover:bg-white dark:hover:bg-gray-700"
              >
                <option value="">All</option>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
            </div>
          </div>

          <div class="col-span-1 md:col-span-2 lg:col-span-2">
            <label class="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1.5 uppercase tracking-wider">Date Range (Due Date)</label>
            <div class="flex items-center gap-2">
              <div class="relative flex-1">
                <input 
                  type="date" 
                  v-model="filters.startDate" 
                  @change="filterTasks"
                  class="w-full px-3 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-gray-900 dark:text-white text-sm"
                >
                <span class="absolute right-8 top-2.5 text-xs text-gray-400 pointer-events-none hidden sm:block">From</span>
              </div>
              <span class="text-gray-400">-</span>
              <div class="relative flex-1">
                <input 
                  type="date" 
                  v-model="filters.endDate" 
                  @change="filterTasks"
                  class="w-full px-3 py-2.5 bg-gray-50 dark:bg-gray-700/50 border border-gray-200 dark:border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 outline-none text-gray-900 dark:text-white text-sm"
                >
                <span class="absolute right-8 top-2.5 text-xs text-gray-400 pointer-events-none hidden sm:block">To</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="flex items-center justify-between mb-6 px-2">
        <h2 class="text-xl font-bold text-gray-800 dark:text-white flex items-center gap-2">
          Task List
          <span class="bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 px-3 py-1 rounded-full text-sm font-medium">
            {{ filteredTasks.length }}
          </span>
        </h2>
      </div>

      <div v-if="filteredTasks.length === 0" class="bg-white dark:bg-gray-800 rounded-2xl p-12 text-center shadow-sm border border-gray-200 dark:border-gray-700">
        <div class="animate-bounce mx-auto h-16 w-16 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">No tasks found</h3>
        <p class="text-gray-500 dark:text-gray-400 mb-6">Try adjusting your search or date filters.</p>
        <button 
          @click="clearFilters" 
          class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2.5 rounded-xl font-medium transition-all duration-200 shadow-lg shadow-blue-500/30 hover:-translate-y-0.5"
        >
          Clear all filters
        </button>
      </div>

      <div v-else class="grid grid-cols-1 gap-4">
        <TransitionGroup name="task-list">
          <div 
            v-for="task in filteredTasks" 
            :key="task.id" 
            class="group bg-white dark:bg-gray-800 rounded-xl p-5 shadow-sm hover:shadow-md border border-gray-200 dark:border-gray-700 transition-all duration-200 relative overflow-hidden"
          >
            <div 
              class="absolute left-0 top-0 bottom-0 w-1.5"
              :class="getPriorityColorClass(task.priority)"
            ></div>

            <div class="pl-3 flex flex-col md:flex-row md:items-center justify-between gap-4">
              
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-3 mb-1">
                  <h3 class="text-lg font-bold text-gray-900 dark:text-white truncate group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                    {{ task.name }}
                  </h3>
                  <span 
                    :class="getPriorityBadgeClass(task.priority)" 
                    class="hidden sm:inline-flex items-center px-2 py-0.5 rounded text-xs font-bold uppercase tracking-wide border"
                  >
                    {{ getPriorityDisplay(task.priority) }}
                  </span>
                </div>
                
                <p class="text-gray-600 dark:text-gray-300 text-sm line-clamp-2 mb-3">
                  {{ task.description || 'No description available.' }}
                </p>
                
                <div class="flex flex-wrap items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                  <div class="flex items-center gap-1.5" :class="{'text-red-500 font-medium': isTaskOverdue(task)}">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                    <span>{{ formatDate(task.endDate) || 'No due date' }}</span>
                  </div>
                  <div v-if="isTaskOverdue(task)" class="flex items-center gap-1 text-red-500 bg-red-50 dark:bg-red-900/20 px-2 py-0.5 rounded text-xs font-bold">
                    <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
                    OVERDUE
                  </div>
                </div>
              </div>

              <div class="flex items-center gap-4 md:border-l md:pl-6 md:border-gray-100 dark:md:border-gray-700">
                <div class="flex flex-col items-end gap-2 w-full md:w-auto">
                  <div class="relative w-full md:w-40">
                    <select 
                      :value="task.status" 
                      @change="changeTaskStatus(task.id, $event.target.value)"
                      :disabled="updatingTasks.includes(task.id)"
                      class="appearance-none w-full pl-3 pr-8 py-1.5 text-sm font-medium rounded-lg border-2 cursor-pointer focus:outline-none transition-colors"
                      :class="getStatusSelectClass(task.status)"
                    >
                      <option value="PENDING">Pending</option>
                      <option value="IN_PROGRESS">In Progress</option>
                      <option value="COMPLETED">Completed</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                    <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none text-gray-500">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                    </div>
                  </div>
                  
                  <button 
                    @click="openTaskDetails(task)"
                    class="text-sm text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-300 font-medium hover:underline flex items-center gap-1"
                  >
                    View details
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path></svg>
                  </button>
                </div>
              </div>

            </div>

            <div v-if="updatingTasks.includes(task.id)" class="absolute inset-0 bg-white/80 dark:bg-gray-800/80 flex items-center justify-center z-10 backdrop-blur-sm">
              <div class="flex items-center gap-2 text-blue-600 font-medium">
                <svg class="animate-spin h-5 w-5" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Updating...
              </div>
            </div>
          </div>
        </TransitionGroup>
      </div>
    </div>

    <Teleport to="body">
      <Transition name="fade">
        <div v-if="selectedTask" class="fixed inset-0 z-[60] flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-black/50 backdrop-blur-sm" @click="closeTaskDetails"></div>
          
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto relative z-10 animate-scale-up">
            
            <div class="p-6 border-b border-gray-100 dark:border-gray-700 flex justify-between items-start sticky top-0 bg-white dark:bg-gray-800 z-10">
              <div>
                <h3 class="text-2xl font-bold text-gray-900 dark:text-white leading-tight mb-2">{{ selectedTask.name }}</h3>
                <div class="flex items-center gap-3">
                  <span 
                    :class="getStatusSelectClass(selectedTask.status)" 
                    class="px-3 py-1 rounded-full text-xs font-bold border"
                  >
                    {{ getStatusDisplay(selectedTask.status) }}
                  </span>
                  <span 
                    :class="getPriorityBadgeClass(selectedTask.priority)" 
                    class="px-3 py-1 rounded-full text-xs font-bold border uppercase"
                  >
                    {{ getPriorityDisplay(selectedTask.priority) }} Priority
                  </span>
                </div>
              </div>
              <button @click="closeTaskDetails" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200 p-1 bg-gray-100 dark:bg-gray-700 rounded-full transition-colors">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
              </button>
            </div>

            <div class="p-8 space-y-8">
              <div>
                <h4 class="text-sm font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-3">Description</h4>
                <div class="bg-gray-50 dark:bg-gray-700/30 rounded-xl p-4 text-gray-700 dark:text-gray-200 leading-relaxed border border-gray-100 dark:border-gray-700/50">
                  {{ selectedTask.description || 'No description provided for this task.' }}
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div>
                  <h4 class="text-sm font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-2">Timeline</h4>
                  <div class="space-y-3">
                    <div class="flex items-center gap-3">
                      <div class="w-8 h-8 rounded-lg bg-blue-50 dark:bg-blue-900/20 flex items-center justify-center text-blue-500">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                      </div>
                      <div>
                        <p class="text-xs text-gray-500 dark:text-gray-400">Due Date</p>
                        <p class="font-medium text-gray-900 dark:text-white">{{ formatDate(selectedTask.endDate) || 'Not set' }}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div>
                   <h4 class="text-sm font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-2">Assignment</h4>
                   <div class="flex items-center gap-3">
                      <div class="w-8 h-8 rounded-lg bg-purple-50 dark:bg-purple-900/20 flex items-center justify-center text-purple-500">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                      </div>
                      <div>
                        <p class="text-xs text-gray-500 dark:text-gray-400">Assigned To</p>
                        <p class="font-medium text-gray-900 dark:text-white">{{ userEmail }}</p>
                      </div>
                   </div>
                </div>
              </div>
            </div>

            <div class="p-6 bg-gray-50 dark:bg-gray-800/50 border-t border-gray-100 dark:border-gray-700 flex justify-end gap-3 rounded-b-2xl">
              <button 
                @click="closeTaskDetails"
                class="px-5 py-2.5 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-200 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-600 font-medium transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <Teleport to="body">
      <Transition name="toast">
        <div v-if="showToast || showErrorToast" class="fixed bottom-6 right-6 z-[70] flex flex-col gap-2">
          <div v-if="showToast" class="bg-white dark:bg-gray-800 border-l-4 border-green-500 shadow-xl rounded-lg p-4 flex items-center gap-3 min-w-[300px] animate-slide-up">
            <div class="text-green-500">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
            </div>
            <div>
              <h4 class="font-bold text-gray-900 dark:text-white text-sm">Success</h4>
              <p class="text-gray-600 dark:text-gray-300 text-sm">{{ toastMessage }}</p>
            </div>
          </div>
          
          <div v-if="showErrorToast" class="bg-white dark:bg-gray-800 border-l-4 border-red-500 shadow-xl rounded-lg p-4 flex items-center gap-3 min-w-[300px] animate-slide-up">
            <div class="text-red-500">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            </div>
            <div>
              <h4 class="font-bold text-gray-900 dark:text-white text-sm">Error</h4>
              <p class="text-gray-600 dark:text-gray-300 text-sm">{{ errorMessage }}</p>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../api' // ✅ CORRECCIÓN 1: Usar instancia 'api' configurada

export default {
  name: 'TaskDash',
  setup() {
    const authStore = useAuthStore()
    const loading = ref(true)
    const error = ref(null)
    const tasks = ref([])
    const filteredTasks = ref([])
    const updatingTasks = ref([])
    const userEmail = ref('')
    
    // Toast state
    const showToast = ref(false)
    const showErrorToast = ref(false)
    const toastMessage = ref('')
    const errorMessage = ref('')
    
    // Modal state
    const selectedTask = ref(null)
    
    const darkMode = ref(false)
    
    const filters = ref({
      status: '',
      priority: '',
      keyword: '',
      startDate: '',
      endDate: ''
    })

    const hasActiveFilters = computed(() => {
      return filters.value.status || 
             filters.value.priority || 
             filters.value.keyword || 
             filters.value.startDate || 
             filters.value.endDate
    })

    const checkSystemTheme = () => {
      darkMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches
      updateThemeClass()
    }

    const toggleTheme = () => {
      darkMode.value = !darkMode.value
      updateThemeClass()
      localStorage.setItem('darkMode', darkMode.value)
    }

    const updateThemeClass = () => {
      if (darkMode.value) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
    }

    const loadTasks = async () => {
      try {
        loading.value = true
        
        // ✅ CORRECCIÓN 2: Obtener email correctamente del objeto 'user' guardado
        let email = authStore.user?.email
        if (!email) {
            const userStr = localStorage.getItem('user')
            if (userStr) {
                const userData = JSON.parse(userStr)
                email = userData.email || userData.username
            }
        }
        userEmail.value = email || 'Unknown User'

        if (!email) throw new Error('Could not get authenticated user')

        // ✅ CORRECCIÓN 3: Usar 'api.get' y quitar '/api' del path (porque ya está en baseURL)
        const response = await api.get(`/tasks/assigned-tasks/${email}`)
        
        // El backend devuelve { user: "...", assignedTasks: [...] }
        tasks.value = response.data.assignedTasks || []
        
        filterTasks() 
      } catch (err) {
        error.value = 'Error loading tasks: ' + (err.message || 'Unknown error')
        console.error(err)
        showError('Error loading tasks')
      } finally {
        loading.value = false
      }
    }

    const changeTaskStatus = async (taskId, newStatus) => {
      if (updatingTasks.value.includes(taskId)) return
      updatingTasks.value.push(taskId)
      
      try {
        // ✅ CORRECCIÓN 4: Usar 'api.put' y ruta correcta
        await api.put(`/tasks/${taskId}/status`, null, {
          params: { status: newStatus }
        })
        
        const taskIndex = tasks.value.findIndex(t => t.id === taskId)
        if (taskIndex !== -1) {
          tasks.value[taskIndex].status = newStatus
          
          if (selectedTask.value && selectedTask.value.id === taskId) {
            selectedTask.value.status = newStatus
          }
        }
        
        filterTasks()
        showSuccess(`Status updated to ${getStatusDisplay(newStatus)}`)
      } catch (err) {
        console.error('Error changing status:', err)
        showError('Error changing status')
      } finally {
        updatingTasks.value = updatingTasks.value.filter(id => id !== taskId)
      }
    }

    const filterTasks = () => {
      filteredTasks.value = tasks.value.filter(task => {
        // 1. Status Filter
        const statusMatch = !filters.value.status || 
                          (filters.value.status === 'OVERDUE' 
                            ? isTaskOverdue(task)
                            : task.status === filters.value.status)
        
        // 2. Priority Filter
        const priorityMatch = !filters.value.priority || task.priority === filters.value.priority
        
        // 3. Keyword Filter
        const keywordMatch = !filters.value.keyword ||
          task.name.toLowerCase().includes(filters.value.keyword.toLowerCase()) ||
          (task.description && task.description.toLowerCase().includes(filters.value.keyword.toLowerCase()))

        // 4. Date Range Filter
        let dateMatch = true
        if (filters.value.startDate || filters.value.endDate) {
          if (!task.endDate) {
            dateMatch = false 
          } else {
            const taskDate = new Date(task.endDate).setHours(0,0,0,0)
            const startDate = filters.value.startDate ? new Date(filters.value.startDate).setHours(0,0,0,0) : null
            const endDate = filters.value.endDate ? new Date(filters.value.endDate).setHours(0,0,0,0) : null

            if (startDate && taskDate < startDate) dateMatch = false
            if (endDate && taskDate > endDate) dateMatch = false
          }
        }

        return statusMatch && priorityMatch && keywordMatch && dateMatch
      })
    }

    const clearFilters = () => {
      filters.value = {
        status: '',
        priority: '',
        keyword: '',
        startDate: '',
        endDate: ''
      }
      filterTasks()
    }

    const isTaskOverdue = (task) => {
      return task.endDate && new Date(task.endDate) < new Date() && task.status !== 'COMPLETED'
    }

    const openTaskDetails = (task) => {
      selectedTask.value = { ...task } 
    }

    const closeTaskDetails = () => {
      selectedTask.value = null
    }

    const getStatusDisplay = (status) => {
      const map = {
        'PENDING': 'Pending',
        'IN_PROGRESS': 'In Progress',
        'COMPLETED': 'Completed',
        'CANCELLED': 'Cancelled'
      }
      return map[status] || status
    }

    const getPriorityDisplay = (priority) => {
      const map = {
        'LOW': 'Low',
        'MEDIUM': 'Medium',
        'HIGH': 'High',
        'URGENT': 'Urgent'
      }
      return map[priority] || priority
    }

    const getPriorityColorClass = (priority) => {
      const classes = {
        'LOW': 'bg-green-500',
        'MEDIUM': 'bg-yellow-500',
        'HIGH': 'bg-orange-500',
        'URGENT': 'bg-red-500'
      }
      return classes[priority] || 'bg-gray-300'
    }

    const getPriorityBadgeClass = (priority) => {
       const classes = {
        'LOW': 'bg-green-50 text-green-700 border-green-200 dark:bg-green-900/30 dark:text-green-300 dark:border-green-800',
        'MEDIUM': 'bg-yellow-50 text-yellow-700 border-yellow-200 dark:bg-yellow-900/30 dark:text-yellow-300 dark:border-yellow-800',
        'HIGH': 'bg-orange-50 text-orange-700 border-orange-200 dark:bg-orange-900/30 dark:text-orange-300 dark:border-orange-800',
        'URGENT': 'bg-red-50 text-red-700 border-red-200 dark:bg-red-900/30 dark:text-red-300 dark:border-red-800'
      }
      return classes[priority] || 'bg-gray-100 text-gray-700'
    }

    const getStatusSelectClass = (status) => {
       const classes = {
        'PENDING': 'bg-yellow-50 border-yellow-200 text-yellow-800 dark:bg-yellow-900/20 dark:border-yellow-800 dark:text-yellow-200',
        'IN_PROGRESS': 'bg-blue-50 border-blue-200 text-blue-800 dark:bg-blue-900/20 dark:border-blue-800 dark:text-blue-200',
        'COMPLETED': 'bg-green-50 border-green-200 text-green-800 dark:bg-green-900/20 dark:border-green-800 dark:text-green-200',
        'CANCELLED': 'bg-red-50 border-red-200 text-red-800 dark:bg-red-900/20 dark:border-red-800 dark:text-red-200'
      }
      return classes[status] || 'bg-gray-50 border-gray-200'
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('en-US', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      })
    }

    const showSuccess = (message) => {
      toastMessage.value = message
      showToast.value = true
      setTimeout(() => showToast.value = false, 4000)
    }

    const showError = (message) => {
      errorMessage.value = message
      showErrorToast.value = true
      setTimeout(() => showErrorToast.value = false, 4000)
    }

    onMounted(async () => {
      const savedMode = localStorage.getItem('darkMode')
      if (savedMode !== null) darkMode.value = savedMode === 'true'
      else checkSystemTheme()
      updateThemeClass()
      
      await loadTasks()
    })

    return {
      // State
      loading, tasks, filteredTasks, updatingTasks, filters, darkMode, authStore,
      showToast, showErrorToast, toastMessage, errorMessage, hasActiveFilters,
      selectedTask, userEmail,
      
      // Actions
      loadTasks, changeTaskStatus, filterTasks, clearFilters, isTaskOverdue, 
      openTaskDetails, closeTaskDetails, toggleTheme,
      
      // UI Helpers
      getStatusDisplay, getPriorityDisplay, getPriorityColorClass, 
      getPriorityBadgeClass, getStatusSelectClass, formatDate
    }
  }
}
</script>

<style scoped>
/* List Transitions */
.task-list-enter-active,
.task-list-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.task-list-enter-from,
.task-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.task-list-move {
  transition: transform 0.4s ease;
}

/* Toast Animation */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* Modal Animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.animate-scale-up {
  animation: scaleUp 0.3s ease-out forwards;
}

@keyframes scaleUp {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>