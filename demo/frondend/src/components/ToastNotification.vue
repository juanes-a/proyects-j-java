<template>
  <div class="fixed top-4 right-4 z-50 space-y-2">
    <transition-group name="toast" tag="div">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="toastClasses(toast.type)"
        class="flex items-center p-4 rounded-lg shadow-lg max-w-sm"
      >
        <component :is="getIcon(toast.type)" class="w-5 h-5 mr-3" />
        <p class="text-sm font-medium">{{ toast.message }}</p>
        <button
          @click="removeToast(toast.id)"
          class="ml-auto text-gray-400 hover:text-gray-600"
        >
          <X class="w-4 h-4" />
        </button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { CheckCircle, AlertCircle, XCircle, Info, X } from 'lucide-vue-next'
import { useToastStore } from '../stores/toast'

const toastStore = useToastStore()

const toasts = computed(() => toastStore.toasts)

const classes = {
  success: 'bg-green-50 border border-green-200 text-green-800',
  error: 'bg-red-50 border border-red-200 text-red-800',
  warning: 'bg-yellow-50 border border-yellow-200 text-yellow-800',
  info: 'bg-blue-50 border border-blue-200 text-blue-800'
}

const icons = {
  success: CheckCircle,
  error: XCircle,
  warning: AlertCircle,
  info: Info
}

const toastClasses = (type) => {
  return classes[type] || classes.info
}

const getIcon = (type) => {
  return icons[type] || Info
}

const removeToast = (id) => {
  toastStore.removeToast(id)
}
</script>

<style>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
