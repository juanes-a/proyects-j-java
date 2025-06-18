<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-4xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ title }}</h3>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors duration-200"
        >
          <X class="w-6 h-6" />
        </button>
      </div>

      <!-- Content -->
      <div class="p-6">
        <div v-if="projects.length === 0" class="text-center py-8">
          <FolderOpen class="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p class="text-gray-500 dark:text-gray-400">No projects found in this category.</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="project in projects"
            :key="project.id"
            class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors duration-200"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-3">
                <div class="w-10 h-10 bg-gradient-to-r from-green-500 to-blue-600 rounded-lg flex items-center justify-center">
                  <FolderOpen class="w-5 h-5 text-white" />
                </div>
                <div>
                  <h4 class="font-medium text-gray-900 dark:text-white">{{ project.name }}</h4>
                  <p class="text-sm text-gray-500 dark:text-gray-400">{{ project.department?.name }}</p>
                </div>
              </div>
              <div class="flex items-center space-x-4">
                <span :class="getStatusClass(project.status)" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                  {{ formatStatus(project.status) }}
                </span>
                <div class="flex items-center space-x-1">
                  <component :is="getPriorityIcon(project.priority)" :class="getPriorityColor(project.priority)" class="w-4 h-4" />
                  <span class="text-sm text-gray-600 dark:text-gray-400">{{ formatPriority(project.priority) }}</span>
                </div>
                <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatCurrency(project.budget) }}</span>
              </div>
            </div>
            <div class="mt-3 flex items-center justify-between text-sm text-gray-500 dark:text-gray-400">
              <span>{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</span>
              <span v-if="project.description" class="truncate max-w-xs">{{ project.description }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="flex justify-end p-6 border-t border-gray-200 dark:border-gray-700">
        <button
          @click="$emit('close')"
          class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors duration-200"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { FolderOpen, X, Circle, AlertCircle, AlertTriangle, Zap } from 'lucide-vue-next'

defineProps({
  projects: Array,
  title: String
})

defineEmits(['close'])

const formatStatus = (status) => {
  const statusMap = {
    'PLANNED': 'Planned',
    'IN_PROGRESS': 'In Progress',
    'COMPLETED': 'Completed',
    'CANCELLED': 'Cancelled'
  }
  return statusMap[status] || status
}

const formatPriority = (priority) => {
  const priorityMap = {
    'LOW': 'Low',
    'MEDIUM': 'Medium',
    'HIGH': 'High',
    'CRITICAL': 'Critical'
  }
  return priorityMap[priority] || priority
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

const formatCurrency = (amount) => {
  if (!amount) return '$0'
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const getStatusClass = (status) => {
  const classes = {
    'PLANNED': 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
    'IN_PROGRESS': 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
    'COMPLETED': 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    'CANCELLED': 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
  }
  return classes[status] || classes.PLANNED
}

const getPriorityIcon = (priority) => {
  const icons = {
    'LOW': Circle,
    'MEDIUM': AlertCircle,
    'HIGH': AlertTriangle,
    'CRITICAL': Zap
  }
  return icons[priority] || Circle
}

const getPriorityColor = (priority) => {
  const colors = {
    'LOW': 'text-green-500',
    'MEDIUM': 'text-yellow-500',
    'HIGH': 'text-orange-500',
    'CRITICAL': 'text-red-500'
  }
  return colors[priority] || 'text-gray-500'
}
</script>
