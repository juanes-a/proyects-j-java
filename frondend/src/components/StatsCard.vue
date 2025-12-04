<template>
  <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm card-hover transition-all duration-300">
    <div class="flex items-center justify-between">
      <div class="flex-1">
        <p class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-1">{{ title }}</p>
        <div class="flex items-baseline space-x-2">
          <p v-if="!loading" class="text-2xl font-bold text-gray-900 dark:text-white">{{ value }}</p>
          <div v-else class="h-8 w-20 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
          <span
            v-if="change && !loading"
            :class="change.startsWith('+') ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'"
            class="text-sm font-medium"
          >
            {{ change }}
          </span>
        </div>
      </div>
      <div :class="colorClasses" class="w-12 h-12 rounded-lg flex items-center justify-center">
        <component :is="icon" class="w-6 h-6 text-white" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  value: String,
  change: String,
  icon: [Object, Function], // Acepta tanto Object como Function
  color: String,
  loading: Boolean
})

const colorClasses = computed(() => {
  const colors = {
    blue: 'bg-blue-500',
    green: 'bg-green-500',
    purple: 'bg-purple-500',
    orange: 'bg-orange-500',
    red: 'bg-red-500'
  }
  return colors[props.color] || 'bg-gray-500'
})
</script>