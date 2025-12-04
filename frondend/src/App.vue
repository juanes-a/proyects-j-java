<template>
  <div id="app" class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
    <div v-if="isAuthRoute">
      <router-view />
    </div>

    <div v-else class="flex min-h-screen">
      <!-- Sidebar -->
      <Sidebar 
        @sidebar-state-change="handleSidebarChange" 
        class="fixed md:relative z-30"
      />
      
      <!-- Main Content -->
      <div 
        class="flex-1 min-h-screen transition-all duration-300"
        :class="{
          'md:ml-64': sidebarState.isOpen && !sidebarState.isMobile,
          'md:ml-16': !sidebarState.isOpen && !sidebarState.isMobile,
          'ml-0': sidebarState.isMobile
        }"
      >
        <!-- Top Navigation -->
        <TopNavigation :sidebar-open="sidebarState.isOpen" />
        
        <!-- Page Content -->
        <main class="p-4 sm:p-6">
          <router-view v-slot="{ Component }">
            <transition name="page" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </main>
      </div>
      
      <!-- Toast Notifications -->
      <ToastNotification />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import TopNavigation from './components/TopNavigation.vue'
import ToastNotification from './components/ToastNotification.vue'
import { useThemeStore } from './stores/theme'

const themeStore = useThemeStore()
const route = useRoute()

// Estado del sidebar
const sidebarState = ref({
  isOpen: true,
  isMobile: false
})

const isAuthRoute = computed(() => {
  return ['/', '/login', '/register', '/unauthorized'].includes(route.path)
})

const handleSidebarChange = (state) => {
  sidebarState.value = state
}

// Inicializar tema
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

.dark ::-webkit-scrollbar-track {
  background: #2d3748;
}

.dark ::-webkit-scrollbar-thumb {
  background: #4a5568;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: #718096;
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

/* Asegurar que las transiciones sean suaves */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
}
</style>