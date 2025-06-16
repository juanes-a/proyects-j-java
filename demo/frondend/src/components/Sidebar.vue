<template>
  <div class="fixed left-0 top-0 h-full w-64 bg-white dark:bg-gray-800 shadow-lg z-30 transition-colors duration-300">
    <!-- Logo Section -->
    <div class="flex items-center justify-center h-16 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center space-x-2">
        <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
          <Building2 class="w-5 h-5 text-white" />
        </div>
        <span class="text-xl font-bold text-gray-800 dark:text-white">DeptManager</span>
      </div>
    </div>
    
    <!-- Navigation Menu -->
    <nav class="mt-8">
      <div class="px-4 space-y-2">
        <router-link
          v-for="item in menuItems"
          :key="item.name"
          :to="item.path"
          class="flex items-center px-4 py-3 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200 group"
          :class="{ 'bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 border-r-2 border-blue-600': $route.path === item.path }"
        >
          <component :is="item.icon" class="w-5 h-5 mr-3" />
          <span class="font-medium">{{ item.name }}</span>
          <ChevronRight 
            v-if="$route.path === item.path" 
            class="w-4 h-4 ml-auto text-blue-600 dark:text-blue-400" 
          />
        </router-link>
      </div>
    </nav>
    
    <!-- Stats Section -->
    <div class="absolute bottom-4 left-4 right-4">
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg p-4 text-white">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm opacity-90">Total Departments</p>
            <p class="text-2xl font-bold">{{ totalDepartments }}</p>
          </div>
          <TrendingUp class="w-8 h-8 opacity-80" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Building2, Home, Users, FolderOpen, Settings, ChevronRight, TrendingUp } from 'lucide-vue-next'
import axios from 'axios'

const totalDepartments = ref(0)

const menuItems = [
  { name: 'Dashboard', path: 'homeDepartaments', icon: Home },
  { name: 'Departments', path: '/departments', icon: Building2 },
  { name: 'Projects', path: '/projects', icon: FolderOpen },
  { name: 'Team', path: '/team', icon: Users },
  { name: 'Settings', path: '/settings', icon: Settings }
]

onMounted(async () => {
  try {
    const response = await axios.get('/api/departments/stats')
    totalDepartments.value = response.data.total || 0
  } catch (error) {
    console.error('Error fetching stats:', error)
  }
})
</script>
