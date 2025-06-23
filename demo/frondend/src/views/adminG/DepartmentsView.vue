<template>
  <div class="space-y-6">
    <!-- Header Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div
        v-for="stat in headerStats"
        :key="stat.title"
        class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm card-hover"
      >
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-600 dark:text-gray-400">{{ stat.title }}</p>
            <p class="text-2xl font-bold text-gray-800 dark:text-white">{{ stat.value }}</p>
          </div>
          <div :class="stat.iconBg" class="w-12 h-12 rounded-lg flex items-center justify-center">
            <component :is="stat.icon" class="w-6 h-6 text-white" />
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm">
      <!-- Table Header -->
      <div class="p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
          <div>
            <h2 class="text-xl font-semibold text-gray-800 dark:text-white">Departments</h2>
            <p class="text-sm text-gray-600 dark:text-gray-400">Manage your organization departments</p>
          </div>
          
          <div class="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-3">
            <!-- Search -->
            <div class="relative">
              <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search departments..."
                class="pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-800 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            
            <!-- Filter -->
            <select
              v-model="statusFilter"
              class="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-800 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="">All Status</option>
              <option value="active">Active</option>
              <option value="inactive">Inactive</option>
            </select>
            
            <!-- Add Button -->
            <button
              @click="openCreateModal"
              class="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
            >
              <Plus class="w-4 h-4" />
              <span>Add Department</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Table -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Department
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Budget
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Projects
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Status
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr
              v-for="department in filteredDepartments"
              :key="department.id"
              class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
            >
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                    <Building2 class="w-5 h-5 text-white" />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900 dark:text-white">{{ department.name }}</div>
                    <div class="text-sm text-gray-500 dark:text-gray-400">{{ department.location }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900 dark:text-white">
                  ${{ (department.budget || 0).toLocaleString() }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 dark:text-white">
                  {{ department.projects.length }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="department.status === 'active' ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'"
                  class="inline-flex px-2 py-1 text-xs font-semibold rounded-full"
                >
                  {{ department.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button
                    @click="viewDepartment(department)"
                    class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                  >
                    <Eye class="w-4 h-4" />
                  </button>
                  <button
                    @click="editDepartment(department)"
                    class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                  >
                    <Edit class="w-4 h-4" />
                  </button>
                  <button
                    @click="toggleDepartmentStatus(department)"
                    :class="department.status === 'active' ? 'text-orange-600 hover:text-orange-900 dark:text-orange-400 dark:hover:text-orange-300' : 'text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300'"
                  >
                    <Power class="w-4 h-4" />
                  </button>
                  <button
                    @click="deleteDepartment(department)"
                    class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Empty State -->
      <div v-if="filteredDepartments.length === 0" class="text-center py-12">
        <Building2 class="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">No departments found</h3>
        <p class="text-gray-500 dark:text-gray-400 mb-4">Get started by creating your first department.</p>
        <button
          @click="openCreateModal"
          class="inline-flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
        >
          <Plus class="w-4 h-4" />
          <span>Add Department</span>
        </button>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <transition name="fade">
      <DepartmentViewModal
        v-if="showViewModal"
        :department="selectedDepartment"
        @close="closeViewModal"
        @edit="handleEditFromView"
      />
    </transition>

    <transition name="fade">
      <DepartmentModal
        v-if="showModal"
        :department="selectedDepartment"
        :is-editing="isEditing"
        @close="closeModal"
        @save="handleSave"
      />
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Building2, DollarSign, TrendingUp, Search, Plus, Eye, Edit, Power, Trash2 } from 'lucide-vue-next'
import api from '../../api'
import DepartmentModal from '../../components/departmentsGlobal/DepartmentModal.vue'
import DepartmentViewModal from '../../components/departmentsGlobal/DepartmentViewModal.vue'
import { useToastStore } from '../../stores/toast'

const toastStore = useToastStore()

// Estados reactivos
const departments = ref([])
const loading = ref(true)
const searchQuery = ref('')
const statusFilter = ref('')
const showModal = ref(false)
const showViewModal = ref(false)
const selectedDepartment = ref(null)
const isEditing = ref(false)


// Métodos para manejar modales
const viewDepartment = (department) => {
  selectedDepartment.value = department
  showViewModal.value = true
}

const editDepartment = (department) => {
  selectedDepartment.value = department
  isEditing.value = true
  showModal.value = true
}

const handleEditFromView = (department) => {
  showViewModal.value = false  // Cierra el modal de vista
  editDepartment(department)   // Abre el modal de edición
}

const closeModal = () => {
  showModal.value = false
  selectedDepartment.value = null
}

const closeViewModal = () => {
  showViewModal.value = false
  selectedDepartment.value = null
}



// Estadísticas del encabezado (eliminada la de empleados)
const headerStats = ref([
  { title: 'Total Departments', value: '0', icon: Building2, iconBg: 'bg-blue-500' },
  { title: 'Active Departments', value: '0', icon: TrendingUp, iconBg: 'bg-green-500' },
  { title: 'Total Budget', value: '$0', icon: DollarSign, iconBg: 'bg-purple-500' }
])

// Departamentos filtrados
const filteredDepartments = computed(() => {
  let filtered = departments.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(dept =>
      dept.name.toLowerCase().includes(query) ||
      dept.location?.toLowerCase().includes(query)
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(dept => dept.status === statusFilter.value)
  }

  return filtered
})

// Métodos de la API
const fetchDepartments = async () => {
  try {
    loading.value = true
    const response = await api.get('/departments')
    departments.value = response.data.map(dept => ({
      id: dept.id,
      name: dept.name,
      description: dept.description,
      budget: dept.budget,
      location: dept.location,
      status: dept.isActive ? 'active' : 'inactive',
      projects: dept.projects || []
    }))
    updateStats()
  } catch (error) {
    console.error('Error fetching departments:', error)
    toastStore.showToast('Error loading departments', 'error')
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const response = await api.get('/departments/stats');
    const stats = response.data;
    
    // Asegurar valores por defecto en frontend también
    headerStats.value = [
      { 
        title: 'Total Departments', 
        value: stats.totalDepartments?.toString() || '0',
        icon: Building2, 
        iconBg: 'bg-blue-500' 
      },
      { 
        title: 'Active Departments', 
        value: stats.activeDepartments?.toString() || '0',
        icon: TrendingUp, 
        iconBg: 'bg-green-500' 
      },
      { 
        title: 'Total Budget', 
        value: `$${(stats.totalBudget || 0).toLocaleString()}`,
        icon: DollarSign, 
        iconBg: 'bg-purple-500' 
      }
    ];
    
  } catch (error) {
    console.error('Error fetching stats:', error);
    
    // Mostrar valores por defecto en caso de error
    headerStats.value = [
      { 
        title: 'Total Departments', 
        value: '0',
        icon: Building2, 
        iconBg: 'bg-blue-500' 
      },
      { 
        title: 'Active Departments', 
        value: '0',
        icon: TrendingUp, 
        iconBg: 'bg-green-500' 
      },
      { 
        title: 'Total Budget', 
        value: '$0',
        icon: DollarSign, 
        iconBg: 'bg-purple-500' 
      }
    ];
    
    // Opcional: Mostrar notificación al usuario
    toastStore.showToast('Error loading department stats. Using default values.', 'warning');
  }
};

const updateStats = () => {
  const total = departments.value.length
  const active = departments.value.filter(d => d.status === 'active').length
  const totalBudget = departments.value.reduce((sum, d) => sum + (d.budget || 0), 0)
  
  headerStats.value[0].value = total.toString()
  headerStats.value[1].value = active.toString()
  headerStats.value[2].value = `$${totalBudget.toLocaleString()}`
}

// Métodos para los modales
const openCreateModal = () => {
  selectedDepartment.value = {
    name: '',
    description: '',
    budget: 0,
    location: '',
    status: 'active',
    projects: []
  }
  isEditing.value = false
  showModal.value = true
}

// Métodos CRUD
const handleSave = async (departmentData) => {
  try {
    const payload = {
      name: departmentData.name,
      description: departmentData.description,
      budget: departmentData.budget,
      location: departmentData.location,
      isActive: departmentData.status === 'active'
    }

    if (isEditing.value) {
      await api.put(`/departments/${selectedDepartment.value.id}`, payload)
      toastStore.showToast('Department updated successfully', 'success')
    } else {
      await api.post('/departments', payload)
      toastStore.showToast('Department created successfully', 'success')
    }
    
    await fetchDepartments()
    closeModal()
  } catch (error) {
    console.error('Error saving department:', error)
    const errorMessage = error.response?.data?.message || 'Error saving department'
    toastStore.showToast(errorMessage, 'error')
  }
}

const toggleDepartmentStatus = async (department) => {
  try {
    const newStatus = department.status === 'active' ? 'inactive' : 'active'
    const endpoint = newStatus === 'active' ? 'activate' : 'deactivate'
    
    await api.patch(`/departments/${department.id}/${endpoint}`)
    department.status = newStatus
    updateStats()
    
    toastStore.showToast(
      `Department ${newStatus === 'active' ? 'activated' : 'deactivated'} successfully`, 
      'success'
    )
  } catch (error) {
    console.error('Error updating department status:', error)
    toastStore.showToast('Error updating department status', 'error')
  }
}

const deleteDepartment = async (department) => {
  if (!confirm(`Are you sure you want to delete ${department.name}?`)) return
  
  try {
    await api.delete(`/departments/${department.id}`)
    departments.value = departments.value.filter(d => d.id !== department.id)
    updateStats()
    toastStore.showToast('Department deleted successfully', 'success')
  } catch (error) {
    console.error('Error deleting department:', error)
    const errorMessage = error.response?.data?.message || 'Error deleting department'
    toastStore.showToast(errorMessage, 'error')
  }
}

// Inicialización
onMounted(() => {
  fetchDepartments()
  fetchStats()
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>