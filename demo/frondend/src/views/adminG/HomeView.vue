<template>
  <div class="w-full min-h-screen animate-fade-in-up">
    <div class="max-w-none space-y-6">
      
      <div class="relative overflow-hidden bg-gradient-to-br from-orange-600 via-orange-500 to-red-600 rounded-2xl p-6 sm:p-8 text-white shadow-xl shadow-orange-500/20">
        <div class="absolute top-0 right-0 -mt-4 -mr-4 w-32 h-32 bg-white/20 rounded-full blur-2xl"></div>
        <div class="absolute bottom-0 left-0 -mb-4 -ml-4 w-24 h-24 bg-black/10 rounded-full blur-xl"></div>
        
        <div class="relative flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 z-10">
          <div class="flex-1 min-w-0">
            <h2 class="text-3xl sm:text-4xl font-extrabold mb-2 tracking-tight">
              Dashboard Global
            </h2>
            <p class="text-orange-50 text-sm sm:text-base font-medium max-w-xl">
              Bienvenido al centro de mando. Aquí tienes el resumen financiero y operativo de toda la organización en tiempo real.
            </p>
          </div>
          
          <div class="hidden md:block flex-shrink-0">
            <div class="w-20 h-20 bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl flex items-center justify-center shadow-inner transform rotate-3 hover:rotate-6 transition-transform duration-300">
              <LayoutDashboard class="w-10 h-10 text-white" />
            </div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 lg:gap-6">
        <StatsCard
          v-for="stat in stats"
          :key="stat.title"
          :title="stat.title"
          :value="stat.value"
          :change="stat.change"
          :icon="stat.icon"
          :color="stat.color"
          :loading="loading"
          class="min-w-0"
        />
      </div>

      <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">
        
        <div class="xl:col-span-2 bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6 transition-all duration-300 hover:shadow-md">
          <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 gap-4">
            <div>
              <h3 class="text-xl font-bold text-gray-800 dark:text-white flex items-center gap-2">
                <PieChart class="w-5 h-5 text-orange-500" />
                Presupuesto por Departamento
              </h3>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Distribución financiera anual</p>
            </div>
            
            <div class="flex bg-gray-100 dark:bg-zinc-800 p-1 rounded-xl">
              <button
                @click="changeChartType('bar')"
                :class="[
                  'px-4 py-1.5 rounded-lg text-sm font-medium transition-all duration-200',
                  chartType === 'bar' 
                    ? 'bg-white dark:bg-zinc-700 text-orange-600 dark:text-orange-400 shadow-sm' 
                    : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                ]"
              >
                Barras
              </button>
              <button
                @click="changeChartType('pie')"
                :class="[
                  'px-4 py-1.5 rounded-lg text-sm font-medium transition-all duration-200',
                  chartType === 'pie' 
                    ? 'bg-white dark:bg-zinc-700 text-orange-600 dark:text-orange-400 shadow-sm' 
                    : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                ]"
              >
                Circular
              </button>
            </div>
          </div>
          
          <div class="h-80 w-full relative">
            <canvas ref="budgetChart"></canvas>
            <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-orange-500"></div>
             </div>
          </div>
        </div>

        <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6 flex flex-col h-full">
          <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6 flex items-center gap-2">
            <History class="w-5 h-5 text-orange-500" />
            Actividad Reciente
          </h3>
          
          <div class="space-y-6 overflow-y-auto max-h-[400px] custom-scrollbar pr-2">
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              class="relative pl-6 pb-2 border-l border-gray-200 dark:border-zinc-700 last:border-0 group"
            >
              <div :class="[
                'absolute -left-1.5 top-1 w-3 h-3 rounded-full border-2 border-white dark:border-zinc-900 transition-colors',
                activity.dotColor || 'bg-gray-400'
              ]"></div>

              <div class="flex flex-col min-w-0">
                <p class="text-sm font-semibold text-gray-800 dark:text-gray-200 group-hover:text-orange-500 transition-colors">
                  {{ activity.title }}
                </p>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1 flex items-center gap-1">
                  <Clock class="w-3 h-3" />
                  {{ activity.time }}
                </p>
              </div>
            </div>

            <div v-if="recentActivities.length === 0 && !loading" class="text-center text-gray-500 py-4">
              No hay actividad reciente.
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white dark:bg-zinc-900 border border-gray-100 dark:border-zinc-800 rounded-2xl shadow-sm p-6">
        <h3 class="text-xl font-bold text-gray-800 dark:text-white mb-6">Acciones Rápidas</h3>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button
            v-for="action in quickActions"
            :key="action.title"
            @click="action.action"
            class="group relative flex items-center p-4 rounded-xl border border-gray-200 dark:border-zinc-700 bg-gray-50 dark:bg-zinc-800/50 hover:bg-white dark:hover:bg-zinc-800 hover:border-orange-500 dark:hover:border-orange-500 hover:shadow-lg hover:shadow-orange-500/10 transition-all duration-300 text-left"
          >
            <div :class="[
              'w-12 h-12 rounded-lg flex items-center justify-center transition-transform duration-300 group-hover:scale-110 text-white shadow-md',
              action.iconBg
            ]">
              <component :is="action.icon" class="w-6 h-6" />
            </div>
            
            <div class="ml-4 flex-1">
              <p class="font-bold text-gray-800 dark:text-white group-hover:text-orange-600 dark:group-hover:text-orange-400 transition-colors">
                {{ action.title }}
              </p>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-0.5">
                {{ action.description }}
              </p>
            </div>

            <ChevronRight class="w-5 h-5 text-gray-300 group-hover:text-orange-500 transform group-hover:translate-x-1 transition-all" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { 
  TrendingUp, 
  Building2, 
  FolderOpen, 
  Users, 
  Edit, 
  Eye,
  LayoutDashboard,
  PieChart,
  History,
  Clock,
  ChevronRight
} from 'lucide-vue-next'
import Chart from 'chart.js/auto'
import api from '../../api'
import StatsCard from '../../components/StatsCard.vue'
import { useToastStore } from '../../stores/toast'

const router = useRouter()
const toastStore = useToastStore()

// Estados reactivos
const loading = ref(true)
const chartType = ref('bar')
const budgetChart = ref(null)
let chartInstance = null

const props = defineProps({
  sidebarOpen: { type: Boolean, default: true },
  sidebarMobile: { type: Boolean, default: false }
})

// Datos iniciales (Colores actualizados al tema Naranja/Dark)
const stats = ref([
  { 
    title: 'Departamentos', 
    value: '0', 
    change: 'Cargando...', 
    icon: markRaw(Building2),
    color: 'orange' // Principal
  },
  { 
    title: 'Proyectos Activos', 
    value: '0', 
    change: 'Cargando...', 
    icon: markRaw(FolderOpen),
    color: 'emerald' // Contraste verde
  },
  { 
    title: 'Presupuesto Total', 
    value: '$0', 
    change: 'Cargando...', 
    icon: markRaw(TrendingUp),
    color: 'red' // Acento fuerte
  },
  { 
    title: 'Total Personal', 
    value: '0', 
    change: 'Cargando...', 
    icon: markRaw(Users),
    color: 'zinc' // Neutro oscuro
  }
])

const recentActivities = ref([])

// Quick Actions filtrados (Solo Deptos y Proyectos)
const quickActions = [
  {
    title: 'Nuevo Departamento',
    description: 'Registrar un nuevo departamento en la organización',
    icon: markRaw(Building2),
    iconBg: 'bg-gradient-to-br from-orange-400 to-orange-600',
    action: () => router.push('/departments?action=create')
  },
  {
    title: 'Nuevo Proyecto',
    description: 'Iniciar un nuevo proyecto global',
    icon: markRaw(FolderOpen),
    iconBg: 'bg-gradient-to-br from-zinc-600 to-zinc-800', // Elegante
    action: () => router.push('/projects?action=create')
  }
  // Eliminado "Add Team Member" como solicitaste
]

// Configuración de colores del Chart para el tema
const chartColors = {
  background: [
    'rgba(249, 115, 22, 0.8)', // Orange 500
    'rgba(220, 38, 38, 0.8)',  // Red 600
    'rgba(245, 158, 11, 0.8)', // Amber 500
    'rgba(82, 82, 91, 0.8)',   // Zinc 600
    'rgba(234, 88, 12, 0.8)',  // Orange 600
    'rgba(0, 0, 0, 0.7)'       // Black
  ],
  border: [
    'rgba(249, 115, 22, 1)',
    'rgba(220, 38, 38, 1)',
    'rgba(245, 158, 11, 1)',
    'rgba(82, 82, 91, 1)',
    'rgba(234, 88, 12, 1)',
    'rgba(0, 0, 0, 1)'
  ]
}

const chartData = ref({
  labels: [],
  datasets: [{
    label: 'Presupuesto',
    data: [],
    backgroundColor: chartColors.background,
    borderColor: chartColors.border,
    borderWidth: 1,
    borderRadius: 4
  }]
})

// Métodos
const fetchDashboardData = async () => {
  try {
    loading.value = true
    
    // 1. Stats
    const statsResponse = await api.get('/departments/stats')
    const statsData = statsResponse.data
    
    stats.value = [
      { 
        ...stats.value[0],
        value: statsData.totalDepartments?.toString() || '0',
        change: statsData.departmentsChange || '+0%'
      },
      { 
        ...stats.value[1],
        value: statsData.activeProjects?.toString() || '0',
        change: statsData.projectsChange || '+0%'
      },
      { 
        ...stats.value[2],
        value: `$${(statsData.totalBudget || 0).toLocaleString()}`,
        change: statsData.budgetChange || '+0%'
      },
      { 
        ...stats.value[3],
        value: statsData.teamMembers?.toString() || '0',
        change: statsData.teamChange || '+0%'
      }
    ]
    
    // 2. Chart Data
    const departmentsResponse = await api.get('/departments')
    const departments = departmentsResponse.data
    
    chartData.value.labels = departments.map(dept => dept.name)
    chartData.value.datasets[0].data = departments.map(dept => dept.budget || 0)
    
    // 3. Recent Activities (Mockeado o real)
    try {
      const activitiesResponse = await api.get('/activities/recent')
      recentActivities.value = (activitiesResponse.data || []).map(act => ({
        id: act.id,
        title: act.description || 'Actividad registrada',
        time: formatTimeAgo(act.createdAt || new Date().toISOString()),
        dotColor: getActivityDotColor(act.type)
      }))
    } catch (error) {
      console.warn('Activities endpoint failed, using fallback')
      recentActivities.value = []
    }
    
    await nextTick()
    createChart()
    
  } catch (error) {
    console.error('Error fetching dashboard data:', error)
    toastStore.showToast('Error cargando datos del dashboard', 'error')
  } finally {
    loading.value = false
  }
}

const createChart = () => {
  if (chartInstance) {
    chartInstance.destroy()
  }
  
  const ctx = budgetChart.value?.getContext('2d')
  if (!ctx) return
  
  // Detectar si es dark mode para ajustar color de texto del chart
  const isDark = document.documentElement.classList.contains('dark')
  const textColor = isDark ? '#e4e4e7' : '#374151'
  const gridColor = isDark ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)'

  chartInstance = new Chart(ctx, {
    type: chartType.value,
    data: chartData.value,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: chartType.value === 'pie',
          position: 'right',
          labels: { color: textColor }
        },
        tooltip: {
          backgroundColor: isDark ? '#18181b' : '#ffffff',
          titleColor: isDark ? '#ffffff' : '#000000',
          bodyColor: isDark ? '#a1a1aa' : '#4b5563',
          borderColor: '#f97316',
          borderWidth: 1,
          padding: 10
        }
      },
      scales: chartType.value === 'bar' ? {
        y: {
          beginAtZero: true,
          grid: { color: gridColor },
          ticks: {
            color: textColor,
            callback: (value) => '$' + value.toLocaleString()
          }
        },
        x: {
          grid: { display: false },
          ticks: { color: textColor }
        }
      } : {}
    }
  })
}

const changeChartType = (type) => {
  chartType.value = type
  createChart()
}

// Helpers visuales
const getActivityDotColor = (type) => {
  const map = {
    'DEPARTMENT_CREATED': 'bg-orange-500',
    'PROJECT_UPDATED': 'bg-green-500',
    'TEAM_MEMBER_ADDED': 'bg-blue-500',
    'REPORT_GENERATED': 'bg-purple-500'
  }
  return map[type] || 'bg-gray-400'
}

const formatTimeAgo = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  
  if (minutes < 1) return 'Ahora mismo'
  if (minutes < 60) return `Hace ${minutes} min`
  if (minutes < 1440) return `Hace ${Math.floor(minutes / 60)} horas`
  return `Hace ${Math.floor(minutes / 1440)} días`
}

onMounted(() => {
  fetchDashboardData()
  
  // Escuchar cambio de tema para repintar el gráfico
  const observer = new MutationObserver(() => {
    createChart()
  })
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
})
</script>

<style scoped>
/* Animación de entrada suave */
.animate-fade-in-up {
  animation: fadeInUp 0.5s ease-out forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Scrollbar personalizado delgado para la lista de actividades */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #52525b;
}
</style>