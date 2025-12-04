import { defineStore } from "pinia"
import { ref } from "vue"

export const useToastStore = defineStore("toast", () => {
  const toasts = ref([])

  const showToast = (message, type = "info", duration = 5000) => {
    const id = Date.now()
    const toast = { id, message, type }

    toasts.value.push(toast)

    setTimeout(() => {
      removeToast(id)
    }, duration)
  }

  const removeToast = (id) => {
    const index = toasts.value.findIndex((toast) => toast.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }

  return {
    toasts,
    showToast,
    removeToast,
  }
})
