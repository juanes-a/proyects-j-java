<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
          {{ isEditing ? 'Edit Project' : 'Create Project' }}
        </h3>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors duration-200"
        >
          <X class="w-6 h-6" />
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Project Name *
            </label>
            <input
              v-model="form.name"
              type="text"
              required
              maxlength="100"
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500': errors.name
              }"
            />
            <p v-if="errors.name" class="text-red-500 text-sm mt-1">{{ errors.name }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Department *
            </label>
            <select
              v-model="form.departmentId"
              required
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500': errors.departmentId
              }"
            >
              <option :value="null">Select Department</option>
              <option 
                v-for="dept in departments" 
                :key="dept.id" 
                :value="dept.id"
              >
                {{ dept.name }}
              </option>
            </select>
            <p v-if="errors.departmentId" class="text-red-500 text-sm mt-1">{{ errors.departmentId }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Priority *
            </label>
            <select 
              v-model="form.priority" 
              required
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Status *
            </label>
            <select 
              v-model="form.status" 
              required
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
            >
              <option value="PLANNED">Planned</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Budget
            </label>
            <div class="relative">
              <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500">$</span>
              <input
                v-model.number="form.budget"
                type="number"
                min="0"
                step="0.01"
                class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600 pl-7"
                :class="{
                  'border-red-500 focus:border-red-500 focus:ring-red-500': errors.budget
                }"
                placeholder="0.00"
              />
            </div>
            <p v-if="errors.budget" class="text-red-500 text-sm mt-1">{{ errors.budget }}</p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Start Date *
            </label>
            <input
              v-model="form.startDate"
              type="date"
              required
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500': errors.startDate
              }"
            />
            <p v-if="errors.startDate" class="text-red-500 text-sm mt-1">{{ errors.startDate }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              End Date *
            </label>
            <input
              v-model="form.endDate"
              type="date"
              required
              :min="form.startDate"
              class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
              :class="{
                'border-red-500 focus:border-red-500 focus:ring-red-500': errors.endDate
              }"
            />
            <p v-if="errors.endDate" class="text-red-500 text-sm mt-1">{{ errors.endDate }}</p>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Description
          </label>
          <textarea
            v-model="form.description"
            rows="3"
            maxlength="500"
            class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
            placeholder="Brief description of the project..."
          ></textarea>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            {{ (form.description || '').length }}/500 characters
          </p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Objectives
          </label>
          <textarea
            v-model="form.objectives"
            rows="4"
            maxlength="1000"
            class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
            placeholder="Project objectives and goals..."
          ></textarea>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            {{ (form.objectives || '').length }}/1000 characters
          </p>
        </div>
        
        <div v-if="!isEditing || (isEditing && !project?.hasAssignees)">
          <h4 class="text-md font-semibold text-gray-800 dark:text-white mb-3 pt-4 border-t border-gray-200 dark:border-gray-700">Asignación de Manager (Opcional)</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Asignar Usuario (Username o Email)
              </label>
              <input
                v-model="form.assignedUserEmail"
                type="text"
                placeholder="user@example.com o username"
                class="block w-full rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:text-white border-gray-300 dark:border-gray-600"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Rol de Asignación
              </label>
              <p 
                class="block w-full rounded-md shadow-sm p-2.5 text-sm bg-gray-100 dark:bg-gray-700 dark:text-gray-300 border border-gray-300 dark:border-gray-600"
              >
                {{ formatRole(form.assignedRole) }}
              </p>
            </div>
          </div>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1" v-if="isEditing">
            Solo puedes asignar un Manager si el proyecto no tiene asignaciones previas.
          </p>
        </div>

        <div v-if="generalError" class="bg-red-50 border-l-4 border-red-500 p-4">
          <div class="flex">
            <div class="flex-shrink-0">
              <XCircle class="h-5 w-5 text-red-500" />
            </div>
            <div class="ml-3">
              <p class="text-sm text-red-700">{{ generalError }}</p>
            </div>
          </div>
        </div>

        <div class="flex space-x-3 pt-4">
          <button
            type="button"
            @click="$emit('close')"
            class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="loading"
            class="flex-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white rounded-lg transition-colors duration-200 flex items-center justify-center"
          >
            <Loader2 v-if="loading" class="w-4 h-4 animate-spin mr-2" />
            {{ isEditing ? 'Update Project' : 'Create Project' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { X, Loader2, XCircle } from 'lucide-vue-next'

const props = defineProps({
  project: Object,
  isEditing: Boolean,
  departments: Array
})

const emit = defineEmits(['close', 'save'])

const loading = ref(false)
const errors = ref({})
const generalError = ref('')

const FIXED_ROLE = 'MANAGER_PROYECTO'; // Usamos MANAGER_PROYECTO como rol fijo para la asignación

const form = reactive({
  name: '',
  description: '',
  objectives: '',
  departmentId: null,
  status: 'PLANNED',
  priority: 'MEDIUM',
  startDate: new Date().toISOString().split('T')[0], // Fecha actual por defecto
  endDate: '',
  budget: null,
  assignedUserEmail: '', 
  assignedRole: FIXED_ROLE // Se inicializa con el rol fijo
})

// Función para formatear fechas del backend
const formatDateFromBackend = (dateString) => {
  if (!dateString) return ''
  try {
    return new Date(dateString).toISOString().split('T')[0]
  } catch {
    return ''
  }
}

// Nueva función de ayuda para formatear el rol para mostrarlo en la UI
const formatRole = (role) => {
    const roleMap = {
        'MANAGER_PROYECTO': 'Manager de Proyecto',
        'COLABORADOR': 'Colaborador',
    }
    return roleMap[role] || role;
}

watch(() => props.project, (newProject) => {
  if (newProject) {
    Object.assign(form, {
      name: newProject.name || '',
      description: newProject.description || '',
      objectives: newProject.objectives || '',
      departmentId: newProject.department?.id || null,
      status: newProject.status || 'PLANNED',
      priority: newProject.priority || 'MEDIUM',
      startDate: formatDateFromBackend(newProject.startDate),
      endDate: formatDateFromBackend(newProject.endDate),
      budget: newProject.budget || null,
      // Se resetean los campos de asignación a sus valores por defecto/fijos
      assignedUserEmail: '', 
      assignedRole: FIXED_ROLE 
    })
  } else {
    // Reset form for new project
    Object.assign(form, {
      name: '',
      description: '',
      objectives: '',
      departmentId: null,
      status: 'PLANNED',
      priority: 'MEDIUM',
      startDate: new Date().toISOString().split('T')[0],
      endDate: '',
      budget: null,
      assignedUserEmail: '',
      assignedRole: FIXED_ROLE 
    })
  }
}, { immediate: true })

const validateForm = () => {
  errors.value = {}
  let isValid = true
  
  // Validación de nombre
  if (!form.name.trim()) {
    errors.value.name = 'Project name is required'
    isValid = false
  } else if (form.name.length < 2 || form.name.length > 100) {
    errors.value.name = 'Name must be between 2 and 100 characters'
    isValid = false
  }

  // Validación de departamento
  if (form.departmentId === null) {
    errors.value.departmentId = 'Department is required'
    isValid = false
  }

  // Validación de fechas
  if (!form.startDate) {
    errors.value.startDate = 'Start date is required'
    isValid = false
  }

  if (!form.endDate) {
    errors.value.endDate = 'End date is required'
    isValid = false
  } else if (form.startDate && new Date(form.endDate) <= new Date(form.startDate)) {
    errors.value.endDate = 'End date must be after start date'
    isValid = false
  }
  
  // Ya no se requiere validar assignedRole, ya que siempre es fijo

  return isValid
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  generalError.value = ''
  
  try {
    const projectData = {
      name: form.name.trim(),
      description: form.description.trim() || null,
      objectives: form.objectives.trim() || null,
      departmentId: form.departmentId,
      status: form.status,
      priority: form.priority,
      startDate: form.startDate,
      endDate: form.endDate,
      budget: form.budget !== null ? Number(form.budget) : null,
      // Se incluye el rol fijo en la data emitida para ProjectsView.vue
      assignedUserEmail: form.assignedUserEmail.trim() || null,
      assignedRole: FIXED_ROLE
    }
    
    console.log('Sending project data:', projectData)
    // Se emite el evento para que ProjectsView.vue maneje el guardado y la asignación
    emit('save', projectData)
  } catch (error) {
    console.error('Error saving project:', error)
    
    // Manejar errores de validación del backend
    if (error.response?.data) {
      const backendErrors = error.response.data
      
      if (Array.isArray(backendErrors)) {
        backendErrors.forEach(err => {
          if (err.propertyPath && err.message) {
            errors.value[err.propertyPath] = err.message
          }
        })
      } else if (typeof backendErrors === 'object') {
        for (const [field, message] of Object.entries(backendErrors)) {
          if (field !== 'message' && field !== 'error' && field !== 'timestamp') {
              errors.value[field] = message
          }
        }
      }
      
      generalError.value = error.response.data.message || 'Validation failed'
    } else {
      generalError.value = error.message || 'An unexpected error occurred'
    }
  } finally {
    loading.value = false
  }
}
</script>