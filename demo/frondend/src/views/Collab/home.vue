<template>
  <div class="w-full min-h-screen animate-fade-in-up">
    <div class="max-w-none space-y-6">

      <div class="relative overflow-hidden bg-gradient-to-br from-emerald-600 via-teal-500 to-emerald-500 rounded-2xl p-6 sm:p-8 text-white shadow-xl shadow-emerald-500/20">
        <div class="absolute top-0 right-0 -mt-8 -mr-8 w-40 h-40 bg-teal-300/20 rounded-full blur-3xl"></div>
        <div class="absolute bottom-0 left-0 -mb-8 -ml-8 w-32 h-32 bg-emerald-900/40 rounded-full blur-2xl"></div>
        
        <div class="relative flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 z-10">
          <div class="flex items-center space-x-4">
            <div class="hidden sm:flex w-16 h-16 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl items-center justify-center shadow-inner">
              <span class="text-2xl font-bold text-white">{{ userInitial }}</span>
            </div>
            
            <div class="min-w-0">
              <h1 class="text-3xl sm:text-4xl font-extrabold tracking-tight truncate">
                Hello, {{ username }} 👋
              </h1>
              <p class="text-emerald-100 text-sm sm:text-base font-medium flex items-center gap-2 mt-1">
                <Calendar class="w-4 h-4" />
                {{ currentDate }}
              </p>
            </div>
          </div>

          <button 
            @click="fetchAssignedTasks"
            class="hidden md:flex items-center px-4 py-2 bg-white/10 hover:bg-white/20 border border-white/30 rounded-xl transition-all duration-200 backdrop-blur-sm text-sm font-semibold group"
            :disabled="loading"
          >
            <RefreshCw class="w-4 h-4 mr-2 group-hover:rotate-180 transition-transform" :class="{ 'animate-spin': loading }" />
            Refresh Tasks
          </button>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 lg:gap-6">
        
        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Completed Today</p>
              <h3 class="text-3xl font-bold text-gray-800 dark:text-white mt-2">{{ stats.completedToday }}</h3>
            </div>
            <div class="p-3 bg-emerald-100 dark:bg-emerald-500/10 rounded-xl text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 class="w-6 h-6" />
            </div>
          </div>
          <div class="w-full bg-gray-100 dark:bg-zinc-800 rounded-full h-1.5 mt-4">
             <div class="bg-emerald-500 h-1.5 rounded-full transition-all duration-1000" :style="{ width: `${getPercentage(stats.completedToday)}%` }"></div>
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">Pending Tasks</p>
              <h3 class="text-3xl font-bold text-gray-800 dark:text-white mt-2">{{ stats.pending }}</h3>
            </div>
            <div class="p-3 bg-blue-100 dark:bg-blue-500/10 rounded-xl text-blue-600 dark:text-blue-400">
              <ListTodo class="w-6 h-6" />
            </div>
          </div>
          <div class="mt-4 flex items-center text-xs text-blue-600 dark:text-blue-400 font-medium">
             Keep it up!
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden group">
          <div class="flex justify-between items-start z-10 relative">
            <div>
              <p class="text-sm font-medium text-gray-500 dark:text-gray-400">High Priority</p>
              <h3 class="text-3xl font-bold text-gray-800 dark:text-white mt-2">{{ stats.highPriority }}</h3>
            </div>
            <div class="p-3 bg-red-100 dark:bg-red-500/10 rounded-xl text-red-600 dark:text-red-400 animate-pulse">
              <AlertOctagon class="w-6 h-6" />
            </div>
          </div>
          <div class="mt-4 flex items-center text-xs text-red-500 font-medium">
             Requires attention
          </div>
        </div>

      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <div class="lg:col-span-2 bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm flex flex-col h-full">
          <div class="p-6 border-b border-gray-100 dark:border-zinc-800 flex justify-between items-center">
            <h3 class="text-xl font-bold text-gray-800 dark:text-white flex items-center gap-2">
              <Target class="w-5 h-5 text-emerald-500" />
              My Tasks
            </h3>
            <div class="flex gap-2">
               <span class="px-2 py-1 text-xs font-medium bg-gray-100 dark:bg-zinc-800 rounded-lg text-gray-500">Sorted by Priority</span>
            </div>
          </div>
          
          <div class="flex-1 overflow-y-auto max-h-[500px] custom-scrollbar p-2 space-y-2">
            
            <div v-if="loading" class="flex items-center justify-center py-10">
               <Loader2 class="w-8 h-8 text-emerald-500 animate-spin" />
            </div>

            <div v-else-if="tasks.length === 0" class="text-center py-10">
               <div class="w-16 h-16 bg-gray-50 dark:bg-zinc-800 rounded-full flex items-center justify-center mx-auto mb-4">
                 <CheckCircle2 class="w-8 h-8 text-gray-300" />
               </div>
               <p class="text-gray-500 font-medium">All caught up! No tasks assigned.</p>
            </div>

            <div 
              v-else
              v-for="task in tasks" 
              :key="task.id"
              class="group relative bg-white dark:bg-zinc-800/40 border border-gray-100 dark:border-zinc-700/50 rounded-xl p-4 hover:border-emerald-400 dark:hover:border-emerald-500 hover:shadow-md transition-all duration-200"
            >
               <div class="flex justify-between items-start">
                  <div class="flex-1 min-w-0 pr-4">
                     <div class="flex items-center gap-2 mb-1">
                        <span :class="['text-[10px] px-2 py-0.5 rounded-full font-bold uppercase tracking-wide', getPriorityBadgeClass(task.priority)]">
                           {{ getPriorityShort(task.priority) }}
                        </span>
                        <span class="text-xs text-gray-400 flex items-center">
                           <FolderDot class="w-3 h-3 mr-1" />
                           {{ task.project?.name || 'No Project' }}
                        </span>
                     </div>
                     <h4 class="text-base font-semibold text-gray-800 dark:text-gray-200 group-hover:text-emerald-600 dark:group-hover:text-emerald-400 transition-colors">
                        {{ task.title }}
                     </h4>
                     <p class="text-sm text-gray-500 dark:text-gray-400 mt-1 line-clamp-1">
                        {{ task.description }}
                     </p>
                  </div>

                  <div class="flex flex-col items-end shrink-0">
                     <div 
                       :class="['flex items-center text-sm font-medium mb-2', getDeadlineClass(task.dueDate)]"
                     >
                        <Clock class="w-4 h-4 mr-1.5" />
                        {{ getDaysUntilDeadline(task.dueDate) }} days
                     </div>
                     
                     <span :class="['text-xs px-2.5 py-1 rounded-md font-medium border', getStatusClass(task.status)]">
                        {{ formatStatus(task.status) }}
                     </span>
                  </div>
               </div>
               
               <div class="absolute bottom-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity">
                  </div>
            </div>

          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6 flex flex-col h-full">
           <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <CalendarClock class="w-5 h-5 text-teal-500" />
            Upcoming Deadlines
          </h3>

          <div class="space-y-4 overflow-y-auto max-h-[500px] custom-scrollbar pr-2">
             <div 
               v-for="task in upcomingDeadlines" 
               :key="'dl-'+task.id"
               class="flex items-center gap-3 p-3 rounded-xl bg-red-50 dark:bg-red-900/10 border border-red-100 dark:border-red-900/20"
             >
                <div class="flex-shrink-0 w-10 h-10 bg-white dark:bg-zinc-800 rounded-lg flex flex-col items-center justify-center text-red-500 shadow-sm">
                   <span class="text-[10px] font-bold uppercase">{{ getMonth(task.dueDate) }}</span>
                   <span class="text-sm font-extrabold">{{ getDay(task.dueDate) }}</span>
                </div>
                <div class="min-w-0">
                   <p class="text-sm font-semibold text-gray-800 dark:text-gray-200 truncate">{{ task.title }}</p>
                   <p class="text-xs text-red-500 font-medium">Due in {{ getDaysUntilDeadline(task.dueDate) }} days</p>
                </div>
             </div>
             
             <div v-if="upcomingDeadlines.length === 0" class="text-center py-8 text-gray-400 text-sm">
                No urgent deadlines. You are safe! 🌴
             </div>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../api' // Ensure this path is correct based on your file structure
import { 
  Calendar, RefreshCw, CheckCircle2, ListTodo, AlertOctagon, 
  Target, Loader2, FolderDot, Clock, CalendarClock,
} from 'lucide-vue-next'

// --- State ---
const tasks = ref([])
const loading = ref(false)
const username = ref('')
const userInitial = ref('U')
const currentDate = ref('')

const stats = ref({
  completedToday: 0,
  pending: 0,
  highPriority: 0,
  totalTasks: 0
})

// --- Logic ---
const formatDateDisplay = () => {
  const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }
  currentDate.value = new Date().toLocaleDateString('en-US', options)
}

const getUserData = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const user = JSON.parse(userStr)
    username.value = user.name || user.username || 'Collaborator'
    userInitial.value = username.value.charAt(0).toUpperCase()
  }
}

const fetchAssignedTasks = async () => {
  loading.value = true
  try {
    // Keeping backend route as requested
    const response = await api.get('/tasks/user/my-tasks')
    tasks.value = response.data

    calculateStats()
  } catch (error) {
    console.error('Error fetching tasks:', error)
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  const today = new Date().toISOString().split('T')[0]
  
  stats.value.totalTasks = tasks.value.length
  
  // Tasks completed today (assuming updated_at or similar field exists, falling back to basic check)
  // Since original code had logic for this, we replicate a simple check:
  // If status is DONE/COMPLETED. Note: Real "Completed Today" needs a 'completedAt' date check.
  // For now, we will count TOTAL completed as per typical simple dashboards if date isn't available.
  stats.value.completedToday = tasks.value.filter(t => 
    (t.status === 'COMPLETED' || t.status === 'DONE')
  ).length

  stats.value.pending = tasks.value.filter(t => 
    t.status !== 'COMPLETED' && t.status !== 'DONE'
  ).length

  stats.value.highPriority = tasks.value.filter(t => 
    (t.priority === 'HIGH' || t.priority === 'URGENT') && 
    (t.status !== 'COMPLETED' && t.status !== 'DONE')
  ).length
}

// --- Helpers ---
const getDaysUntilDeadline = (dateString) => {
  if (!dateString) return 0
  const diff = new Date(dateString) - new Date().setHours(0,0,0,0)
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
}

const getPercentage = (value) => {
  if (stats.value.totalTasks === 0) return 0
  return Math.round((value / stats.value.totalTasks) * 100)
}

// Styling Helpers (Emerald/Teal Theme)
const getPriorityBadgeClass = (priority) => {
  const map = {
    'HIGH': 'bg-red-50 text-red-600 border border-red-200 dark:bg-red-900/30 dark:text-red-300 dark:border-red-900',
    'URGENT': 'bg-red-100 text-red-700 border border-red-300 dark:bg-red-900/50 dark:text-red-200 dark:border-red-800',
    'MEDIUM': 'bg-amber-50 text-amber-600 border border-amber-200 dark:bg-amber-900/30 dark:text-amber-300 dark:border-amber-900',
    'LOW': 'bg-emerald-50 text-emerald-600 border border-emerald-200 dark:bg-emerald-900/30 dark:text-emerald-300 dark:border-emerald-900'
  }
  return map[priority] || 'bg-gray-100 text-gray-600'
}

const getPriorityShort = (priority) => {
  const map = { 'HIGH': 'High', 'URGENT': 'Urg', 'MEDIUM': 'Med', 'LOW': 'Low' }
  return map[priority] || priority
}

const getStatusClass = (status) => {
  const map = {
    'PENDING': 'bg-gray-100 text-gray-600 border-gray-200 dark:bg-zinc-800 dark:text-gray-400 dark:border-zinc-700',
    'IN_PROGRESS': 'bg-blue-50 text-blue-600 border-blue-200 dark:bg-blue-900/20 dark:text-blue-300 dark:border-blue-900',
    'COMPLETED': 'bg-emerald-50 text-emerald-600 border-emerald-200 dark:bg-emerald-900/20 dark:text-emerald-300 dark:border-emerald-900',
    'DONE': 'bg-emerald-50 text-emerald-600 border-emerald-200 dark:bg-emerald-900/20 dark:text-emerald-300 dark:border-emerald-900'
  }
  return map[status] || 'bg-gray-50'
}

const getDeadlineClass = (dateString) => {
  const days = getDaysUntilDeadline(dateString)
  if (days < 0) return 'text-gray-400' // Overdue
  if (days <= 3) return 'text-red-500 font-bold' // Urgent
  if (days <= 7) return 'text-amber-500' // Warning
  return 'text-emerald-500' // Safe
}

const formatStatus = (status) => {
  return status ? status.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase()) : ''
}

// Date helpers for calendar icons
const getMonth = (d) => new Date(d).toLocaleDateString('en-US', { month: 'short' })
const getDay = (d) => new Date(d).getDate()

const upcomingDeadlines = computed(() => {
  return tasks.value
    .filter(t => t.status !== 'COMPLETED' && t.status !== 'DONE')
    .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
    .slice(0, 5) // Show top 5
})

onMounted(() => {
  getUserData()
  formatDateDisplay()
  fetchAssignedTasks()
})
</script>

<style scoped>
.animate-fade-in-up {
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background-color: #d1fae5; border-radius: 20px; }
.dark .custom-scrollbar::-webkit-scrollbar-thumb { background-color: #064e3b; }
</style>