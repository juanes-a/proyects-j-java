<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
          {{ isEditing ? 'Edit Department Project' : 'Create Department Project' }}
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
              class="w-full form-input"
              :class="{ 'border-red-500': errors.name }"
              placeholder="Enter project name..."
            />
            <p v-if="errors.name" class="text-red-500 text-sm mt-1">{{ errors.name }}</p>
          </div>

          <!-- Priority -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Priority
            </label>
            <select v-model="form.priority" class="w-full form-input">
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          <!-- Status -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Status
            </label>
            <select v-model="form.status" class="w-full form-input">
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
            <input
              v-model.number="form.budget"
              type="number"
              min="0"
              step="0.01"
              class="w-full form-input"
              :class="{ 'border-red-500': errors.budget }"
              placeholder="0.00"
            />
            <p v-if="errors.budget" class="text-red-500 text-sm mt-1">{{ errors.budget }}</p>
          </div>

          <!-- Team Members -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Team Members
            </label>
            <input
              v-model.number="form.teamMembers"
              type="number"
              min="0"
              class="w-full form-input"
              placeholder="Number of team members"
            />
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
              class="w-full form-input"
              :class="{ 'border-red-500': errors.startDate }"
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
              class="w-full form-input"
              :class="{ 'border-red-500': errors.endDate }"
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
            class="w-full form-input"
            placeholder="Brief description of the project..."
          ></textarea>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            {{ (form.description || '').length }}/500 characters
          </p>
        </div>

        <!-- Objectives -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Objectives
          </label>
          <textarea
            v-model="form.objectives"
            rows="4"
            maxlength="1000"
            class="w-full form-input"
            placeholder="Project objectives and goals..."
          ></textarea>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            {{ (form.objectives || '').length }}/1000 characters
          </p>
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
            class="flex-1 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white rounded-lg transition-colors duration-200 flex items-center justify-center"
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
import { X, Loader2 } from 'lucide-vue-next'

const props = defineProps({
  project: Object,
  isEditing: Boolean,
  departmentId: Number
})

const emit = defineEmits(['close', 'save'])

const loading = ref(false)
const errors = ref({})

const form = reactive({
  name: '',
  description: '',
  objectives: '',
  status: 'PLANNED',
  priority: 'MEDIUM',
  startDate: '',
  endDate: '',
  budget: null,
  teamMembers: null
})

// Watch for project changes
watch(() => props.project, (newProject) => {
  if (newProject) {
    Object.assign(form, {
      name: newProject.name || '',
      description: newProject.description || '',
      objectives: newProject.objectives || '',
      status: newProject.status || 'PLANNED',
      priority: newProject.priority || 'MEDIUM',
      startDate: newProject.startDate || '',
      endDate: newProject.endDate || '',
      budget: newProject.budget || null,
      teamMembers: newProject.teamMembers || null
    })
  } else {
    // Reset form for new project
    Object.assign(form, {
      name: '',
      description: '',
      objectives: '',
      status: 'PLANNED',
      priority: 'MEDIUM',
      startDate: '',
      endDate: '',
      budget: null,
      teamMembers: null
    })
  }
}, { immediate: true })

const validateForm = () => {
  errors.value = {}
  
  if (!form.name.trim()) {
    errors.value.name = 'Project name is required'
  } else if (form.name.length < 2 || form.name.length > 100) {
    errors.value.name = 'Project name must be between 2 and 100 characters'
  }
  
  if (!form.startDate) {
    errors.value.startDate = 'Start date is required'
  }
  
  if (!form.endDate) {
    errors.value.endDate = 'End date is required'
  }
  
  if (form.startDate && form.endDate && form.startDate >= form.endDate) {
    errors.value.endDate = 'End date must be after start date'
  }
  
  if (form.budget !== null && form.budget < 0) {
    errors.value.budget = 'Budget cannot be negative'
  }
  
  return Object.keys(errors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    const projectData = {
      name: form.name.trim(),
      description: form.description?.trim() || null,
      objectives: form.objectives?.trim() || null,
      departmentId: props.departmentId,
      status: form.status,
      priority: form.priority,
      startDate: form.startDate,
      endDate: form.endDate,
      budget: form.budget,
      teamMembers: form.teamMembers
    }
    
    await emit('save', projectData)
  } finally {
    loading.value = false
  }
}
</script>
