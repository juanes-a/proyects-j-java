<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl w-full max-w-md mx-4">
      <!-- Header -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200 dark:border-gray-700">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 bg-red-100 dark:bg-red-900/20 rounded-lg flex items-center justify-center">
            <AlertTriangle class="w-5 h-5 text-red-600 dark:text-red-400" />
          </div>
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Cancel Project</h3>
        </div>
        <button
          @click="$emit('close')"
          class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors duration-200"
        >
          <X class="w-6 h-6" />
        </button>
      </div>

      <!-- Content -->
      <div class="p-6">
        <p class="text-gray-700 dark:text-gray-300 mb-4">
          Are you sure you want to cancel the project <strong>"{{ project?.name }}"</strong>?
        </p>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Reason for cancellation (optional)
          </label>
          <textarea
            v-model="reason"
            rows="3"
            class="w-full form-input"
            placeholder="Please provide a reason for cancelling this project..."
          ></textarea>
        </div>

        <div class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-3 mb-4">
          <p class="text-sm text-red-800 dark:text-red-200">
            <strong>Warning:</strong> This action will mark the project as cancelled. You can still edit the project later if needed.
          </p>
        </div>
      </div>

      <!-- Footer -->
      <div class="flex space-x-3 p-6 border-t border-gray-200 dark:border-gray-700">
        <button
          @click="$emit('close')"
          class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors duration-200"
        >
          Keep Project
        </button>
        <button
          @click="handleConfirm"
          :disabled="loading"
          class="flex-1 px-4 py-2 bg-red-600 hover:bg-red-700 disabled:bg-red-400 text-white rounded-lg transition-colors duration-200 flex items-center justify-center"
        >
          <Loader2 v-if="loading" class="w-4 h-4 animate-spin mr-2" />
          Cancel Project
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { AlertTriangle, X, Loader2 } from 'lucide-vue-next'

const props = defineProps({
  project: Object
})

const emit = defineEmits(['close', 'confirm'])

const reason = ref('')
const loading = ref(false)

const handleConfirm = async () => {
  loading.value = true
  try {
    await emit('confirm', reason.value.trim() || null)
  } finally {
    loading.value = false
  }
}
</script>
