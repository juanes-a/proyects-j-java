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

   <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
     <div class="lg:col-span-2 bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover border border-slate-100 dark:border-gray-700">
       <div class="flex items-center justify-between mb-4">
         <h3 class="text-lg font-semibold text-slate-800 dark:text-white flex items-center">
           <i class="fas fa-filter text-indigo-500 mr-2"></i>
           Filters
         </h3>
         <button @click="clearFilters" class="text-sm text-slate-500 dark:text-gray-400 hover:text-indigo-600 dark:hover:text-indigo-400 font-medium transition-colors">
           Clear All
         </button>
       </div>


       <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
         <div>
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">Status</label>
           <select v-model="filters.status" @change="filterTasks" class="form-select w-full">
             <option value="">All statuses</option>
             <option value="PENDING">Pending</option>
             <option value="IN_PROGRESS">In Progress</option>
             <option value="IN_REVIEW">In Review</option>
             <option value="COMPLETED">Completed</option>
             <option value="CANCELLED">Cancelled</option>
           </select>
         </div>

         <div>
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">Priority</label>
           <select v-model="filters.priority" @change="filterTasks" class="form-select w-full">
             <option value="">All priorities</option>
             <option value="LOW">Low</option>
             <option value="MEDIUM">Medium</option>
             <option value="HIGH">High</option>
             <option value="URGENT">Urgent</option>
           </select>
         </div>

         <div>
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">Project</label>
           <select v-model="filters.projectId" @change="filterTasks" class="form-select w-full">
             <option value="">All projects</option>
             <option v-for="project in projects" :key="project.id" :value="project.id">{{ project.name }}</option>
           </select>
         </div>

         <div>
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">Start Date</label>
           <input type="date" v-model="filters.startDate" @change="filterTasks" class="form-input w-full">
         </div>

         <div>
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">End Date</label>
           <input type="date" v-model="filters.endDate" @change="filterTasks" class="form-input w-full">
         </div>

         <div class="md:col-span-2">
           <label class="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2">Keyword</label>
           <input type="text" v-model="filters.keyword" @input="filterTasks" placeholder="Search by name or description" class="form-input w-full">
         </div>

       </div>
       <div class="mt-4 md:col-span-2">
         <button
           class="btn btn-primary"
           @click="generatePdf"
         >
           <i class="fas fa-file-pdf mr-2"></i>
           Download PDF Report
         </button>
       </div>

     </div>



      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover border border-slate-100 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-4">Quick Actions</h3>
        <div class="space-y-3">
          <button
            @click="openCreateModal"
            class="w-full flex items-center space-x-3 p-3 text-left rounded-lg border-2 border-dashed border-gray-200 dark:border-gray-600 hover:border-indigo-500 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 transition-all duration-200 group"
          >
            <div class="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
              <i class="fas fa-plus text-white text-sm"></i>
            </div>
            <div>
              <p class="font-medium text-gray-800 dark:text-white text-sm">New Task</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">Create a new task</p>
            </div>
          </button>
          <button
            @click="clearFilters"
            class="w-full flex items-center space-x-3 p-3 text-left rounded-lg border-2 border-dashed border-gray-200 dark:border-gray-600 hover:border-purple-500 hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-all duration-200 group"
          >
            <div class="w-8 h-8 bg-purple-500 rounded-lg flex items-center justify-center group-hover:scale-110 transition-transform duration-200">
              <i class="fas fa-eye text-white text-sm"></i>
            </div>
            <div>
              <p class="font-medium text-gray-800 dark:text-white text-sm">View All</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">Show all tasks</p>
            </div>
          </button>
        </div>
      </div>
    </div>

    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-slate-100 dark:border-gray-700 overflow-hidden card-hover">
      <div class="bg-gradient-to-r from-slate-50 to-slate-100 dark:from-gray-700 dark:to-gray-800 p-6 border-b border-slate-200 dark:border-gray-600">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          <div>
            <h2 class="text-xl font-bold text-slate-800 dark:text-white flex items-center">
              <i class="fas fa-list-ul text-indigo-500 mr-3"></i>
              Task List
            </h2>
            <p class="text-slate-600 dark:text-gray-400 mt-1">{{ filteredTasks.length }} tasks found</p>
          </div>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 dark:bg-gray-700 border-b border-slate-200 dark:border-gray-600">
            <tr>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Task</th>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Project</th>

              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Status</th>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Priority</th>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Dates</th>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Hours</th>
              <th class="px-6 py-4 text-left text-sm font-bold text-slate-700 dark:text-gray-300 uppercase tracking-wide">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100 dark:divide-gray-600">
            <tr v-for="task in filteredTasks" :key="task.id" class="hover:bg-slate-50 dark:hover:bg-gray-700 transition-colors duration-200">

              <td class="px-6 py-4">
                <div class="flex items-center">
                  <div class="w-12 h-12 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-xl flex items-center justify-center shadow-md">
                    <i class="fas fa-clipboard-list text-white"></i>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-bold text-slate-900 dark:text-white">{{ task.name }}</div>
                    <div class="text-sm text-slate-500 dark:text-gray-400 truncate max-w-xs">{{ task.description || 'No description' }}</div>
                  </div>
                </div>
              </td>

              <td class="px-6 py-4">
               <div class="flex items-center">
                 <i class="fas fa-folder-open text-indigo-500 dark:text-indigo-400 mr-2 text-lg"></i>
                 <span class="inline-block bg-indigo-200/70 text-indigo-900 dark:bg-indigo-800/50 dark:text-indigo-200 text-sm font-bold px-4 py-1 rounded-xl shadow-sm tracking-wide uppercase">
                   {{ task.projectName || 'No project' }}
                 </span>
               </div>
             </td>



              <td class="px-6 py-4">
                <span :class="getStatusClass(task.status)" class="status-badge">
                  {{ getStatusDisplay(task.status) }}
                </span>
              </td>

              <td class="px-6 py-4">
                <div class="flex items-center">
                  <i :class="getPriorityIcon(task.priority)" class="mr-2 text-lg"></i>
                  <span class="text-sm font-medium text-slate-700 dark:text-gray-300">{{ getPriorityDisplay(task.priority) }}</span>
                </div>
              </td>

              <td class="px-6 py-4">
                <div class="text-sm font-medium text-slate-900 dark:text-white">{{ formatDate(task.startDate) }}</div>
                <div class="text-sm text-slate-500 dark:text-gray-400">{{ formatDate(task.endDate) }}</div>
              </td>

              <td class="px-6 py-4">
                <div class="text-sm font-medium text-slate-900 dark:text-white">Est: {{ task.estimatedHours || '-' }}h</div>
                <div class="text-sm text-slate-500 dark:text-gray-400">Act: {{ task.actualHours || '-' }}h</div>
              </td>



             <td class="px-6 py-4">
               <div class="flex items-center space-x-3">
                 <button @click="editTask(task)" class="action-btn bg-indigo-100 text-indigo-700 hover:bg-indigo-200 dark:bg-indigo-900/30 dark:text-indigo-400" title="Edit">
                   ✏️
                 </button>
                 <button @click="deleteTask(task.id)" class="action-btn bg-red-100 text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400" title="Delete">
                   🗑️
                 </button>
               </div>
             </td>


            </tr>
          </tbody>
          </table>
          </div>



      <div v-if="filteredTasks.length === 0" class="text-center py-16">
        <div class="w-24 h-24 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-full flex items-center justify-center mx-auto mb-6 shadow-lg">
          <i class="fas fa-tasks text-white text-3xl"></i>
        </div>
        <h3 class="text-xl font-bold text-slate-800 dark:text-white mb-2">No tasks found</h3>
        <p class="text-slate-500 dark:text-gray-400 mb-6">Start by creating your first task or adjust the filters.</p>
        <button @click="openCreateModal" class="btn-primary">
          <i class="fas fa-plus mr-2"></i>
          New Task
        </button>
      </div>
    </div>

    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 card-hover border border-slate-100 dark:border-gray-700">
      <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-6">Performance Indicators</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                :stroke-dasharray="`${completionRate}, 100`"
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
              <span class="text-lg font-bold text-indigo-600 dark:text-indigo-400">{{ completionRate }}%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">Completion Rate</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Completed tasks</p>
        </div>

        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                :stroke-dasharray="`${progressRate}, 100`"
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
              <span class="text-lg font-bold text-green-600 dark:text-green-400">{{ progressRate }}%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">In Progress</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Active tasks</p>
        </div>

        <div class="text-center">
          <div class="w-20 h-20 mx-auto mb-4 relative">
            <svg class="w-20 h-20 transform -rotate-90" viewBox="0 0 36 36">
              <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                :stroke-dasharray="`${pendingRate}, 100`"
                class="text-amber-500"
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
              <span class="text-lg font-bold text-amber-600 dark:text-amber-400">{{ pendingRate }}%</span>
            </div>
          </div>
          <p class="text-sm font-medium text-gray-800 dark:text-white">Pending</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">Tasks to start</p>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="bg-gradient-to-r from-indigo-600 to-purple-600 p-6 text-white rounded-t-2xl">
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-bold flex items-center">
              <i class="fas fa-tasks mr-2"></i>
              {{ isEditing ? 'Edit Task' : 'New Task' }}
            </h3>
            <button @click="closeModal" class="text-white hover:bg-white hover:bg-opacity-20 dark:hover:bg-gray-700 p-2 rounded-lg transition-colors">
              <i class="fas fa-times text-lg"></i>
            </button>
          </div>
        </div>

        <form @submit.prevent="saveTask" class="p-6 space-y-4">
          <div>
            <label class="form-label">Name *</label>
            <input v-model="taskForm.name" type="text" required maxlength="100" class="form-input">
          </div>

          <div>
            <label class="form-label">Description</label>
            <textarea v-model="taskForm.description" maxlength="500" rows="3" class="form-input"></textarea>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="form-label">Status *</label>
              <select v-model="taskForm.status" required class="form-select">
                <option value="PENDING">Pending</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="IN_REVIEW">In Review</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
            <div>
              <label class="form-label">Priority *</label>
              <select v-model="taskForm.priority" required class="form-select">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent</option>
              </select>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="form-label">Start Date</label>
              <input v-model="taskForm.startDate" type="datetime-local" class="form-input">
            </div>
            <div>
              <label class="form-label">End Date</label>
              <input v-model="taskForm.endDate" type="datetime-local" class="form-input">
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="form-label">Estimated Hours</label>
              <input v-model="taskForm.estimatedHours" type="number" min="0" class="form-input">
            </div>
            <div>
              <label class="form-label">Actual Hours</label>
              <input v-model="taskForm.actualHours" type="number" min="0" class="form-input">
            </div>
          </div>


          <div v-if="!isEditing">
            <label class="form-label">Assign to (username or email)</label>
            <input
              v-model="taskForm.assignedUserIdentifier"
              type="text"
              placeholder="Enter username or email"
              class="form-input"
            >
          </div>

          <div class="flex justify-end space-x-3 pt-6 border-t border-slate-200 dark:border-gray-600">
            <button type="button" @click="closeModal" class="btn-secondary">Cancel</button>
            <button type="submit" class="btn-primary">{{ isEditing ? 'Update' : 'Create' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

export default {
  name: 'TaskView',
 data() {
   return {
     tasks: [],
     filteredTasks: [],
     projects: [],
     users: [], // 🧑 List of assignable users
     showModal: false,
     isEditing: false,
     filters: {
       status: '',
       priority: '',
       projectId: '',
       assignedUserId: '',
       startDate: '',
       endDate: '',
       keyword: ''
     },
     taskForm: {
       id: null,
       name: '',
       description: '',
       status: 'PENDING',
       priority: 'MEDIUM',
       startDate: '',
       endDate: '',
       estimatedHours: null,
       actualHours: null,
       projectId: '',
       assignedUserIdentifier: ''
     }
   };


  },
  computed: {
    stats() {
      return {
        total: this.tasks.length,
        pending: this.tasks.filter(t => t.status === 'PENDING').length,
        inProgress: this.tasks.filter(t => t.status === 'IN_PROGRESS').length,
        completed: this.tasks.filter(t => t.status === 'COMPLETED').length
      }
    },
    headerStats() {
      return [
        { title: 'Total Tasks', value: this.stats.total, icon: 'fas fa-tasks', iconBg: 'bg-gradient-to-r from-indigo-500 to-indigo-600' },
        { title: 'Pending', value: this.stats.pending, icon: 'fas fa-clock', iconBg: 'bg-gradient-to-r from-amber-500 to-orange-500' },
        { title: 'In Progress', value: this.stats.inProgress, icon: 'fas fa-spinner', iconBg: 'bg-gradient-to-r from-green-500 to-emerald-500' },
        { title: 'Completed', value: this.stats.completed, icon: 'fas fa-check-circle', iconBg: 'bg-gradient-to-r from-purple-500 to-purple-600' }
      ]
    },
    completionRate() {
      if (this.stats.total === 0) return 0
      return Math.round((this.stats.completed / this.stats.total) * 100)
    },
    progressRate() {
      if (this.stats.total === 0) return 0
      return Math.round((this.stats.inProgress / this.stats.total) * 100)
    },
    pendingRate() {
      if (this.stats.total === 0) return 0
      return Math.round((this.stats.pending / this.stats.total) * 100)
    }
  },
  async mounted() {
    await this.loadTasks()
  },

  methods: {
    async loadTasks() {
     try {
       const userEmail = authStore.user?.email || localStorage.getItem('userEmail');
       console.log('🔍 Current user:', userEmail);

       if (!userEmail) {
         throw new Error('User email not available');
       }

       const response = await axios.get(`http://localhost:8081/api/projects/assing-project/${userEmail}`);

       console.log('📦 Complete backend response:', response);
       console.log('📊 Data received:', response.data);
       console.log('✅ Tasks received:', response.data.tasks);

       this.tasks = response.data.tasks;
       this.filteredTasks = [...this.tasks];

       if (response.data.projectName === 'All projects') {
         const allProjectsResponse = await axios.get('http://localhost:8081/api/projects');

         this.projects = [
           { id: null, name: 'All projects' },
           ...allProjectsResponse.data.map(project => ({
             id: project.id,
             name: project.name
           }))
         ];

         this.taskForm.projectId = null;
       } else {
         this.projects = [{
           id: response.data.projectId,
           name: response.data.projectName
         }];

         this.taskForm.projectId = response.data.projectId;
       }


     } catch (error) {
       console.error('Error loading tasks:', error);
       this.projects = [];
       this.tasks = [];
       this.filteredTasks = [];
     }
    },

    // 👇 This is the function that gives you the formatted date
    getCurrentDate() {
      const now = new Date()
      return now.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },

  generatePdf() {
    const params = new URLSearchParams();

    if (this.filters.projectId) params.append('projectId', this.filters.projectId);
    if (this.filters.status) params.append('status', this.filters.status);
    if (this.filters.priority) params.append('priority', this.filters.priority);
    if (this.filters.startDate) params.append('startDate', `${this.filters.startDate}T00:00:00`);
    if (this.filters.endDate) params.append('endDate', `${this.filters.endDate}T23:59:59`);
    if (this.filters.keyword) params.append('keyword', this.filters.keyword);

    const url = `/api/tasks/report?${params.toString()}`;
    console.log("🧾 Opening PDF at:", url); // Debug
    window.open(url, '_blank');
  },



    filterTasks() {
      this.filteredTasks = this.tasks.filter(task => {
        const statusMatch = !this.filters.status || task.status === this.filters.status
        const priorityMatch = !this.filters.priority || task.priority === this.filters.priority
      const projectMatch = !this.filters.projectId || task.projectId === Number(this.filters.projectId)
        const userMatch = !this.filters.assignedUserId || task.assignedUser?.id === Number(this.filters.assignedUserId)

        const startMatch = !this.filters.startDate || new Date(task.startDate) >= new Date(this.filters.startDate)
        const endMatch = !this.filters.endDate || new Date(task.endDate) <= new Date(this.filters.endDate)

        const keywordMatch = !this.filters.keyword ||
          task.name?.toLowerCase().includes(this.filters.keyword.toLowerCase()) ||
          task.description?.toLowerCase().includes(this.filters.keyword.toLowerCase())

        return statusMatch && priorityMatch && projectMatch && userMatch && startMatch && endMatch && keywordMatch
      })
    },

    clearFilters() {
      this.filters = {
        status: '',
        priority: '',
        projectId: '',
        assignedUserId: '',
        startDate: '',
        endDate: '',
        keyword: ''
      }
      this.filterTasks()
    },

    openCreateModal() {
      this.isEditing = false
      this.resetForm()
      this.showModal = true
    },
    editTask(task) {
      this.isEditing = true
      this.taskForm = {
        id: task.id, name: task.name, description: task.description || '', status: task.status, priority: task.priority,
        startDate: task.startDate ? this.formatDateForInput(task.startDate) : '',
        endDate: task.endDate ? this.formatDateForInput(task.endDate) : '',
        estimatedHours: task.estimatedHours, actualHours: task.actualHours, projectId: task.project?.id || ''
      }
      this.showModal = true
    },
    async saveTask() {
      try {
        // Verify that there is an assigned project
        if (!this.projects.length || !this.projects[0].id) {
          throw new Error('No project assigned');
        }

        const taskData = {
          name: this.taskForm.name,
          description: this.taskForm.description,
          status: this.taskForm.status,
          priority: this.taskForm.priority,
          startDate: this.taskForm.startDate || null,
          endDate: this.taskForm.endDate || null,
          estimatedHours: this.taskForm.estimatedHours,
          actualHours: this.taskForm.actualHours,
          projectId: this.projects[0].id
        };

        let response;
        if (this.isEditing) {
          response = await axios.put(`/api/tasks/${this.taskForm.id}`, taskData);
        } else {
          // 1. First we create the task
          response = await axios.post('/api/tasks', taskData);

          // 2. If there is a user to assign, we do it after
          if (this.taskForm.assignedUserIdentifier) {
            try {
              await axios.post('/api/tasks/assign-task', {
                usernameOrEmail: this.taskForm.assignedUserIdentifier,
                taskId: response.data.id,
                role: 'COLLAB'
              });

              console.log("User assigned successfully");
            } catch (assignError) {
              console.error('Error assigning user:', assignError);
              alert('Task created but user assignment failed');
            }
          }
        }

        await this.loadTasks();
        this.closeModal();
      } catch (error) {
        console.error('Error saving task:', error);
        alert(error.response?.data?.message || 'Error saving task');
      }
    },
    async deleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task?')) {
              try {
                await axios.delete(`/api/tasks/${taskId}`)
                await this.loadTasks()
              } catch (error) {
                console.error('Error deleting task:', error)
                alert('Error deleting task')
              }
            }
            }
          ,
          closeModal() {
            this.showModal = false
            this.resetForm()
          },
          resetForm() {
            this.taskForm = {
              id: null,
              name: '',
              description: '',
              status: 'PENDING',
              priority: 'MEDIUM',
              startDate: '',
              endDate: '',
              estimatedHours: null,
              actualHours: null,
              projectId: ''
            }
          },
          formatDate(dateString) {
            if (!dateString) return '-'
            const date = new Date(dateString)
            return date.toLocaleDateString('en-US', {
              year: 'numeric',
              month: 'short',
              day: 'numeric'
            })
          },
          formatDateForInput(dateString) {
            if (!dateString) return ''
            const date = new Date(dateString)
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, '0')
            const day = String(date.getDate()).padStart(2, '0')
            const hours = String(date.getHours()).padStart(2, '0')
            const minutes = String(date.getMinutes()).padStart(2, '0')
            return `${year}-${month}-${day}T${hours}:${minutes}`
          },
          getStatusClass(status) {
            const classes = {
              'PENDING': 'bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400',
              'IN_PROGRESS': 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400',
              'IN_REVIEW': 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400',
              'COMPLETED': 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
              'CANCELLED': 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
            }
            return classes[status] || 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400'
          },
          getStatusDisplay(status) {
            const statuses = {
              'PENDING': 'Pending',
              'IN_PROGRESS': 'In Progress',
              'IN_REVIEW': 'In Review',
              'COMPLETED': 'Completed',
              'CANCELLED': 'Cancelled'
            }
            return statuses[status] || status
          },
          getPriorityIcon(priority) {
            const icons = {
              'LOW': 'fas fa-arrow-down text-green-500',
              'MEDIUM': 'fas fa-minus text-yellow-500',
              'HIGH': 'fas fa-arrow-up text-orange-500',
              'URGENT': 'fas fa-exclamation text-red-500'
            }
            return icons[priority] || 'fas fa-minus text-gray-500'
          },
          getPriorityDisplay(priority) {
            const priorities = {
              'LOW': 'Low',
              'MEDIUM': 'Medium',
              'HIGH': 'High',
              'URGENT': 'Urgent'
            }
            return priorities[priority] || priority
          }
        }
      }



    </script>

    <style scoped>
    /* Custom styles for the task management view */
    .card-hover {
      transition: all 0.3s ease;
    }

    .card-hover:hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    }


    .btn-primary {
      @apply bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 text-white font-semibold py-2 px-4 rounded-lg transition-all duration-200 shadow-md hover:shadow-lg;
    }

    .btn-primary-header {
      @apply bg-white/20 hover:bg-white/30 text-white font-semibold py-2 px-4 rounded-lg transition-all duration-200 backdrop-blur-sm;
    }

    .btn-secondary {
      @apply bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-800 dark:text-gray-200 font-semibold py-2 px-4 rounded-lg transition-all duration-200;
    }

    .form-label {
      @apply block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2;
    }

    .form-input {
      @apply w-full px-3 py-2 border border-slate-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white transition-colors;
    }

    .form-select {
      @apply w-full px-3 py-2 border border-slate-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 dark:bg-gray-700 dark:text-white transition-colors;
    }

    .status-badge {
      @apply inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium;
    }

    .action-btn {
      @apply w-10 h-10 rounded-lg flex items-center justify-center transition-all duration-200 hover:scale-105;
    }
    </style>