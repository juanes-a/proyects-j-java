import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)), // 👈 esto es lo que faltaba
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // URL de tu backend Spring Boot
        changeOrigin: true,
      },
    },
  },
})
