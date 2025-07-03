<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 dark:from-gray-900 dark:to-gray-800 p-6">
    <!-- Header -->
    <div class="bg-gradient-to-r from-indigo-600 to-purple-700 rounded-2xl p-6 text-white mb-8 relative overflow-hidden">
      <div class="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -translate-y-16 translate-x-16"></div>
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full translate-y-12 -translate-x-12"></div>
      
      <div class="relative z-10">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between">
          <div>
            <h1 class="text-3xl font-bold mb-2">¡Bienvenido de vuelta!</h1>
            <p class="text-indigo-100 text-lg">Estás trabajando en el proyecto: <strong>{{ projectName }}</strong></p>
          </div>
          <div class="mt-4 md:mt-0">
            <div class="flex items-center space-x-3">
              <div class="w-14 h-14 bg-white/20 rounded-xl flex items-center justify-center">
                <i class="fas fa-project-diagram text-2xl"></i>
              </div>
              <div>
                <p class="text-indigo-100">Proyecto ID</p>
                <p class="font-bold text-xl">{{ projectId }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div v-for="stat in stats" :key="stat.title" 
           class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow duration-300 border border-slate-100 dark:border-gray-700">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-slate-600 dark:text-gray-300 mb-1">{{ stat.title }}</p>
            <p class="text-3xl font-bold text-slate-800 dark:text-white">{{ stat.value }}</p>
            <p class="text-sm text-slate-500 dark:text-gray-400 mt-1">{{ stat.change }}</p>
          </div>
          <div :class="stat.iconBg" class="w-12 h-12 rounded-xl flex items-center justify-center shadow-md">
            <i :class="stat.icon" class="text-white text-lg"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
      <!-- Completion Chart -->
      <div class="lg:col-span-2 bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-slate-100 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-slate-800 dark:text-white mb-6">Progreso del Proyecto</h3>
        <div class="h-64">
          <canvas ref="progressChart"></canvas>
        </div>
      </div>

      <!-- Task Distribution -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-slate-100 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-slate-800 dark:text-white mb-6">Distribución de Tareas</h3>
        <div class="h-64">
          <canvas ref="statusChart"></canvas>
        </div>
      </div>
    </div>

    <!-- Priority and Recent Tasks -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Priority Distribution -->
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-slate-100 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-slate-800 dark:text-white mb-6">Prioridades</h3>
        <div class="h-64">
          <canvas ref="priorityChart"></canvas>
        </div>
      </div>

      <!-- Recent Tasks -->
      <div class="lg:col-span-2 bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-slate-100 dark:border-gray-700">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-slate-800 dark:text-white">Tareas Recientes</h3>
          <router-link to="/tasks" class="text-sm text-indigo-600 dark:text-indigo-400 hover:underline">
            Ver todas
          </router-link>
        </div>
        
        <div class="space-y-4">
          <div v-for="task in recentTasks" :key="task.id" 
               class="p-4 bg-slate-50 dark:bg-gray-700 rounded-lg hover:bg-slate-100 dark:hover:bg-gray-600 transition-colors">
            <div class="flex items-start justify-between">
              <div>
                <h4 class="font-medium text-slate-800 dark:text-white">{{ task.name }}</h4>
                <p class="text-sm text-slate-500 dark:text-gray-400 mt-1">{{ task.description || 'Sin descripción' }}</p>
              </div>
              <div class="flex items-center space-x-2">
                <span :class="getStatusClass(task.status)" class="px-3 py-1 rounded-full text-xs font-medium">
                  {{ task.statusDisplayName }}
                </span>
                <i :class="getPriorityIcon(task.priority)" class="text-lg"></i>
              </div>
            </div>
            <div class="flex items-center justify-between mt-3 text-sm">
              <span class="text-slate-500 dark:text-gray-400">
                <i class="far fa-calendar-alt mr-1"></i>
                {{ formatDate(task.endDate) }}
              </span>
              <span class="font-medium" :class="getHoursClass(task)">
                {{ task.actualHours || 0 }}/{{ task.estimatedHours || '?' }}h
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import axios from 'axios';
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()


Chart.register(...registerables);

export default {
  name: 'DashboardView',
  data() {
    return {
      projectId: null,
      projectName: '',
      tasks: [],
      stats: [],
      recentTasks: [],
      progressChart: null,
      statusChart: null,
      priorityChart: null
    };
  },
  computed: {
    completedTasks() {
      return this.tasks.filter(t => t.status === 'COMPLETED').length;
    },
    inProgressTasks() {
      return this.tasks.filter(t => t.status === 'IN_PROGRESS').length;
    },
    pendingTasks() {
      return this.tasks.filter(t => t.status === 'PENDING').length;
    },
    urgentTasks() {
      return this.tasks.filter(t => t.priority === 'URGENT').length;
    }
  },
  async mounted() {
    await this.loadProjectData();
    this.prepareStats();
    this.initCharts();
  },
  methods: {
    async loadProjectData() {
      try {
        const userEmail = authStore.user?.email || localStorage.getItem('userEmail');
         console.log('🔍 Usuario actual:', userEmail); 
    
        if (!userEmail) {
          throw new Error('User email not available');
        }
        const response = await axios.get(`/api/projects/assing-project/${userEmail}`);
        
        this.projectId = response.data.projectId;
        this.projectName = response.data.projectName;
        this.tasks = response.data.tasks || [];
        
        // Ordenar tareas recientes por fecha
        this.recentTasks = [...this.tasks]
          .sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
          .slice(0, 5);
          
      } catch (error) {
        console.error('Error loading project data:', error);
      }
    },
    
    prepareStats() {
      this.stats = [
        { 
          title: 'Total Tareas', 
          value: this.tasks.length,
          change: '+2 esta semana',
          icon: 'fas fa-tasks',
          iconBg: 'bg-gradient-to-r from-indigo-500 to-indigo-600'
        },
        { 
          title: 'Completadas', 
          value: this.completedTasks,
          change: `${Math.round((this.completedTasks / this.tasks.length) * 100) || 0}%`,
          icon: 'fas fa-check-circle',
          iconBg: 'bg-gradient-to-r from-green-500 to-emerald-500'
        },
        { 
          title: 'En Progreso', 
          value: this.inProgressTasks,
          change: `${Math.round((this.inProgressTasks / this.tasks.length) * 100) || 0}%`,
          icon: 'fas fa-spinner',
          iconBg: 'bg-gradient-to-r from-blue-500 to-cyan-500'
        },
        { 
          title: 'Urgentes', 
          value: this.urgentTasks,
          change: 'Prioridad máxima',
          icon: 'fas fa-exclamation-triangle',
          iconBg: 'bg-gradient-to-r from-red-500 to-rose-500'
        }
      ];
    },
    
    initCharts() {
      // Progress Chart (Line)
      this.progressChart = new Chart(this.$refs.progressChart, {
        type: 'line',
        data: {
          labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
          datasets: [{
            label: 'Tareas Completadas',
            data: [12, 19, 3, 5, 2, 3],
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99, 102, 241, 0.1)',
            tension: 0.3,
            fill: true
          }]
        },
        options: this.getChartOptions('Progreso Mensual')
      });
      
      // Status Distribution (Doughnut)
      this.statusChart = new Chart(this.$refs.statusChart, {
        type: 'doughnut',
        data: {
          labels: ['Completadas', 'En Progreso', 'Pendientes', 'Canceladas'],
          datasets: [{
            data: [
              this.completedTasks,
              this.inProgressTasks,
              this.pendingTasks,
              this.tasks.filter(t => t.status === 'CANCELLED').length
            ],
            backgroundColor: [
              '#10b981',
              '#3b82f6',
              '#f59e0b',
              '#ef4444'
            ],
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                color: '#6b7280'
              }
            }
          }
        }
      });
      
      // Priority Distribution (Bar)
      this.priorityChart = new Chart(this.$refs.priorityChart, {
        type: 'bar',
        data: {
          labels: ['Baja', 'Media', 'Alta', 'Urgente'],
          datasets: [{
            label: 'Tareas por Prioridad',
            data: [
              this.tasks.filter(t => t.priority === 'LOW').length,
              this.tasks.filter(t => t.priority === 'MEDIUM').length,
              this.tasks.filter(t => t.priority === 'HIGH').length,
              this.tasks.filter(t => t.priority === 'URGENT').length
            ],
            backgroundColor: [
              'rgba(16, 185, 129, 0.7)',
              'rgba(59, 130, 246, 0.7)',
              'rgba(245, 158, 11, 0.7)',
              'rgba(239, 68, 68, 0.7)'
            ],
            borderColor: [
              '#10b981',
              '#3b82f6',
              '#f59e0b',
              '#ef4444'
            ],
            borderWidth: 1
          }]
        },
        options: this.getChartOptions('Distribución por Prioridad')
      });
    },
    
    getChartOptions(title) {
      return {
        responsive: true,
        plugins: {
          legend: {
            position: 'top',
          },
          title: {
            display: true,
            text: title,
            color: '#6b7280'
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(0, 0, 0, 0.05)'
            }
          },
          x: {
            grid: {
              display: false
            }
          }
        }
      };
    },
    
    getStatusClass(status) {
      const classes = {
        'COMPLETED': 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
        'IN_PROGRESS': 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400',
        'PENDING': 'bg-amber-100 text-amber-800 dark:bg-amber-900/30 dark:text-amber-400',
        'CANCELLED': 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
        'IN_REVIEW': 'bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400'
      };
      return classes[status] || 'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400';
    },
    
    getPriorityIcon(priority) {
      const icons = {
        'LOW': 'fas fa-arrow-down text-green-500',
        'MEDIUM': 'fas fa-equals text-blue-500',
        'HIGH': 'fas fa-arrow-up text-orange-500',
        'URGENT': 'fas fa-exclamation-triangle text-red-500'
      };
      return icons[priority] || 'fas fa-question text-gray-500';
    },
    
    getHoursClass(task) {
      if (!task.estimatedHours) return 'text-gray-500';
      const ratio = task.actualHours / task.estimatedHours;
      if (ratio > 1.2) return 'text-red-500';
      if (ratio > 0.8) return 'text-green-500';
      return 'text-amber-500';
    },
    
    formatDate(dateString) {
      if (!dateString) return 'Sin fecha';
      const options = { day: 'numeric', month: 'short', year: 'numeric' };
      return new Date(dateString).toLocaleDateString('es-ES', options);
    }
  },
  
  beforeUnmount() {
    if (this.progressChart) this.progressChart.destroy();
    if (this.statusChart) this.statusChart.destroy();
    if (this.priorityChart) this.priorityChart.destroy();
  }
};
</script>

<style scoped>
/* Custom styles */
.bg-gradient-header {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
}

.chart-container {
  position: relative;
  height: 300px;
}
</style>