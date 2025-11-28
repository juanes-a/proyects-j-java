<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
    <!-- Header with glass effect -->
    <div class="bg-white/80 dark:bg-gray-800/80 backdrop-blur-lg shadow-sm border-b border-gray-200 dark:border-gray-700">
      <div class="max-w-7xl mx-auto px-4 py-6">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Task Dashboard</h1>
            <p class="text-gray-600 dark:text-gray-300">Manage and update your assigned tasks</p>
          </div>
          <button @click="toggleTheme" class="p-2 rounded-full bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors">
            <svg v-if="darkMode" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path>
            </svg>
            <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- Filters with elevation effect -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300 p-6 mb-8 border border-gray-200 dark:border-gray-700">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Filters</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
            <select 
              v-model="filters.status" 
              @change="filterTasks" 
              class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white transition-all duration-200 hover:border-gray-400 dark:hover:border-gray-500"
            >
              <option value="">All statuses</option>
              <option value="PENDING">Pending</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
              <option value="OVERDUE">Overdue</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Priority</label>
            <select 
              v-model="filters.priority" 
              @change="filterTasks" 
              class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white transition-all duration-200 hover:border-gray-400 dark:hover:border-gray-500"
            >
              <option value="">All priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="URGENT">Urgent</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Search</label>
            <div class="relative">
              <input 
                type="text" 
                v-model="filters.keyword" 
                @input="filterTasks" 
                placeholder="Search tasks..." 
                class="w-full px-3 py-2 pr-10 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white transition-all duration-200 hover:border-gray-400 dark:hover:border-gray-500"
              >
              <svg class="absolute right-3 top-2.5 h-5 w-5 text-gray-400 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>
        </div>
        <button 
          @click="clearFilters" 
          class="mt-4 px-4 py-2 text-sm text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white underline transition-colors duration-200"
        >
          Clear filters
        </button>
      </div>

      <!-- Task list with interactive cards effect -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow duration-300 overflow-hidden border border-gray-200 dark:border-gray-700">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Assigned Tasks</h2>
            <span class="bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 px-3 py-1 rounded-full text-sm">
              {{ filteredTasks.length }} {{ filteredTasks.length === 1 ? 'task' : 'tasks' }}
            </span>
          </div>
        </div>

        <!-- Empty state with animation -->
        <div v-if="filteredTasks.length === 0" class="text-center py-16">
          <div class="animate-bounce mx-auto h-12 w-12 text-gray-400 mb-4">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
            </svg>
          </div>
          <p class="text-gray-500 dark:text-gray-400 text-lg mb-4">No tasks found</p>
          <button 
            @click="clearFilters" 
            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition-colors duration-200 shadow-md hover:shadow-lg transform hover:-translate-y-0.5"
          >
            Clear filters
          </button>
        </div>

        <!-- Task list with animations -->
        <div v-else class="divide-y divide-gray-200 dark:divide-gray-700">
          <TransitionGroup name="task-list">
            <div 
              v-for="task in filteredTasks" 
              :key="task.id" 
              class="p-6 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-all duration-300 group"
              @mouseenter="hoveredTask = task.id"
              @mouseleave="hoveredTask = null"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <div class="flex items-start justify-between mb-2">
                    <h3 class="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors duration-200">
                      {{ task.name }}
                    </h3>
                    <div class="flex items-center space-x-2 ml-4">
                      <span 
                        :class="getPriorityClass(task.priority)" 
                        class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium transition-all duration-200 group-hover:scale-105"
                      >
                        <svg :class="getPriorityIcon(task.priority)" class="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.293l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L9 9.414V13a1 1 0 102 0V9.414l1.293 1.293a1 1 0 001.414-1.414z" clip-rule="evenodd"></path>
                        </svg>
                        {{ getPriorityDisplay(task.priority) }}
                      </span>
                    </div>
                  </div>
                  
                  <p class="text-gray-600 dark:text-gray-300 mb-3 transition-colors duration-200 group-hover:text-gray-700 dark:group-hover:text-gray-200">
                    {{ task.description || 'No description' }}
                  </p>
                  
                  <div class="flex items-center space-x-4 text-sm text-gray-500 dark:text-gray-400">
                    <div class="flex items-center space-x-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                      </svg>
                      <span>{{ formatDate(task.endDate) || 'No due date' }}</span>
                    </div>
                    <div v-if="isTaskOverdue(task)" class="flex items-center space-x-1 text-red-500 dark:text-red-400 animate-pulse">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                      </svg>
                      <span class="font-medium">Overdue</span>
                    </div>
                  </div>
                </div>
                
                <!-- Quick action button that appears on hover -->
                <Transition name="fade">
                  <button 
                    v-if="hoveredTask === task.id"
                    @click="openTaskDetails(task)"
                    class="ml-4 p-2 rounded-full bg-blue-100 dark:bg-blue-900/50 text-blue-600 dark:text-blue-400 hover:bg-blue-200 dark:hover:bg-blue-800 transition-colors duration-200 shadow-sm"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                    </svg>
                  </button>
                </Transition>
              </div>

              <!-- Enhanced status section with animations -->
              <div class="mt-6 bg-gradient-to-r from-gray-50 to-gray-100 dark:from-gray-700/50 dark:to-gray-800/50 rounded-xl p-4 border border-gray-200 dark:border-gray-700 transition-all duration-300 group-hover:shadow-sm">
                <div class="flex items-center justify-between">
                  <div class="flex items-center space-x-3">
                    <div class="flex items-center space-x-2">
                      <svg class="w-5 h-5 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                      </svg>
                      <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Current status:</span>
                    </div>
                    <div class="relative">
                      <span 
                        :class="[getStatusClass(task.status), 'transform transition-all duration-300 group-hover:scale-105']" 
                        class="inline-flex items-center px-3 py-1.5 rounded-full text-sm font-medium shadow-sm"
                      >
                        <div class="w-2 h-2 rounded-full bg-current opacity-70 mr-2 animate-pulse"></div>
                        {{ getStatusDisplay(task.status) }}
                      </span>
                    </div>
                  </div>
                  
                  <div class="flex items-center space-x-3">
                    <div class="flex items-center space-x-2">
                      <svg class="w-5 h-5 text-blue-500 dark:text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"></path>
                      </svg>
                      <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Change to:</span>
                    </div>
                    
                    <div class="relative">
                      <select 
                        :value="task.status" 
                        @change="changeTaskStatus(task.id, $event.target.value)"
                        :disabled="updatingTasks.includes(task.id)"
                        class="appearance-none bg-white dark:bg-gray-700 border-2 border-gray-300 dark:border-gray-600 rounded-lg px-4 py-2 pr-10 text-sm font-medium focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 hover:border-gray-400 dark:hover:border-gray-500 hover:shadow-sm text-gray-900 dark:text-white"
                      >
                        <option value="PENDING">Pending</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED">Cancelled</option>
                      </select>
                      <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <svg class="w-4 h-4 text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Enhanced loading indicator with animation -->
                <Transition name="slide-fade">
                  <div v-if="updatingTasks.includes(task.id)" class="mt-4 flex items-center justify-center space-x-3 bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3 border border-blue-200 dark:border-blue-800">
                    <div class="relative">
                      <div class="w-6 h-6 border-2 border-blue-200 dark:border-blue-700 rounded-full"></div>
                      <div class="absolute top-0 left-0 w-6 h-6 border-2 border-blue-600 dark:border-blue-400 rounded-full animate-spin border-t-transparent"></div>
                    </div>
                    <div class="flex flex-col">
                      <span class="text-sm font-medium text-blue-700 dark:text-blue-400">Updating status...</span>
                      <div class="w-32 h-1 bg-blue-200 dark:bg-blue-800 rounded-full mt-1 overflow-hidden">
                        <div class="h-full bg-gradient-to-r from-blue-500 to-blue-600 dark:from-blue-400 dark:to-blue-500 rounded-full animate-pulse"></div>
                      </div>
                    </div>
                  </div>
                </Transition>
              </div>
            </div>
          </TransitionGroup>
        </div>
      </div>
    </div>

    <!-- Enhanced notification toast with animation -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="transform translate-y-2 opacity-0 scale-95"
      enter-to-class="transform translate-y-0 opacity-100 scale-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="transform translate-y-0 opacity-100 scale-100"
      leave-to-class="transform translate-y-2 opacity-0 scale-95"
    >
      <div v-if="showToast" class="fixed bottom-6 right-6 bg-gradient-to-r from-green-500 to-green-600 text-white px-6 py-4 rounded-xl shadow-2xl z-50 max-w-sm">
        <div class="flex items-center space-x-3">
          <div class="flex-shrink-0">
            <div class="w-6 h-6 bg-white bg-opacity-20 rounded-full flex items-center justify-center animate-pulse">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
          </div>
          <div class="flex-1">
            <p class="font-medium">{{ toastMessage }}</p>
          </div>
          <button @click="showToast = false" class="flex-shrink-0 text-white hover:text-gray-200 transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </Transition>

    <!-- Enhanced error toast with animation -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="transform translate-y-2 opacity-0 scale-95"
      enter-to-class="transform translate-y-0 opacity-100 scale-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="transform translate-y-0 opacity-100 scale-100"
      leave-to-class="transform translate-y-2 opacity-0 scale-95"
    >
      <div v-if="showErrorToast" class="fixed bottom-6 right-6 bg-gradient-to-r from-red-500 to-red-600 text-white px-6 py-4 rounded-xl shadow-2xl z-50 max-w-sm">
        <div class="flex items-center space-x-3">
          <div class="flex-shrink-0">
            <div class="w-6 h-6 bg-white bg-opacity-20 rounded-full flex items-center justify-center animate-pulse">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <div class="flex-1">
            <p class="font-medium">{{ errorMessage }}</p>
          </div>
          <button @click="showErrorToast = false" class="flex-shrink-0 text-white hover:text-gray-200 transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import axios from 'axios'

export default {
  name: 'TaskDash',
  setup() {
    const authStore = useAuthStore()
    const loading = ref(true)
    const error = ref(null)
    const tasks = ref([])
    const filteredTasks = ref([])
    const updatingTasks = ref([])
    const showToast = ref(false)
    const showErrorToast = ref(false)
    const toastMessage = ref('')
    const errorMessage = ref('')
    const hoveredTask = ref(null)
    const darkMode = ref(false)
    
    const filters = ref({
      status: '',
      priority: '',
      keyword: ''
    })

    // Check system theme preference
    const checkSystemTheme = () => {
      darkMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches
      updateThemeClass()
    }

    // Toggle light/dark theme
    const toggleTheme = () => {
      darkMode.value = !darkMode.value
      updateThemeClass()
      localStorage.setItem('darkMode', darkMode.value)
    }

    // Update theme class on document
    const updateThemeClass = () => {
      if (darkMode.value) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
    }

    // Load tasks
    const loadTasks = async () => {
      try {
        loading.value = true
        const usernameOrEmail = authStore.user?.email || localStorage.getItem('userEmail')
        if (!usernameOrEmail) throw new Error('Could not get authenticated user')

        const response = await axios.get(`/api/tasks/assigned-tasks/${usernameOrEmail}`)
        tasks.value = response.data.assignedTasks || []
        filteredTasks.value = [...tasks.value]
      } catch (err) {
        error.value = 'Error loading tasks: ' + err.message
        showError('Error loading tasks')
      } finally {
        loading.value = false
      }
    }

    // Change task status
    const changeTaskStatus = async (taskId, newStatus) => {
      if (updatingTasks.value.includes(taskId)) return
      
      updatingTasks.value.push(taskId)
      
      try {
        const response = await axios.put(`/api/tasks/${taskId}/status`, null, {
          params: { status: newStatus }
        })
        
        // Update task in local state
        const taskIndex = tasks.value.findIndex(t => t.id === taskId)
        if (taskIndex !== -1) {
          tasks.value[taskIndex].status = newStatus
        }
        
        // Re-filter tasks
        filterTasks()
        
        showSuccess(`Status changed to ${getStatusDisplay(newStatus)}`)
        
      } catch (err) {
        console.error('Error changing status:', err)
        showError('Error changing task status')
      } finally {
        updatingTasks.value = updatingTasks.value.filter(id => id !== taskId)
      }
    }

    // Filter tasks
    const filterTasks = () => {
      filteredTasks.value = tasks.value.filter(task => {
        const statusMatch = !filters.value.status || 
                          (filters.value.status === 'OVERDUE' 
                            ? isTaskOverdue(task)
                            : task.status === filters.value.status)
        const priorityMatch = !filters.value.priority || task.priority === filters.value.priority
        const keywordMatch = !filters.value.keyword ||
          task.name.toLowerCase().includes(filters.value.keyword.toLowerCase()) ||
          (task.description && task.description.toLowerCase().includes(filters.value.keyword.toLowerCase()))
        return statusMatch && priorityMatch && keywordMatch
      })
    }

    // Clear filters
    const clearFilters = () => {
      filters.value = {
        status: '',
        priority: '',
        keyword: ''
      }
      filterTasks()
    }

    // Check if task is overdue
    const isTaskOverdue = (task) => {
      return task.endDate && new Date(task.endDate) < new Date() && task.status !== 'COMPLETED'
    }

    // Open task details (simulated)
    const openTaskDetails = (task) => {
      showSuccess(`Opening details for "${task.name}"`)
      // Here you could implement a modal or navigation to the details view
    }

    // Show status text
    const getStatusDisplay = (status) => {
      const statusMap = {
        'PENDING': 'Pending',
        'IN_PROGRESS': 'In Progress',
        'COMPLETED': 'Completed',
        'CANCELLED': 'Cancelled'
      }
      return statusMap[status] || status
    }

    // Get CSS class for status
    const getStatusClass = (status) => {
      const statusClasses = {
        'PENDING': 'bg-yellow-100 text-yellow-800 border-yellow-300 dark:bg-yellow-900/50 dark:text-yellow-200 dark:border-yellow-700',
        'IN_PROGRESS': 'bg-blue-100 text-blue-800 border-blue-300 dark:bg-blue-900/50 dark:text-blue-200 dark:border-blue-700',
        'COMPLETED': 'bg-green-100 text-green-800 border-green-300 dark:bg-green-900/50 dark:text-green-200 dark:border-green-700',
        'CANCELLED': 'bg-red-100 text-red-800 border-red-300 dark:bg-red-900/50 dark:text-red-200 dark:border-red-700'
      }
      return statusClasses[status] || 'bg-gray-100 text-gray-800 border-gray-300 dark:bg-gray-700 dark:text-gray-200 dark:border-gray-600'
    }

    // Show priority text
    const getPriorityDisplay = (priority) => {
      const priorityMap = {
        'LOW': 'Low',
        'MEDIUM': 'Medium',
        'HIGH': 'High',
        'URGENT': 'Urgent'
      }
      return priorityMap[priority] || priority
    }

    // Get CSS class for priority
    const getPriorityClass = (priority) => {
      const priorityClasses = {
        'LOW': 'bg-green-100 text-green-800 dark:bg-green-900/50 dark:text-green-200',
        'MEDIUM': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/50 dark:text-yellow-200',
        'HIGH': 'bg-orange-100 text-orange-800 dark:bg-orange-900/50 dark:text-orange-200',
        'URGENT': 'bg-red-100 text-red-800 dark:bg-red-900/50 dark:text-red-200'
      }
      return priorityClasses[priority] || 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    }

    // Get icon for priority
    const getPriorityIcon = (priority) => {
      const icons = {
        'LOW': 'text-green-500 dark:text-green-400',
        'MEDIUM': 'text-yellow-500 dark:text-yellow-400',
        'HIGH': 'text-orange-500 dark:text-orange-400',
        'URGENT': 'text-red-500 dark:text-red-400'
      }
      return icons[priority] || 'text-gray-500 dark:text-gray-400'
    }

    // Format date
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('en-US', {
        day: 'numeric',
        month: 'short',
        year: 'numeric'
      })
    }

    // Show success notification
    const showSuccess = (message) => {
      toastMessage.value = message
      showToast.value = true
      setTimeout(() => {
        showToast.value = false
      }, 4000)
    }

    // Show error notification
    const showError = (message) => {
      errorMessage.value = message
      showErrorToast.value = true
      setTimeout(() => {
        showErrorToast.value = false
      }, 4000)
    }

    // Initialize component
    onMounted(async () => {
      // Check theme saved in localStorage
      const savedMode = localStorage.getItem('darkMode')
      if (savedMode !== null) {
        darkMode.value = savedMode === 'true'
      } else {
        checkSystemTheme()
      }
      updateThemeClass()
      
      await loadTasks()
    })

    return {
      loading,
      error,
      tasks,
      filteredTasks,
      updatingTasks,
      showToast,
      showErrorToast,
      toastMessage,
      errorMessage,
      filters,
      hoveredTask,
      darkMode,
      loadTasks,
      changeTaskStatus,
      filterTasks,
      clearFilters,
      isTaskOverdue,
      openTaskDetails,
      getStatusDisplay,
      getStatusClass,
      getPriorityDisplay,
      getPriorityClass,
      getPriorityIcon,
      formatDate,
      showSuccess,
      showError,
      toggleTheme
    }
  }
}
</script>

<style>
/* Custom animations */
.task-list-move, /* apply transition to moving elements */
.task-list-enter-active,
.task-list-leave-active {
  transition: all 0.5s ease;
}

.task-list-enter-from,
.task-list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.task-list-leave-active {
  position: absolute;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(10px);
  opacity: 0;
}
</style>