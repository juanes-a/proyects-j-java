<template>
  <div id="app">
    <div v-if="isAuthRoute">
      <router-view />
    </div>

    <div v-else class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
      <div class="flex">
        <!-- Sidebar -->
        <Sidebar />
        
        <!-- Main Content -->
        <div class="flex-1 ml-64 transition-all duration-300">
          <!-- Top Navigation -->
          <TopNavigation />
          
          <!-- Page Content -->
          <main class="p-6">
            <router-view v-slot="{ Component }">
              <transition name="page" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </main>
        </div>
      </div>
      
      <!-- Toast Notifications -->
      <ToastNotification />
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import TopNavigation from './components/TopNavigation.vue'
import ToastNotification from './components/ToastNotification.vue'
import { useThemeStore } from './stores/theme'

const themeStore = useThemeStore()
const route = useRoute()
const isAuthRoute = computed(() => {
  return ['/', '/login', '/register'].includes(route.path)
})

themeStore.initTheme()
</script>


<style>
/* Page transitions */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease;
}

.page-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Loading animation */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Card hover effects */
.card-hover {
  transition: all 0.3s ease;
}

.card-hover:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
</style>
