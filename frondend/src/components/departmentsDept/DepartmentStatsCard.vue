<template>
  <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm card-hover transition-all duration-300 border-l-4" :class="borderColor">
    <div class="flex items-center justify-between">
      <div class="flex-1">
        <p class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-1">{{ title }}</p>
        <div class="flex items-baseline space-x-2">
          <p v-if="!loading" class="text-2xl font-bold text-gray-900 dark:text-white">{{ value }}</p>
          <div v-else class="h-8 w-20 bg-gray-200 dark:bg-gray-700 rounded animate-pulse"></div>
        </div>
        <p v-if="change && !loading" class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ change }}</p>
      </div>
      <div :class="iconBgColor" class="w-12 h-12 rounded-lg flex items-center justify-center">
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
  icon: Object,
  color: String,
  loading: Boolean
})

const borderColor = computed(() => {
  const colors = {
    indigo: 'border-indigo-500',
    green: 'border-green-500',
    purple: 'border-purple-500',
    blue: 'border-blue-500',
    orange: 'border-orange-500',
    red: 'border-red-500'
  }
  return colors[props.color] || 'border-gray-500'
})

const iconBgColor = computed(() => {
  const colors = {
    indigo: 'bg-indigo-500',
    green: 'bg-green-500',
    purple: 'bg-purple-500',
    blue: 'bg-blue-500',
    orange: 'bg-orange-500',
    red: 'bg-red-500'
  }
  return colors[props.color] || 'bg-gray-500'
})
</script>
