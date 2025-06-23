<template>
  <header class="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700 transition-colors duration-300">
    <div class="flex items-center justify-between px-6 py-4">
      <!-- Page Title -->
      <div>
        <h1 class="text-2xl font-semibold text-gray-800 dark:text-white">
          {{ pageTitle }}
        </h1>
        <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">
          {{ pageDescription }}
        </p>
      </div>
      
      <!-- Right Section -->
      <div class="flex items-center space-x-4">
        <!-- Theme Toggle -->
        <button
          @click="toggleTheme"
          class="p-2 rounded-lg bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors duration-200"
        >
          <Sun v-if="isDark" class="w-5 h-5 text-yellow-500" />
          <Moon v-else class="w-5 h-5 text-gray-600" />
        </button>
        
        <!-- Notifications -->
        <button class="relative p-2 rounded-lg bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors duration-200">
          <Bell class="w-5 h-5 text-gray-600 dark:text-gray-400" />
          <span class="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full"></span>
        </button>
        
        <!-- User Profile -->
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
            <User class="w-4 h-4 text-white" />
          </div>
          <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Admin User</span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Sun, Moon, Bell, User } from 'lucide-vue-next'
import { useThemeStore } from '../stores/theme'

const themeStore = useThemeStore()

// Cambiar a un tema específico
themeStore.setTheme('dark') // o 'light', 'system'

// Alternar entre claro/oscuro (manteniendo la lógica de sistema)
themeStore.toggleTheme()

// Acceder al estado actual
console.log(themeStore.isDark) // true/false
console.log(themeStore.theme)

const route = useRoute()
const isDark = computed(() => themeStore.isDark)

const pageTitle = computed(() => {
  const titles = {
    '/': 'Dashboard',
    '/departments': 'Departments Management',
    '/projects': 'Projects',
    '/team': 'Team Management',
    '/settings': 'Settings'
  }
  return titles[route.path] || 'Dashboard'
})

const pageDescription = computed(() => {
  const descriptions = {
    '/': 'Overview of your departments and projects',
    '/departments': 'Manage your organization departments',
    '/projects': 'Track and manage ongoing projects',
    '/team': 'Manage team members and roles',
    '/settings': 'Application settings and preferences'
  }
  return descriptions[route.path] || 'Welcome to DeptManager'
})

const toggleTheme = () => {
  themeStore.toggleTheme()
}
</script>
