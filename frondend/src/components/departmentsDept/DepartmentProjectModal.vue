<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
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

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Basic Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Name -->
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

          <!-- Priority -->
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

          <!-- Status -->
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

          <!-- Budget -->
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

        <!-- Dates -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Start Date -->
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

          <!-- End Date -->
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

        <!-- Description -->
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

                <!-- User Assignment (Solo en creación) -->
        <div v-if="!isEditing">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Assign Project Admin (Username or Email)
          </label>
          <input
            v-model="form.assignedUser"
            type="text"
            placeholder="Enter admin's username or email"
            class="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          <p v-if="userAssignmentError" class="text-red-500 text-sm mt-1">{{ userAssignmentError }}</p>
        </div>

        <!-- Error message general -->
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

        <!-- Actions -->
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
import { ref, reactive, watch, onMounted } from 'vue'
import { X, Loader2, XCircle } from 'lucide-vue-next'

const props = defineProps({
  project: Object,
  isEditing: Boolean,
  departmentId: [Number, String]
})

console.log('🎭 MODAL DEBUG: Props recibidos:', props)
console.log('🏢 Department ID:', props.departmentId)
console.log('📝 Project:', props.project)
console.log('✏️ Is Editing:', props.isEditing)

const emit = defineEmits(['close', 'save'])

const loading = ref(false)
const errors = ref({})
const generalError = ref('')
const userAssignmentError = ref(null)

const form = reactive({
  name: '',
  description: '',
  objectives: '',
  departmentId: props.departmentId,
  status: 'PLANNED',
  priority: 'MEDIUM',
  startDate: new Date().toISOString().split('T')[0],
  endDate: '',
  budget: null,
  assignedUser: ''
})

console.log('📋 Form inicial:', form)

// ✅ Definir formatDateFromBackend ANTES de usarla
const formatDateFromBackend = (dateString) => {
  if (!dateString) return ''
  return dateString.split('T')[0]
}

// Inicializar el formulario correctamente
const initializeForm = () => {
  console.log('🔄 Inicializando formulario...')
  
  if (props.project && props.isEditing) {
    console.log('✏️ Modo edición - cargando datos del proyecto')
    Object.assign(form, {
      ...props.project,
      description: props.project.description || '',
      objectives: props.project.objectives || '',
      startDate: formatDateFromBackend(props.project.startDate),
      endDate: formatDateFromBackend(props.project.endDate),
      departmentId: props.departmentId
    })
  } else {
    console.log('🆕 Modo creación - formulario nuevo')
    Object.assign(form, {
      name: '',
      description: '',
      objectives: '',
      departmentId: props.departmentId,
      status: 'PLANNED',
      priority: 'MEDIUM',
      startDate: new Date().toISOString().split('T')[0],
      endDate: '',
      budget: null
    })
  }
  
  console.log('📋 Form después de inicializar:', form)
}

// Actualizar departmentId cuando cambie el prop
watch(() => props.departmentId, (newId) => {
  console.log('🔄 Department ID cambió a:', newId)
  form.departmentId = newId
}, { immediate: true })

// Actualizar el formulario cuando cambie el proyecto
watch(
  () => props.project,
  (newProject) => {
    console.log('🔄 Project cambió:', newProject)
    initializeForm()
  },
  { immediate: true }
)

const validateForm = () => {
  errors.value = {}
  let isValid = true
  userAssignmentError.value = null
  
  if (!form.name.trim()) {
    errors.value.name = 'Project name is required'
    isValid = false
  } else if (form.name.length < 2 || form.name.length > 100) {
    errors.value.name = 'Name must be between 2 and 100 characters'
    isValid = false
  }

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

  console.log('✅ Validación:', isValid ? 'EXITOSA' : 'FALLIDA', errors.value)
  return isValid
}

const handleSubmit = async () => {
  console.log('📤 Enviando formulario...')
  
  if (!validateForm()) {
    console.log('❌ Validación fallida')
    return
  }
  
  loading.value = true
  generalError.value = ''
  
  try {
  let userAssignment = null;
  if (!props.isEditing && form.assignedUser) {
    userAssignment = {
      usernameOrEmail: form.assignedUser
    }
  }

  const projectData = {
    name: form.name.trim(),
    description: form.description ? form.description.trim() || null : null,
    objectives: form.objectives ? form.objectives.trim() || null : null,
    departmentId: Number(form.departmentId),
    status: form.status,
    priority: form.priority,
    startDate: form.startDate,
    endDate: form.endDate,
    budget: form.budget !== null ? Number(form.budget) : null,
    userAssignment // ✅ ahora se asigna correctamente
  }
    
    console.log('📦 Datos a enviar:', projectData)
    
    emit('save', projectData)
    
  } catch (error) {
    console.error('❌ Error al procesar formulario:', error)
    
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
          errors.value[field] = message
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

onMounted(() => {
  console.log('🎭 Modal montado')
  initializeForm()
})
</script>