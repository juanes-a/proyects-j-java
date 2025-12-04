<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-4xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex items-center space-x-3">
          <div class="w-12 h-12 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg flex items-center justify-center">
            <FolderOpen class="w-6 h-6 text-white" />
          </div>
          <div>
            <h3 class="text-xl font-semibold text-gray-900 dark:text-white">{{ project?.name }}</h3>
            <p class="text-sm text-gray-500 dark:text-gray-400">Department Project Details</p>
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
        <!-- Status and Priority Row -->
        <div class="flex items-center space-x-6">
          <div class="flex items-center space-x-2">
            <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Status:</span>
            <span :class="getStatusClass(project?.status)" class="inline-flex px-3 py-1 text-sm font-semibold rounded-full">
              {{ formatStatus(project?.status) }}
            </span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Priority:</span>
            <div class="flex items-center space-x-1">
              <component :is="getPriorityIcon(project?.priority)" :class="getPriorityColor(project?.priority)" class="w-4 h-4" />
              <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatPriority(project?.priority) }}</span>
            </div>
          </div>
        </div>

        <!-- Basic Info Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Start Date</label>
            <p class="text-gray-900 dark:text-white">{{ formatDate(project?.startDate) }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">End Date</label>
            <p class="text-gray-900 dark:text-white">{{ formatDate(project?.endDate) }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Budget</label>
            <p class="text-gray-900 dark:text-white font-semibold">{{ formatCurrency(project?.budget) }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Team Members</label>
            <p class="text-gray-900 dark:text-white">{{ project?.teamMembers || 0 }} members</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Created</label>
            <p class="text-gray-900 dark:text-white">{{ formatDateTime(project?.createdAt) }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Last Updated</label>
            <p class="text-gray-900 dark:text-white">{{ formatDateTime(project?.updatedAt) }}</p>
          </div>
        </div>

        <!-- Description -->
        <div v-if="project?.description">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Description</label>
          <div class="bg-indigo-50 dark:bg-indigo-900/20 rounded-lg p-4 border border-indigo-200 dark:border-indigo-800">
            <p class="text-gray-900 dark:text-white">{{ project.description }}</p>
          </div>
        </div>

        <!-- Objectives -->
        <div v-if="project?.objectives">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Objectives</label>
          <div class="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-4 border border-purple-200 dark:border-purple-800">
            <p class="text-gray-900 dark:text-white whitespace-pre-wrap">{{ project.objectives }}</p>
          </div>
        </div>

        <!-- Progress Timeline -->
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-4">Project Timeline</label>
          <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm text-gray-600 dark:text-gray-400">Progress</span>
              <span class="text-sm font-medium text-gray-900 dark:text-white">{{ getProgressPercentage() }}%</span>
            </div>
            <div class="w-full bg-gray-200 dark:bg-gray-600 rounded-full h-3">
              <div 
                class="h-3 rounded-full transition-all duration-300"
                :class="getProgressBarClass()"
                :style="{ width: `${getProgressPercentage()}%` }"
              ></div>
            </div>
            <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-2">
              <span>{{ formatDate(project?.startDate) }}</span>
              <span>{{ formatDate(project?.endDate) }}</span>
            </div>
          </div>
        </div>

        <!-- Department Project Statistics -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="bg-indigo-50 dark:bg-indigo-900/20 rounded-lg p-4 border border-indigo-200 dark:border-indigo-800">
            <div class="flex items-center space-x-2">
              <Calendar class="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              <div>
                <p class="text-sm text-indigo-600 dark:text-indigo-400">Duration</p>
                <p class="text-lg font-semibold text-indigo-800 dark:text-indigo-300">{{ getDuration() }} days</p>
              </div>
            </div>
          </div>
          <div class="bg-green-50 dark:bg-green-900/20 rounded-lg p-4 border border-green-200 dark:border-green-800">
            <div class="flex items-center space-x-2">
              <TrendingUp class="w-5 h-5 text-green-600 dark:text-green-400" />
              <div>
                <p class="text-sm text-green-600 dark:text-green-400">Days Remaining</p>
                <p class="text-lg font-semibold text-green-800 dark:text-green-300">{{ getDaysRemaining() }}</p>
              </div>
            </div>
          </div>
          <div class="bg-purple-50 dark:bg-purple-900/20 rounded-lg p-4 border border-purple-200 dark:border-purple-800">
            <div class="flex items-center space-x-2">
              <Users class="w-5 h-5 text-purple-600 dark:text-purple-400" />
              <div>
                <p class="text-sm text-purple-600 dark:text-purple-400">Team Size</p>
                <p class="text-lg font-semibold text-purple-800 dark:text-purple-300">{{ project?.teamMembers || 0 }} members</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="flex justify-end space-x-3 p-6 border-t border-gray-200 dark:border-gray-700">
        <button
          @click="$emit('close')"
          class="px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
        >
          Close
        </button>
        <button
          @click="$emit('edit', project)"
          class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors duration-200"
        >
          Edit Project
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { FolderOpen, X, Calendar, TrendingUp, Users, Circle, AlertCircle, AlertTriangle, Zap } from 'lucide-vue-next'

const props = defineProps({
  project: Object
})

defineEmits(['close', 'edit'])

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

const formatDateTime = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleString()
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

const getDuration = () => {
  if (!props.project?.startDate || !props.project?.endDate) return 0
  const start = new Date(props.project.startDate)
  const end = new Date(props.project.endDate)
  const diffTime = Math.abs(end - start)
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

const getDaysRemaining = () => {
  if (!props.project?.endDate) return 'N/A'
  const end = new Date(props.project.endDate)
  const today = new Date()
  const diffTime = end - today
  const days = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return days > 0 ? `${days} days` : 'Overdue'
}

const getProgressPercentage = () => {
  if (!props.project?.startDate || !props.project?.endDate) return 0
  
  const start = new Date(props.project.startDate)
  const end = new Date(props.project.endDate)
  const today = new Date()
  
  if (props.project.status === 'COMPLETED') return 100
  if (props.project.status === 'CANCELLED') return 0
  if (today < start) return 0
  if (today > end) return 100
  
  const totalDuration = end - start
  const elapsed = today - start
  return Math.round((elapsed / totalDuration) * 100)
}

const getProgressBarClass = () => {
  const percentage = getProgressPercentage()
  if (percentage === 100) return 'bg-green-500'
  if (percentage >= 75) return 'bg-indigo-500'
  if (percentage >= 50) return 'bg-blue-500'
  if (percentage >= 25) return 'bg-yellow-500'
  return 'bg-red-500'
}
</script>
