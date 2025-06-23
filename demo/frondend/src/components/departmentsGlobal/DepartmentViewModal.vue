<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
            <Building2 class="w-5 h-5 text-white" />
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ department?.name }}</h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">Department Details</p>
          </div>
        </div>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors duration-200"
        >
          <X class="w-6 h-6" />
        </button>
      </div>

      <!-- Content -->
      <div class="p-6 space-y-6">
        <!-- Basic Info -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Manager</label>
            <p class="text-gray-900 dark:text-white">{{ department?.manager || 'Not assigned' }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Location</label>
            <p class="text-gray-900 dark:text-white">{{ department?.location || 'Not specified' }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Budget</label>
            <p class="text-gray-900 dark:text-white font-semibold">${{ (department?.budget || 0).toLocaleString() }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Employee Count</label>
            <p class="text-gray-900 dark:text-white">{{ department?.employeeCount || 0 }} employees</p>
          </div>
        </div>

        <!-- Description -->
        <div v-if="department?.description">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Description</label>
          <p class="text-gray-900 dark:text-white">{{ department.description }}</p>
        </div>

        <!-- Status -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
          <span
            :class="department?.status === 'active' ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'"
            class="inline-flex px-3 py-1 text-sm font-semibold rounded-full"
          >
            {{ department?.status }}
          </span>
        </div>

        <!-- Projects Section -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">Projects</label>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="flex items-center space-x-2">
              <FolderOpen class="w-5 h-5 text-gray-500 dark:text-gray-400" />
                <router-link 
                  :to="{ path: '/projects', query: { department: department?.id } }"
                  class="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 text-sm font-medium"
                >
                  {{ activeProjectsCount }} active projects
                </router-link>
            </div>
          </div>
        </div>


        <!-- Statistics -->
        <div class="grid grid-cols-2 gap-4">
          <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
            <div class="flex items-center space-x-2">
              <TrendingUp class="w-5 h-5 text-blue-600 dark:text-blue-400" />
              <div>
                <p class="text-sm text-blue-600 dark:text-blue-400">Budget Utilization</p>
                <p class="text-lg font-semibold text-blue-800 dark:text-blue-300">75%</p>
              </div>
            </div>
          </div>
          <div class="bg-green-50 dark:bg-green-900/20 rounded-lg p-4">
            <div class="flex items-center space-x-2">
              <Users class="w-5 h-5 text-green-600 dark:text-green-400" />
              <div>
                <p class="text-sm text-green-600 dark:text-green-400">Team Performance</p>
                <p class="text-lg font-semibold text-green-800 dark:text-green-300">Excellent</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Footer -->
      <div class="flex justify-end space-x-3 p-6 border-t border-gray-200 dark:border-gray-700">
        <button
          @click="closeModal"
          class="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
        >
          Close
        </button>
        <button 
          @click="handleEdit"
          class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
        >
          Edit Department
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Building2, X, FolderOpen, TrendingUp, Users } from 'lucide-vue-next'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const props = defineProps({
  department: Object
})

const emit = defineEmits(['close', 'edit'])

const router = useRouter()
const activeProjectsCount = ref(0)

const closeModal = () => {
  emit('close')
}

const handleEdit = () => {
  emit('edit', props.department)  // Pasamos el departamento a editar
  closeModal()
}
// Cargar conteo de proyectos
async function fetchProjectCount(departmentId) {
  try {
    const response = await axios.get(`/api/departments/${departmentId}/projects/count`);
    activeProjectsCount.value = response.data.count;
  } catch (error) {
    console.error('Error fetching project count:', error);
    activeProjectsCount.value = 0;
  }
}

onMounted(() => {
  if (props.department?.id) {
    fetchProjectCount(props.department.id)
  }
})
</script>